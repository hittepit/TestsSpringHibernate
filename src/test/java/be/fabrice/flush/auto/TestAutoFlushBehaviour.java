package be.fabrice.flush.auto;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.hibernate.FlushMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.utils.MockFlushEntityListener;
import be.fabrice.utils.MockSessionFlushListener;
import be.fabrice.utils.TransactionalTestBase;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;

/**
 * <p>L'auto-flush est déclenché lors d'un select (mais les selects obtenus via des get) 
 * afin de garantir la cohérence entre le modèle objet qui a peut-être été modifié et la base de données
 * telle que connue dans le contexte de la transaction (voir le test unitaire autoFlush).</p>
 * <p>Si ce mécanisme est fondamental, il est aussi gourmand en temps d'exécution puisque, derrière le flush, Hibernate
 * va faire du dirty-checking sur toutes les entités persistantes, même si elles n'ont pas été modifiées.</p>
 * <p>Il est particulièrement frustrant dans le cas de requêtes portant sur des objets qui, par design ou par utilisation,
 * ne sauraient pas avoir été modifiés dans le cadre de l'application. Ce mécanisme est alors inutile.</p>
 * <p>Il existe différentes manières d'empêcher le flush automatique dans ces cas en particulier</p>
 * 
 * @author fabrice.claes
 *
 */
@ContextConfiguration(locations="classpath:flush/auto/test-autoflush-spring.xml")
public class TestAutoFlushBehaviour extends TransactionalTestBase{
	@Autowired
	private LibelleDao libelleDao;
	@Autowired
	private MockSessionFlushListener mockSessionFlushListener;
	@Autowired
	private MockFlushEntityListener mockFlushEntityListener;

