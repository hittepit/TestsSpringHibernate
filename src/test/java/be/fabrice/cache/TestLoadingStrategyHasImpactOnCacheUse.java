package be.fabrice.cache;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.hibernate.SessionFactory;
import org.hibernate.stat.SecondLevelCacheStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.cache.dao.Dao;
import be.fabrice.cache.entity.Personne;
import be.fabrice.cache.entity.Situation;
import be.fabrice.cache.entity.Statut;

@ContextConfiguration(locations="classpath:cache/test-cache-spring.xml")
public class TestLoadingStrategyHasImpactOnCacheUse extends AbstractTransactionalTestNGSpringContextTests {
	@Autowired
	private SessionFactory sessionFactory;
	@Autowired
	private Dao dao;
	
	private SecondLevelCacheStatistics etatStats;
	private SecondLevelCacheStatistics statutStats;
	private SecondLevelCacheStatistics sitStats;
	private SecondLevelCacheStatistics civStats;
	
	@BeforeClass
	public void beforeClass(){
		etatStats = sessionFactory.getStatistics().getSecondLevelCacheStatistics("ETAT");
		statutStats = sessionFactory.getStatistics().getSecondLevelCacheStatistics("STATUT");
		sitStats = sessionFactory.getStatistics().getSecondLevelCacheStatistics("SIT");
		civStats = sessionFactory.getStatistics().getSecondLevelCacheStatistics("CIV");
	}
	
	@BeforeMethod
	public void beforeMethod(){
		executeSqlScript("cache/test-script.sql", false);
		dao.findAllEtatCivil();
		dao.findAllStatut();
		dao.findAllSitutions();
		dao.findAllCivilites();
		sessionFactory.getCurrentSession().clear();
	}
	
	@Test
	public void testCacheIsWorking(){
		long hitCount = statutStats.getHitCount();
		dao.findStatut(1000L);
		assertEquals(statutStats.getHitCount(), hitCount+1);
	}

	@Test
	public void testEagerLoadingDoesNotHitCache(){
		long statutHitCount = statutStats.getHitCount();
		long etatHitCount = etatStats.getHitCount();
		long sitHitCount = sitStats.getHitCount();
		Personne p  = dao.find(1001L);
		assertNotNull(p);
		assertEquals(p.getStatut().getCode(), "S");
		assertEquals(statutStats.getHitCount(),statutHitCount);
		assertEquals(etatStats.getHitCount(),etatHitCount);
		assertEquals(sitStats.getHitCount(),sitHitCount);
	}
	
	@Test
	public void testLazyLoadingHitsCache(){
		long statutHitCount = statutStats.getHitCount();
		long etatHitCount = etatStats.getHitCount();
		long sitHitCount = sitStats.getHitCount();
		Personne p  = dao.find(1001L);
		assertNotNull(p);
		assertEquals(p.getEtatCivil().getCode(), "C");
		assertEquals(statutStats.getHitCount(),statutHitCount);
		assertEquals(etatStats.getHitCount(),etatHitCount+1);
		assertEquals(sitStats.getHitCount(),sitHitCount);
	}
	
	@Test
	public void testLazyLoadingWithEmbeddedHitsCache(){
		long civHitCount = civStats.getHitCount();
		Personne p  = dao.find(1001L);
		assertNotNull(p);
		assertEquals(p.getCivilite().getNom().getNom(), "Monsieur");
		assertEquals(etatStats.getHitCount(),civHitCount+1);
	}
	
	@Test
	public void testUpdateOfUpdatableCacheResultInCorrectValue(){
		long sitHitCount = sitStats.getHitCount();
		Situation s = dao.findSituation(2001L);
		s.setEnfants(10);
		sessionFactory.getCurrentSession().flush();
		sessionFactory.getCurrentSession().clear();
		assertEquals(sitStats.getHitCount(),sitHitCount+1,"Un hit, mais le cache est invalidé ");
		Personne p  = dao.find(1001L);
		assertNotNull(p);
		assertEquals(p.getSituation().getEnfants(),10);
		assertEquals(sitStats.getHitCount(),sitHitCount+1,"Pas de hit, car le cache est invalidé ");
		sessionFactory.getCurrentSession().clear();
		p  = dao.find(1001L);
		assertNotNull(p);
		assertEquals(p.getSituation().getEnfants(),10);
		assertEquals(sitStats.getHitCount(),sitHitCount+2,"Un nouveau hit, le cache étant correct");
	}
	
	@Test
	public void testInsertDoesNotCreatesACacheEntry(){
		long statutPutCount = statutStats.getPutCount();
		Statut statut = new Statut();
		statut.setCode("I");
		statut.setLibelle("Indépendant");
		dao.save(statut);
		assertNotNull(statut.getId());
		assertEquals(statutStats.getPutCount(), statutPutCount);
	}
}
