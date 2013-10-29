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

@ContextConfiguration(locations="classpath:cache/test-cache-spring.xml")
public class TestLoadingStrategyHasImpactOnCacheUse extends AbstractTransactionalTestNGSpringContextTests {
	@Autowired
	private SessionFactory sessionFactory;
	@Autowired
	private Dao dao;
	
	private SecondLevelCacheStatistics etatStats;
	private SecondLevelCacheStatistics statutStats;
	
	@BeforeClass
	public void beforeClass(){
		etatStats = sessionFactory.getStatistics().getSecondLevelCacheStatistics("ETAT");
		statutStats = sessionFactory.getStatistics().getSecondLevelCacheStatistics("STATUT");
	}
	
	@BeforeMethod
	public void beforeMethod(){
		executeSqlScript("cache/test-script.sql", false);
		dao.findAllEtatCivil();
		dao.findAllStatut();
		sessionFactory.getCurrentSession().clear();
	}
	
	@Test
	public void testCacheIsWorking(){
		long hitCount = statutStats.getHitCount();
		dao.findStatut(1L);
		assertEquals(statutStats.getHitCount(), hitCount+1);
	}

	@Test
	public void testEagerLoadingDoesNotHitCache(){
		long statutHitCount = statutStats.getHitCount();
		long etatHitCount = etatStats.getHitCount();
		Personne p  = dao.find(1001L);
		assertNotNull(p);
		assertEquals(p.getStatut().getCode(), "S");
		assertEquals(statutStats.getHitCount(),statutHitCount);
		assertEquals(etatStats.getHitCount(),etatHitCount);
	}
	
	@Test
	public void testLazyLoadingHitsCache(){
		long statutHitCount = statutStats.getHitCount();
		long etatHitCount = etatStats.getHitCount();
		Personne p  = dao.find(1001L);
		assertNotNull(p);
		assertEquals(p.getEtatCivil().getCode(), "C");
		assertEquals(statutStats.getHitCount(),statutHitCount);
		assertEquals(etatStats.getHitCount(),etatHitCount+1);
	}
}