	@BeforeMethod
	public void initData(){
		Operation operations = sequenceOf(
				deleteAllFrom("SIMPLEENTITY","LIBELLE","IMMUTABLELIBELLE","DOSSIER"),
				insertInto("LIBELLE").columns("ID","LABEL").values(1,"Test").build(),
				insertInto("IMMUTABLELIBELLE").columns("ID","LABEL").values(1,"Test").build(),
				insertInto("SIMPLEENTITY").columns("ID","NAME")
					.values(1,"Nom1")
					.values(2,"Nom2")
					.build(),
				insertInto("DOSSIER").columns("ID","NOM","STATUT")
					.values(1,"NOUVEAU",'N')
					.values(2,"Prêt",'R')
					.values(3,"Traité",'P')
					.build()
				);
		
		DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), operations);
		dbSetup.launch();
		mockSessionFlushListener.resetInvocation();
		mockFlushEntityListener.resetInvocation();
	}
	
	/**
	 * <p>Ce test montre que l'auto-flush est déclenché par un select portant sur une entité ({@link Libelle})
	 * autre que les entités connues et éventuellement modifiées ({@link SimpleEntity}). Il est possible de montrer
	 * que l'auto-flush est indépendant du fait que les entités ont été ou non modifiées. D'ailleurs, dans ce test,
	 * deux entités sont flushées (les deux {@link SimpleEntity} connues de session) lors du premier select, 
	 * alors qu'aucune n'a été modifiée.</p>
	 * <p>Une modification est ensuite effectuée sur un {@link SimpleEntity} et une requête est effectuée sur le nouveau
	 * nom de cette entité. Si l'auto-flush n'avait pas eu lieu, l'entité récemment modifiée n'aurait pas été retrouvée,
	 * ce qui était incohérent.</p>
	 * <p>Ce dernier flush provoque le flush des trois entités connues à ce moment, l'update de l'entité modifiée, ce
	 * qui permet la récupération le l'entité.</p>
	 */
	@Test
	public void autoFlush(){
		SimpleEntity simpleEntity = (SimpleEntity) getSession().get(SimpleEntity.class, 1);
		getSession().get(SimpleEntity.class, 2);
		
		libelleDao.findByLabelStandard("Test");
		
		assertThat(mockSessionFlushListener.getInvocation()).isEqualTo(0);
		//Auto-flush, for each entity
		assertThat(mockFlushEntityListener.getInvocation()).isEqualTo(2);
		
		mockFlushEntityListener.resetInvocation();
		
		simpleEntity.setName("new");
		
		SimpleEntity newEntity = (SimpleEntity) getSession().createQuery("from SimpleEntity s where s.name=:name")
				.setParameter("name", "new").uniqueResult();
		
		assertThat(mockFlushEntityListener.getInvocation()).isEqualTo(3);
		
		//L'auto-flush permet de retrouver l'entité modifiée
		assertThat(newEntity).isSameAs(simpleEntity);
	}
	
	/**
	 * <p>Ce test montre que le fait que l'entité recherchée soit immtable (donc qu'aucune modification ne sera répercutée)
	 * n'empêche pas le flush. Hibernate ne s'intéresse pas au type d'entité recherché. Si un select doit être fait 
	 * (autre que via un session.get), il y aura un flush.</p>
	 * <p>Notons également que le deuxième flush porte sur trois entités, alors que la troisième ne peut être modifiée.
	 * Néanmoins, il n'y aura pas de dirty checking sur l'entité {@link ImmutableLibelle}.</p>
	 */
	@Test
	public void autoFlushWithImmutable(){
		SimpleEntity simpleEntity = (SimpleEntity) getSession().get(SimpleEntity.class, 1);
		getSession().get(SimpleEntity.class, 2);
		
		libelleDao.findImmutableByLabelStandard("Test");
		
		assertThat(mockSessionFlushListener.getInvocation()).isEqualTo(0);
		//Auto-flush, for each entity
		assertThat(mockFlushEntityListener.getInvocation()).isEqualTo(2);
		
		mockFlushEntityListener.resetInvocation();
		
		simpleEntity.setName("new");
		
		SimpleEntity newEntity = (SimpleEntity) getSession().createQuery("from SimpleEntity s where s.name=:name")
				.setParameter("name", "new").uniqueResult();
		
		assertThat(mockFlushEntityListener.getInvocation()).isEqualTo(3);
		
		//L'auto-flush permet de retrouver l'entité modifiée
		assertThat(newEntity).isSameAs(simpleEntity);
	}
	
	/**
	 * Ce test montre que le get ne provoque pas d'auto-flush. En effet, le select porte sur l'id, lequel ne peut pas
	 * être modifié depuis Hibernate.
	 */
	@Test
	public void noAutoFlushWithGet(){
		SimpleEntity simpleEntity = (SimpleEntity) getSession().get(SimpleEntity.class, 1);
		getSession().get(SimpleEntity.class, 2);
		
		simpleEntity.setName("new");
		
		libelleDao.find(1);
		
		assertThat(mockSessionFlushListener.getInvocation()).isEqualTo(0);
		//Pas d'auto-flush
		assertThat(mockFlushEntityListener.getInvocation()).isEqualTo(0);
	}
	
	/**
	 * <p>Un des moyens d'empêcher d'auto-flush est de déclarer une nouvelle transaction pour le select sur une
	 * entité, en particulier si elle ne risque pas d'avoir été modifiée.</p>
	 * <p>Il faut néanmoins être prudent car l'entité ainsi récupérée n'appartient pas à la même session que
	 * celle de la transaction principale. En fait, elle est même détachée, puisque sa session a été fermée à la fin
	 * de cette transaction spécifique.</p>
	 * <p>Le test montre néanmoins que, même si elle appartenait à une autre session, l'entité {@link Libelle}
	 * peut être attachée à la {@link SimpleEntity} et que ce changement sera persisté dès qu'il y aura un flush.</p>
	 * <p>Mais le flush ne portera pas sur le {@link Libelle} puisqu'il n'est pas encore connu de session au moment
	 * du flush.</p>
	 */
	@Test
	public void noAutoFlushWithNewTransaction(){
		SimpleEntity simpleEntity = (SimpleEntity) getSession().get(SimpleEntity.class, 1);
		
		Libelle libelle = libelleDao.findByLabelNewTransaction("Test");
		
		assertThat(mockSessionFlushListener.getInvocation()).isEqualTo(0);
		assertThat(mockFlushEntityListener.getInvocation()).isEqualTo(0);
		
		simpleEntity.setLibelle(libelle);
		
		SimpleEntity editedEntity = (SimpleEntity) getSession().createQuery("from SimpleEntity s where s.libelle = :l")
				.setParameter("l", libelle).uniqueResult();
		//Cette requête a provoqué un autoflush, sur une seule entité
		assertThat(mockSessionFlushListener.getInvocation()).isEqualTo(0);
		assertThat(mockFlushEntityListener.getInvocation()).isEqualTo(1);
		
		assertThat(simpleEntity).isSameAs(editedEntity);
	}

	/**
	 * <p>La meilleure manière de faire les choses est de changer le flush mode de la query qui porte sur l'entité
	 * qui ne saurait être modifiée: query.setFlushMode(FlushMode.MANUAL) ou query.setFlushMode(FlushMode.COMMIT)
	 * qui a l'avantage de ne porter que sur cette requête.</p>
	 * <p>Le test montre d'ailleurs que le flush mode reste identique pour le reste (c'est-à-dire AUTO),
	 * ce qui permet de garantir la cohérence.</p>
	 */
	@Test
	public void noAutoFlushIfForcedToManual(){
		SimpleEntity simpleEntity1 = (SimpleEntity) getSession().get(SimpleEntity.class, 1);
		
		simpleEntity1.setName("new");
		
		libelleDao.findByLabelFlushModeManual("Test");
		
		assertThat(mockSessionFlushListener.getInvocation()).isEqualTo(0);
		assertThat(mockFlushEntityListener.getInvocation()).isEqualTo(0);
		
		//Mais le flush se fait sur d'autre requête
		SimpleEntity newEntity = (SimpleEntity) getSession().createQuery("from SimpleEntity s where s.name=:name")
				.setParameter("name", "new").uniqueResult();
		
		//Cette requête a provoqué un flush des deux entités connues
		assertThat(mockFlushEntityListener.getInvocation()).isEqualTo(2);
		//Ce qui lui permet de retrouver l'entité modifiée
		assertThat(newEntity).isSameAs(simpleEntity1);
	}
	
	@Test
	public void whatIfNoAutoFlush(){
		getSession().setFlushMode(FlushMode.COMMIT);
		
		List<Dossier> nouveauxDossiers = getSession().createQuery("from Dossier d where d.statut = :statut")
				.setParameter("statut", 'N').list();
		
		for(Dossier d:nouveauxDossiers){
			//Evaluation des nouveaux dossiers et s'il est prêt -> R
			d.setStatut('R');
			getSession().saveOrUpdate(d); //Ca ne sert à rien, mais beaucoup de développeurs en sont convaincus
		}
		
		List<Dossier> dossiersATraiter = getSession().createQuery("from Dossier d where d.statut = :statut")
				.setParameter("statut", 'R').list();

		assertThat(dossiersATraiter).as("Il n'y en a qu'un en ready").hasSize(1);
 
		
		for(Dossier d:dossiersATraiter){
			//Traitement du dossier -> R
			d.setStatut('P');
			getSession().saveOrUpdate(d); //Ca ne sert à rien, mais beaucoup de développeurs en sont convaincus
		}
		
		getSession().flush();
		
		
		List<Dossier> dossiersTraites = getSession().createQuery("from Dossier d where d.statut = :statut")
				.setParameter("statut", 'P').list();
		
		assertThat(dossiersTraites).as("Deux seulement alors que tous devraient être traités").hasSize(2);
		
		getSession().setFlushMode(FlushMode.AUTO);
	}
	
	@Test
	public void whatIfWithAutoFlush(){
		List<Dossier> nouveauxDossiers = getSession().createQuery("from Dossier d where d.statut = :statut")
				.setParameter("statut", 'N').list();
		
		for(Dossier d:nouveauxDossiers){
			//Evaluation des nouveaux dossiers et s'il est prêt -> R
			d.setStatut('R');
			getSession().save(d); //Ca ne sert à rien, mais beaucoup de développeurs en sont convaincus
		}
		
		List<Dossier> dossiersATraiter = getSession().createQuery("from Dossier d where d.statut = :statut")
				.setParameter("statut", 'R').list();
 
		assertThat(dossiersATraiter).as("Il y en a bien en ready").hasSize(2);
		
		for(Dossier d:dossiersATraiter){
			//Traitement du dossier -> R
			d.setStatut('P');
			getSession().save(d); //Ca ne sert à rien, mais beaucoup de développeurs en sont convaincus
		}
		
		getSession().flush();
		
		
		List<Dossier> dossiersTraites = getSession().createQuery("from Dossier d where d.statut = :statut")
				.setParameter("statut", 'P').list();
		
		assertThat(dossiersTraites).as("Ici, les trois ont été traités").hasSize(3);
	}
}
