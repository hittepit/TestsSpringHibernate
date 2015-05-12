package be.fabrice.bidirectionnel.strange;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.FlushMode;
import org.hibernate.PropertyValueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.utils.MockFlushEntityListener;
import be.fabrice.utils.MockSessionFlushListener;
import be.fabrice.utils.TransactionalTestBase;
import be.fabrice.utils.logging.SimpleSql;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;

@ContextConfiguration(locations="classpath:bidirectionnel/strange/test-bidirectionnel-spring-strange.xml")
@Test(description="Manipulation dangereuse de relations bidirectionnelles",
		testName="Manipulation dangereuse de relations bidirectionnelles",
		suiteName="Relations bidirectionnelles")
public class TestBadMethodBehaviour extends TransactionalTestBase {
	@Autowired
	private MockSessionFlushListener mockSessionFlushListener;
	@Autowired
	private MockFlushEntityListener mockFlushEntityListener;
	
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
		
		mockSessionFlushListener.resetInvocation();
		mockFlushEntityListener.resetInvocation();
		
		flushMode = getSession().getFlushMode();
		getSession().setFlushMode(FlushMode.AUTO); //Garantit le flush mode
	}
	
	@AfterMethod
	public void restoreFlushMode(){
		getSession().setFlushMode(flushMode);
	}

	@Test(description="everything must work when new Item added, not persisted, then removed, and session flushed")
	public void noInsertionButRemovedThenFlushedIsOk(){
		Container c = (Container) getSession().get(Container.class, 1000);
		
		SimpleSql.reinitSqlList();
		
		Item newItem = new Item();
		newItem.setName("Nouveau");
		c.addItem(newItem);
		
		c.clearItems();
		
		assertThat(mockFlushEntityListener.getInvocation()).isEqualTo(0); //No entity flushed
		
		getSession().flush(); //Force Update DB
		
		assertThat(SimpleSql.contains("delete from item where .*")).isTrue();
		
		assertThat(getSession().createCriteria(Item.class).list()).isEmpty();
	}

	@Test(expectedExceptions=PropertyValueException.class, 
			description="no orphan delete must be casted when new Item is added, persisted because accidental flush, then removed")
	public void noOrphanDeleteWithAccidentalFlush(){
		Container c = (Container) getSession().get(Container.class, 1000);

		SimpleSql.reinitSqlList();
		
		Item newItem = new Item();
		newItem.setName("Nouveau");
		c.addItem(newItem);
		
		//flush "accidentel"
		getSession().createQuery("from Container t where t.name = :name").setParameter("name", "None").list();
		
		//Il y a bien eu un flush des 4 entités en session 
		assertThat(mockFlushEntityListener.getInvocation()).isEqualTo(4);
		
		//requêtes exécutées: le select qui provoque le flush + une insertion et rien d'autre
		assertThat(SimpleSql.getSqlList()).hasSize(2);
		assertThat(SimpleSql.contains("insert into item .*")).isTrue();
		assertThat(SimpleSql.contains("select .* from CONTAINER .*")).isTrue();
		
		c.clearItems();
		
		getSession().flush(); //Force update DB -> exception
	}
	
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
		
		assertThat(newItem.getId()).isNotNull(); //A été persisté
		assertThat(SimpleSql.contains("insert into item .*")).isTrue(); //Le preuve
		
		c.removeItem(newItem);
		
		assertThat(c.getItems()).hasSize(2); //Remove correct
		assertThat(newItem.getContainer()).isNull();
		
		getSession().flush(); //Force update DB -> exception
	}
	
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
		
		assertThat(SimpleSql.contains("delete from item .*")).isTrue();
		assertThat(SimpleSql.contains("update item .*")).isFalse();
	}
	
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
		
		//requêtes exécutées: le select qui provoque le flush + une insertion et rien d'autre
		assertThat(SimpleSql.getSqlList()).hasSize(2);
		assertThat(SimpleSql.contains("insert into item2 .*")).isTrue();
		assertThat(SimpleSql.contains("select .* from CONTAINER2 .*")).isTrue();
		
		c.clearItems();
		
		SimpleSql.reinitSqlList();
		
		getSession().flush(); //Force update DB -> exception
		
		System.out.println(SimpleSql.getSqlList());
		assertThat(SimpleSql.contains("delete .*")).isTrue();
		assertThat(SimpleSql.contains("update item2 .*"));

		List<Item2> items = getSession().createCriteria(Item2.class).list();
		
		assertThat(items).hasSize(1); //Le nouvel item n'a pas été supprimé
	}

	@Test(description="orphan delete must occure when new Item is added, persisted because manual flush, then removed")
	public void testBadMethodManualFlushNoUpdateOfContainer(){
		Container e = (Container) getSession().get(Container.class, 1000);
		
		Item newItem = new Item();
		newItem.setName("Nouveau");
		e.addItem(newItem);
		
		getSession().flush();
		
		e.clearItems();
		
		getSession().flush();
	}

	@Test(expectedExceptions=PropertyValueException.class)
	public void testBadMethodWithAccidentalWithNoUpdatesContainerEntityButManualWithdrawOfItem(){
		Container e = (Container) getSession().get(Container.class, 1000);

		SimpleSql.reinitSqlList();
		
		Item newItem = new Item();
		newItem.setName("Nouveau");
		e.addItem(newItem);
		
		getSession().createQuery("from Container t where t.name = :name").setParameter("name", "None").list();
		
		//Il y a bien eu un flush des 4 entités en session 
		assertThat(mockFlushEntityListener.getInvocation()).isEqualTo(4);
		
		//requêtes exécutées: le select qui provoque le flush + une insertion et rien d'autre
		assertThat(SimpleSql.getSqlList()).hasSize(2);
		assertThat(SimpleSql.contains("insert into ITEM .*")).isTrue();
		assertThat(SimpleSql.contains("select .* from CONTAINER .*")).isTrue();
		
		List<Item> ts = new ArrayList<Item>(e.getItems());
		for(Item t:ts){
			e.removeItem(t); //=e.clearItems()
		}
		
		getSession().flush(); //Force update DB -> exception
	}

	@Test
	public void testBadMethodWithAccidentalFlushThatUpdatesContainerEntity(){
		Container e = (Container) getSession().get(Container.class, 1000);
		
		Item newItem = new Item();
		newItem.setName("Nouveau");
		e.addItem(newItem);
		
		e.setName("other");

		getSession().createQuery("from Container t where t.name = :name").setParameter("name", "None").uniqueResult();
		
		//Il y a bien eu un flush des 4 entités en session -> une insertion, un update
		assertThat(mockFlushEntityListener.getInvocation()).isEqualTo(4);
		
		e.clearItems();
		
		getSession().flush(); //Force update DB
	}

	@Test
	public void testBadMethodManualFlushWithUpdateOfContainer(){
		Container e = (Container) getSession().get(Container.class, 1000);
		
		Item newItem = new Item();
		newItem.setName("Nouveau");
		e.addItem(newItem);
		
		e.setName("toto");
		
		getSession().flush();
		
		e.clearItems();
		
		getSession().flush();
	}

	@Test(expectedExceptions=PropertyValueException.class)
	public void testWithManualInsertOfItemAccidentalFlushNoUpdateOfContainer(){
		Container e = (Container) getSession().get(Container.class, 1000);
		
		Item newItem = new Item();
		newItem.setName("Nouveau");
		e.addItem(newItem);
		
		getSession().save(newItem);
		
		getSession().createQuery("from Container t where t.name = :name").setParameter("name", "None").uniqueResult();
		
		e.clearItems();
		
		getSession().flush();
	}

	@Test
	public void testWithManualInsertOfItemAccidentalFlushUpdateOfContainer(){
		Container e = (Container) getSession().get(Container.class, 1000);
		
		Item newItem = new Item();
		newItem.setName("Nouveau");
		e.addItem(newItem);
		
		getSession().save(newItem);
		
		e.setName("autre");
		
		getSession().createQuery("from Container t where t.name = :name").setParameter("name", "None").uniqueResult();
		
		e.clearItems();
		
		getSession().flush();
	}
	
	//TODO tester en vidant partiellement les Items les tests qui échouent
	//TODO tester en vidant proprement
}
