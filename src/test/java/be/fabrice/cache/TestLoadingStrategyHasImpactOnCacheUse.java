package be.fabrice.cache;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.stat.SecondLevelCacheStatistics;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import com.ninja_squad.dbsetup.operation.Insert.Builder;

import be.fabrice.cache.entity.Cat;
import be.fabrice.cache.entity.Civilite;
import be.fabrice.cache.entity.EtatCivil;
import be.fabrice.cache.entity.Owner;
import be.fabrice.cache.entity.Personne;
import be.fabrice.cache.entity.Situation;
import be.fabrice.cache.entity.Statut;
import be.fabrice.utils.TransactionalTestBase;

@Test(testName="Impact du lazy-loading sur l'efficacité du cache", suiteName="Cache de second niveau")
@ContextConfiguration(locations="classpath:cache/test-cache-spring.xml")
public class TestLoadingStrategyHasImpactOnCacheUse extends TransactionalTestBase {
	private SecondLevelCacheStatistics etatStats;
	private SecondLevelCacheStatistics statutStats;
	private SecondLevelCacheStatistics sitStats;
	private SecondLevelCacheStatistics civStats;
	private SecondLevelCacheStatistics ownerStats;
	
	@BeforeClass
	public void beforeClass(){
		etatStats = sessionFactory.getStatistics().getSecondLevelCacheStatistics("ETAT");
		statutStats = sessionFactory.getStatistics().getSecondLevelCacheStatistics("STATUT");
		sitStats = sessionFactory.getStatistics().getSecondLevelCacheStatistics("SIT");
		civStats = sessionFactory.getStatistics().getSecondLevelCacheStatistics("CIV");
		ownerStats = sessionFactory.getStatistics().getSecondLevelCacheStatistics("OWNER");
		
		Builder insertOwnerBuilder = insertInto("OW").columns("ID","NAME");
		Builder insertCatBuilder = insertInto("CAT").columns("ID","NAME","OW_ID");
		Builder insertStatut = insertInto("STATUT")
				.columns("ID","CODE","LIBELLE")
				.values(1000,"S","Salarié")
				.values(2000,"C","Sans emploi");
		Builder insertEtat = insertInto("ETAT")
				.columns("ID","CODE","LIBELLE")
				.values(1,"M","Marié")
				.values(2,"C","Célibataire");
		Builder insertCivilite = insertInto("CIV")
				.columns("ID","CODE","NOM")
				.values(1,"M","Monsieur")
				.values(2,"Mme","Madame");
		Builder insertSituation = insertInto("SIT").columns("ID","ENFANTS").values(2001,0);
		Builder insertPersonnes = insertInto("PERS").columns("ID","NOM","STATUT_ID","ETAT_ID","SIT_ID","CIV_ID")
				.values(1001,"First",1000,2,2001,1);
		
		for(int i=1;i <= 3; i++){
			insertOwnerBuilder = insertOwnerBuilder.values(i,"Owner"+i);
			for(int j=1; j<=5; j++){
				insertCatBuilder = insertCatBuilder.values(i*10+j,"Cat"+(i*10+j),i);
			}
		}
		
		Operation operation = sequenceOf(
				insertStatut.build(),
				insertEtat.build(),
				insertCivilite.build(),
				insertSituation.build(),
				insertPersonnes.build(),
				insertOwnerBuilder.build(),
				insertCatBuilder.build());
		DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), operation);
        dbSetup.launch();
        
	}
	
	@BeforeMethod
	public void beforeMethod(){
		getSession().createCriteria(EtatCivil.class).list();
		getSession().createCriteria(Statut.class).list();
		getSession().createCriteria(Situation.class).list();
		getSession().createCriteria(Civilite.class).list();
		getSession().createCriteria(Owner.class).list();
		getSession().clear(); // Avoid first level cache
	}
	
	@AfterClass
	public void cleanTables(){
		deleteAllFrom("CAT","OW","STATUT","ETAT","CIV","SIT","PERS");
	}
	
	@Test
	public void cacheMustWork(){
		long hitCount = statutStats.getHitCount();
		getSession().get(Statut.class, 1000L);
		assertEquals(statutStats.getHitCount(), hitCount+1);
	}

	@Test
	public void eagerLoadedRelationWithJoinMustNotHitCache(){
		long statutHitCount = statutStats.getHitCount();
		long etatHitCount = etatStats.getHitCount();
		long sitHitCount = sitStats.getHitCount();
		Personne p  = (Personne)getSession().get(Personne.class, 1001L);
		assertNotNull(p);
		assertEquals(p.getStatut().getCode(), "S");
		assertEquals(statutStats.getHitCount(),statutHitCount);
		assertEquals(etatStats.getHitCount(),etatHitCount);
		assertEquals(sitStats.getHitCount(),sitHitCount);
	}
	
	@Test
	public void eagerLoadRelationWithSelectMustHitCache(){
		//TODO
	}
	
	@Test
	public void lazyLoadedRelationMustHitCacheWhenAccessed(){
		long statutHitCount = statutStats.getHitCount();
		long etatHitCount = etatStats.getHitCount();
		long sitHitCount = sitStats.getHitCount();
		Personne p  = (Personne)getSession().get(Personne.class, 1001L);
		assertNotNull(p);
		assertEquals(p.getEtatCivil().getCode(), "C");
		assertEquals(statutStats.getHitCount(),statutHitCount);
		assertEquals(etatStats.getHitCount(),etatHitCount+1);
		assertEquals(sitStats.getHitCount(),sitHitCount);
	}
	
	@Test
	public void lazyLoadedRelationMustHitCacheWhenAccessingEmbeddedProperty(){
		long civHitCount = civStats.getHitCount();
		Personne p  = (Personne)getSession().get(Personne.class, 1001L);
		assertNotNull(p);
		assertEquals(p.getCivilite().getNom().getNom(), "Monsieur");
		assertEquals(etatStats.getHitCount(),civHitCount+1);
	}
	
	@Test
	public void cacheMustNotBeHitWhenInvalidated(){
		long sitHitCount = sitStats.getHitCount();
		Situation s = (Situation)getSession().get(Situation.class, 2001L);
		s.setEnfants(10);
		getSession().flush();
		getSession().clear();
		assertEquals(sitStats.getHitCount(),sitHitCount+1,"Un hit, mais le cache est invalidé ");
		//TODO vérifier que le cache est invalidé?
		Personne p  = (Personne)getSession().get(Personne.class, 1001L);
		assertNotNull(p);
		assertEquals(p.getSituation().getEnfants(),10);
		assertEquals(sitStats.getHitCount(),sitHitCount+1,"Pas de hit, car le cache est invalidé ");
		getSession().clear();
		p  = (Personne)getSession().get(Personne.class, 1001L);;
		assertNotNull(p);
		assertEquals(p.getSituation().getEnfants(),10);
		assertEquals(sitStats.getHitCount(),sitHitCount+2,"Un nouveau hit, le cache étant correct");
	}
	
	@Test
	public void insertMustNotCreateACacheEntry(){
		long statutPutCount = statutStats.getPutCount();
		Statut statut = new Statut();
		statut.setCode("I");
		statut.setLibelle("Indépendant");
		getSession().saveOrUpdate(statut);
		assertNotNull(statut.getId());
		assertEquals(statutStats.getPutCount(), statutPutCount);
	}
	
	@Test
	public void lazyLoadedRelationMustHitCacheWhenAccessedWithBatchSizeButWontBatchFetch(){
		long initialHitsOnOwner = ownerStats.getHitCount();
		
		List<Cat> cats = getSession().createCriteria(Cat.class).list();
		
		int numberOfInit = 0;
		
		for(Cat c:cats){
			if(Hibernate.isInitialized(c.getOwner())) numberOfInit++;
		}
		
		assertEquals(numberOfInit,0, "Rien n'est intialisé");
		
		Hibernate.initialize(cats.get(0).getOwner()); //Initialize owner, but of the cache, does not batch...
		
		assertEquals(ownerStats.getHitCount(), initialHitsOnOwner+1, "Only one");
	}
}
