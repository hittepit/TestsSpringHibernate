package be.fabrice.bidirectionnel.strange;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.assertj.core.api.Assertions.assertThat;

import org.hibernate.FlushMode;
import org.hibernate.PropertyValueException;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.utils.MockFlushEntityListener;
import be.fabrice.utils.TransactionalTestBase;
import be.fabrice.utils.logging.SimpleSql;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;

/**
 * <p>Ces tests ont pour objectif d'explorer un comportement erroné d'Hibernate dans le cas d'une relation bidirectionnelle
 * many-to-one one-to-many et où le delete orphan est pris en défaut.</p>
 * 
 * <p>Lorsqu'un enfant persistant est séparé d'un parent persistant (simplement en cassant la relation biridrectionnelle,
 * c'est-à-dire en retirant l'enfant de la collection d'enfants du parent et en settant la référence vers la parent à null
 * dans l'enfant), Hibernate procède à un orphan delete.</p>
 * 
 * <p>Le cas identifié ici arrive lorsqu'un enfant transient est ajouté à un parent persistant, 
 * puis que la session est flushée à cause, par exemple, d'un select, ce qui provoque par cascading la persistance
 * de l'enfant. Mais enfant, l'enfant est détaché du parent. Lors du flush final, Hibernate ne supprime pas l'enfant détaché
 * malgré le deleteOrphan, mais essaye plutôt de mettre à jour la colonne de la foreign key vers parent à null.</p>
 * 
 * <p>La situation explorée est celle d'un {@link Container} qui contient plusieurs {@link Item}. La relation étant
 * bidirectionnelle, chaque {@link Item} appartenant au {@link Container} a une référence vers celui-ci. Il y a du cascading
 * sur la collection d'Items et un deleteOrphan. De plus Item doit avoir une référence vers un Container (la colonne de 
 * la foreign key est not nullable).</p>
 * 
 * <p>Le modèle ({@link Container2} et {@link Item2})2 diffère peu du précédent à l'exception d'une contrainte
 * d'intégrité en moins: la colonne de la foreign key vers le {@link Container2} peut être nulle. Ce modèle sert à
 * démontrer ce qu'Hibernate fait en fait.</p>
 * 
 * <p>Sauf indication contraire, toutes les relations bidirectonnelles sont correctement gérées. </p>
 * 
 * @author fabrice.claes
 *
 */
@ContextConfiguration(locations="classpath:bidirectionnel/strange/test-bidirectionnel-spring-strange.xml")
@Test(description="Manipulation dangereuse de relations bidirectionnelles",
		testName="Manipulation dangereuse de relations bidirectionnelles",
		suiteName="Relations bidirectionnelles")
public class TestWrongHibernateBehaviour extends TransactionalTestBase {
	/**
	 * Listener destiné à enregistrer les flush exécutés sur la session
	 */
	@Autowired
	private MockFlushEntityListener mockFlushEntityListener;
	
	/**
	 * Certains tests pourraient modifier l'impact du FlushMode, 
	 * il faut donc le conserver en début de test et le rétablir à la fin
	 */
	private FlushMode flushMode;
	
