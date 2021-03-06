package be.fabrice.cache.query;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.hibernate.Session;
import org.hibernate.cache.Region;
import org.hibernate.impl.SessionFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.utils.TransactionalTestBase;
import be.fabrice.utils.logging.SimpleSql;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;

@Test(testName="Fonctionnement du cache de 2d niveau pour les queries", suiteName="Cache de second niveau")
@ContextConfiguration(locations="classpath:cache/query/test-spring.xml")
public class TestQueryCacheBehaviour extends TransactionalTestBase{
	@Autowired
	private DataSource dataSource;
	
	@BeforeClass
	public void initData(){
		Operation operations = sequenceOf(
				insertInto("item")
				.columns("id","name","status","date")
				.values(1,"test",true, new Date())
				.values(2,"test",true, new Date())
				.values(3,"test",true, new Date())
				.values(4,"test1",false, new Date())
				.build());
		
		DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), operations);
		dbSetup.launch();
	}
	
	@BeforeMethod
	public void reinitCache(){
		sessionFactory.getCache().evictDefaultQueryRegion();
		sessionFactory.getCache().evictQueryRegions();
	}
	
	@Test(description="cached query results must be found in cache")
	public void cached_query_results_must_be_found_in_cache(){
		long initialHits = sessionFactory.getStatistics().getQueryCacheHitCount();
		Session session = getSession();
		List<Item> items = session.createQuery("from Item i where i.name=:name")
			.setParameter("name", "test")
			.setCacheable(true)
			.list();
		assertThat(items).hasSize(3);
		
		Session newSession = sessionFactory.openSession(); //No session hit
		items = newSession.createQuery("from Item i where i.name=:name")
			.setParameter("name", "test")
			.setCacheable(true)
			.list();

		assertThat(items).hasSize(3);
		
		assertThat(sessionFactory.getStatistics().getQueryCacheHitCount()).isEqualTo(initialHits+1);		
	}
	
	@Test(description="only ids of the result must be cached")
	public void only_ids_of_the_result_must_be_cached(){
		getSession().createQuery("from Item i where i.name=:name")
				.setParameter("name", "test")
				.setCacheable(true)
				.list();
		
		Map<?,?> regions = ((SessionFactoryImpl)sessionFactory).getAllSecondLevelCacheRegions();
		Map<?,ArrayList<?>> content = ((Region)regions.get("org.hibernate.cache.StandardQueryCache")).toMap();
		for(ArrayList<?> items: content.values()){
			assertThat(items).contains(1L,2L,3L);
		}
	}
	
	@Test(description="each entity is fetch separatly when query cache is hit")
	public void each_entity_is_fetch_separatly_when_query_cache_is_hit(){
		getSession().createQuery("from Item i where i.name=:name")
				.setParameter("name", "test")
				.setCacheable(true)
				.list();
		
		SimpleSql.reinitSqlList();
		
		Session newSession = sessionFactory.openSession(); //No session hit
		newSession.createQuery("from Item i where i.name=:name")
			.setParameter("name", "test")
			.setCacheable(true)
			.list();

		assertThat(SimpleSql.contains("select .* from Item .* where .*\\.id=1")).isTrue();
		assertThat(SimpleSql.contains("select .* from Item .* where .*\\.id=2")).isTrue();
		assertThat(SimpleSql.contains("select .* from Item .* where .*\\.id=3")).isTrue();
		assertThat(SimpleSql.contains("select .* from Item .* where .*\\.id=4")).isFalse();
	}
	
	@Test(description="cached query results will not be found in cache when second query does not define setCacheable")
	public void cached_query_results_will_not_be_found_in_cache_when_second_query_does_not_define_setCacheable(){
		long initialHits = sessionFactory.getStatistics().getQueryCacheHitCount();
		Session session = getSession();
		session.createQuery("from Item i where i.name=:name")
			.setParameter("name", "test")
			.setCacheable(true)
			.list();
		
		Session newSession = sessionFactory.openSession(); //No session hit
		newSession.createQuery("from Item i where i.name=:name")
			.setParameter("name", "test")
			.list();
		
		assertThat(sessionFactory.getStatistics().getQueryCacheHitCount()).isEqualTo(initialHits);
		
		newSession = sessionFactory.openSession(); //No session hit
		newSession.createQuery("from Item i where i.name=:name")
			.setParameter("name", "test")
			.setCacheable(false)
			.list();
		
		assertThat(sessionFactory.getStatistics().getQueryCacheHitCount()).isEqualTo(initialHits);
	}
	
	@Test(description="different queries with same result must not hit cache")
	public void different_queries_with_same_result_must_not_hit_cache(){
		long initialHits = sessionFactory.getStatistics().getQueryCacheHitCount();
		Session session = getSession();
		List<Item> items1 = session.createQuery("from Item i where i.name=:name")
			.setParameter("name", "test")
			.setCacheable(true)
			.list();
		
		Session newSession = sessionFactory.openSession(); //No session hit
		List<Item> items2 = newSession.createQuery("from Item i where i.status=:status")
			.setParameter("status", true)
			.setCacheable(true)
			.list();
		
		assertThat(items1).isEqualTo(items2); //Same content
		
		assertThat(sessionFactory.getStatistics().getQueryCacheHitCount()).isEqualTo(initialHits);
	}
	
	@Test(description="same queries with where clause in different order must nor hit cache")
	public void same_queries_with_where_clause_in_different_order_must_nor_hit_cache(){
		long initialHits = sessionFactory.getStatistics().getQueryCacheHitCount();
		Session session = getSession();
		List<Item> items1 = session.createQuery("from Item i where i.name=:name and i.status=:status")
			.setParameter("name", "test")
			.setParameter("status", true)
			.setCacheable(true)
			.list();
		
		Session newSession = sessionFactory.openSession(); //No session hit
		List<Item> items2 = newSession.createQuery("from Item i where i.status=:status and i.name=:name")
			.setParameter("name", "test")
			.setParameter("status", true)
			.setCacheable(true)
			.list();
		
		assertThat(items1).isEqualTo(items2); //Same content
		
		assertThat(sessionFactory.getStatistics().getQueryCacheHitCount()).isEqualTo(initialHits);
	}
	
	@Test(description="tuple data must be cached when query is cached")
	public void tuple_data_must_be_cached_when_query_is_cached(){
		Session session = getSession();
		session.createQuery("select i.id, i.name from Item i where i.status=:status")
			.setParameter("status", true)
			.setCacheable(true)
			.list();
		
		Map<?,?> regions = ((SessionFactoryImpl)sessionFactory).getAllSecondLevelCacheRegions();
		Map<?,ArrayList<?>> content = ((Region)regions.get("org.hibernate.cache.StandardQueryCache")).toMap();
		for(ArrayList<?> items: content.values()){
			assertThat(items).contains(new Object[]{new Serializable[]{1L,"test"},new Serializable[]{2L,"test"},new Serializable[]{3L,"test"}});
		}
	}
}