	@BeforeMethod
	public void initTestData(){
		Operation operations = sequenceOf(
				deleteAllFrom("ITEM","CONTAINER","ITEM2","CONTAINER2"),
				insertInto("CONTAINER").columns("ID","NAME").values(1000,"Container1").build(),
				insertInto("ITEM").columns("ID","NAME","C_FK")
					.values(1001,"Item 1",1000)
					.values(1002,"Item 2",1000)
					.build(),
				insertInto("CONTAINER2").columns("ID","NAME").values(1000,"Container1").build(),
				insertInto("ITEM2").columns("ID","NAME","C_FK")
					.values(1001,"Item 1",1000)
					.values(1002,"Item 2",1000)
					.build()
		);
		
		DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), operations);
		dbSetup.launch();
		
		mockFlushEntityListener.resetInvocation();
		
		flushMode = getSession().getFlushMode();
		getSession().setFlushMode(FlushMode.AUTO); //Garantit le flush mode
	}
	
	@AfterMethod
	public void restoreFlushMode(){
		getSession().setFlushMode(flushMode);
	}

	/**
	 * Démonstration que le orphanDelete fonctionne correctement dans une situation "normale"
	 */
	@Test(description="orphan items are deleted")
	public void orphanItemsAreRemovedThenFlushed(){
		Container c = (Container) getSession().get(Container.class, 1000);
		
		SimpleSql.reinitSqlList();
		
		c.clearItems();
		
		assertThat(mockFlushEntityListener.getInvocation()).isEqualTo(0); //No entity flushed
		
		getSession().flush(); //Force Update DB
		
		assertThat(SimpleSql.contains("delete from item where .*")).isTrue();
		
		assertThat(getSession().createQuery("from Item i where i.container = :c")
				.setParameter("c",c).list()).isEmpty();
	}

	/**
	 * Démontration du cas de base. Un item est créé, ajouté, persisté par un flush "accidentel", puis rendu orphelin (détaché).
	 * Plutôt que de faire un delete, Hibernate essaye plutôt de le mettre à jour, mais comme la foreign key
	 * est not nullable, une exception est lancée.
	 */
	@Test(expectedExceptions=PropertyValueException.class, 
			description="hibernate does not make orphan delete of a new entity that was persisted by accidental flush then removed")
	public void noOrphanDelete(){
		Container c = (Container) getSession().get(Container.class, 1000);
		
		Item newItem = new Item();
		newItem.setName("Nouveau");
		c.addItem(newItem);
		
		SimpleSql.reinitSqlList();
		
		//flush "accidentel"
		getSession().createQuery("from Container t where t.name = :name").setParameter("name", "None").list();
		
		assertThat(newItem.getId()).isNotNull(); //L'item a été persisté
		assertThat(SimpleSql.contains("insert into item .*")).isTrue(); //La preuve
		
		c.removeItem(newItem);
		
		assertThat(c.getItems()).hasSize(2); //Remove correct
		assertThat(newItem.getContainer()).isNull();
		
		getSession().flush(); //Force update DB -> exception
	}

	/**
	 * <p>Dans le test précédent, l'exception vient du fait que la foreign key ne peut être nulle. Si on lève cette
	 * contrainte, on voit qu'en fait Hibernate, plutôt que de faire un delete du nouvel item, fait juste un update
	 * en settant la foreign key à null.</p>
	 * 
	 * <p>Dans ce test, on montre aussi que les items "normaux" sont par contre supprimés.</p>
	 */
	@Test(description="no orphan delete must be casted when new Item is added, persisted because accidental flush, then removed")
	public void noOrphanDeleteWithAccidentalFlushOnModel2(){
		Container2 c = (Container2) getSession().get(Container2.class, 1000);

		SimpleSql.reinitSqlList();
		
		Item2 newItem = new Item2();
		newItem.setName("Nouveau");
		c.addItem(newItem);
		
		//flush "accidentel"
		getSession().createQuery("from Container2 t where t.name = :name").setParameter("name", "None").list();
		
		//Il y a bien eu un flush des 4 entités en session 
		assertThat(mockFlushEntityListener.getInvocation()).isEqualTo(4);
		
		//requêtes exécutées: le select qui provoque le flush, une insertion du nouvel item et rien d'autre
		assertThat(SimpleSql.contains("select .* from CONTAINER2 .*")).isTrue();
		assertThat(SimpleSql.contains("insert into item2 .*")).isTrue();
		assertThat(SimpleSql.getSqlList()).hasSize(2);
		
		c.clearItems();
		
		SimpleSql.reinitSqlList();
		
		getSession().flush(); //Force update DB -> pas d'exception cette fois
		
		assertThat(SimpleSql.contains("delete .*")).isTrue(); //les autres items ont été supprimés
		assertThat(SimpleSql.contains("update item2 .*")).isTrue(); //le nouvel item a été mis à jour

		//Il n'y a plus d'items attachés au container
		assertThat(getSession().createQuery("from Item2 i where i.container = :c")
				.setParameter("c",c).list()).isEmpty();
		
		//Mais le nouvel item est toujours en DB
		Item2 item = (Item2) getSession().createCriteria(Item2.class)
				.add(Restrictions.eq("name", "Nouveau")).uniqueResult();
		
		assertThat(item).isNotNull(); //Le nouvel item n'a pas été supprimé
		assertThat(item.getContainer()).isNull(); //mais il ne contient plus de container
	}
	
	/**
	 * Par contre, si on delete l'item juste après l'avoir séparé du container, il n'y a plus de problème.
	 */
	@Test(description="hibernate does not make orphan delete of a new entity but manual delete works")
	public void manualDeleteOfAnOrphan(){
		Container c = (Container) getSession().get(Container.class, 1000);

		Item newItem = new Item();
		newItem.setName("Nouveau");
		c.addItem(newItem);
		
		SimpleSql.reinitSqlList();
		
		//flush "accidentel"
		getSession().createQuery("from Container t where t.name = :name").setParameter("name", "None").list();
		
		assertThat(newItem.getId()).isNotNull(); //A été persisté
		assertThat(SimpleSql.contains("insert into item .*")).isTrue(); //Le preuve
		
		c.removeItem(newItem);
		
		assertThat(c.getItems()).hasSize(2); //Remove correct
		assertThat(newItem.getContainer()).isNull();
		
		SimpleSql.reinitSqlList();
		
		getSession().delete(newItem);
		
		getSession().flush(); //Force update DB -> plus d'exception
		
		assertThat(SimpleSql.contains("delete from item .*")).isTrue(); //Un delete a été fait
		assertThat(SimpleSql.contains("update item .*")).isFalse(); //Aucun update n'a été fait
	}

	/**
	 * <p>Le problème ne survient qu'en cas de flush "accidentel", c'est-à-dire si un select requiert un flush préalable.</p>
	 * 
	 * <p>Dans ce test, il n'y a aucun problème si le flush est forcé après l'ajout de l'item.</p>
	 */
	@Test(description="orphan delete must occur when new Item is added, persisted because manual flush, then removed")
	public void orphanDeleteOccursWithManualFlush(){
		Container c = (Container) getSession().get(Container.class, 1000);
		
		Item newItem = new Item();
		newItem.setName("Nouveau");
		c.addItem(newItem);
		
		getSession().flush(); //Flush manuel qui provoque l'insertion
		
		assertThat(newItem.getId()).isNotNull(); //L'item a été persisté
		assertThat(SimpleSql.contains("insert into item .*")).isTrue(); //La preuve
		
		c.clearItems();
		
		getSession().flush(); //on force le flush
		
		//Le delete ophan a fonctionné
		assertThat(getSession().createQuery("from Item i where i.container = :c")
				.setParameter("c",c).list()).isEmpty();
	}

	/**
	 * Assez curieusement, si un update du container est fait (par flush accidentel), le deleteOrphan se comporte
	 * normalement. 
	 */
	@Test
	public void orphanDeleteOccursIfDirtyContainerHasBeenFlushedWithNewItem(){
		Container c = (Container) getSession().get(Container.class, 1000);
		
		SimpleSql.reinitSqlList();
		
		Item newItem = new Item();
		newItem.setName("Nouveau");
		c.addItem(newItem);
		
		c.setName("other");

		getSession().createQuery("from Container t where t.name = :name").setParameter("name", "None").uniqueResult();
		
		//Il y a bien eu un flush des 4 entités en session -> une insertion, un update
		assertThat(mockFlushEntityListener.getInvocation()).isEqualTo(4);
		
		assertThat(SimpleSql.contains("select .* from CONTAINER .*")).isTrue();
		assertThat(SimpleSql.contains("insert into item .*")).isTrue();
		assertThat(SimpleSql.contains("update container .*")).isTrue();
		
		c.clearItems();
		
		SimpleSql.reinitSqlList();
		
		getSession().flush(); //Force update DB
		
		assertThat(SimpleSql.contains("delete from item where .*")).isTrue(); //Des suppressions d'items
		assertThat(SimpleSql.contains("update item .*")).isFalse(); //Pas d'update
		
		assertThat(getSession().createQuery("from Item i where i.container = :c")
				.setParameter("c",c).list()).isEmpty(); //Tous les items ont été supprimés
	}

	/**
	 * Essayer de persister manuellement l'item ajouté avant le flush accidentel n'arrange pas le problème.
	 */
	@Test(expectedExceptions=PropertyValueException.class)
	public void manualPersistOfNewItemDoesNotSolveTheProblem(){
		Container e = (Container) getSession().get(Container.class, 1000);
		
		SimpleSql.reinitSqlList();
		
		Item newItem = new Item();
		newItem.setName("Nouveau");
		e.addItem(newItem);
		
		getSession().save(newItem);
		
		assertThat(newItem.getId()).isNotNull(); //L'item a bien été persisté
		assertThat(SimpleSql.contains("insert into item .*")).isTrue(); //avec cette requête
		
		getSession().createQuery("from Container t where t.name = :name").setParameter("name", "None").uniqueResult();
		
		e.clearItems();
		
		getSession().flush(); //boum
	}
}
