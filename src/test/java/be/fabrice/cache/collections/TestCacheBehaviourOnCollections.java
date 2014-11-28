package be.fabrice.cache.collections;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.testng.Assert.assertEquals;

import org.hibernate.Hibernate;
import org.hibernate.stat.SecondLevelCacheStatistics;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.utils.TransactionalTestBase;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Insert.Builder;
import com.ninja_squad.dbsetup.operation.Operation;

@ContextConfiguration(locations="classpath:cache/collections/test-spring.xml")
public class TestCacheBehaviourOnCollections extends TransactionalTestBase{
	private SecondLevelCacheStatistics elementsCollectionCache;
	private SecondLevelCacheStatistics cachedElementsCollectionCache;
	private SecondLevelCacheStatistics cachedElementsCache;
	private SecondLevelCacheStatistics eagerCollectionElementsCache;
	
	@BeforeClass
	public void initCacheStatisticsAndData(){
		elementsCollectionCache = sessionFactory.getStatistics().getSecondLevelCacheStatistics("elements");
		cachedElementsCollectionCache = sessionFactory.getStatistics().getSecondLevelCacheStatistics("cachedElements");
		cachedElementsCache = sessionFactory.getStatistics().getSecondLevelCacheStatistics("element");
		eagerCollectionElementsCache = sessionFactory.getStatistics().getSecondLevelCacheStatistics("eagerElements");
		
		Builder insertContainerBuilder = insertInto("CONT").columns("ID","NAME");
		Builder insertElementBuilder = insertInto("EL").columns("ID","NAME","CONT_FK");
		Builder insertCachedElementBuilder = insertInto("CEL").columns("ID","NAME","CONT_FK");
		Builder insertEagerElementBuilder = insertInto("EEL").columns("ID","NAME","CONT_FK");
		
		insertContainerBuilder
			.values(1000,"CONT1");
		
		insertElementBuilder
			.values(10001,"EL1",1000)
			.values(10002,"EL2",1000)
			.values(10003,"EL3",1000);
		
		insertCachedElementBuilder
			.values(10001,"EL1",1000)
			.values(10002,"EL2",1000)
			.values(10003,"EL3",1000);
		
		insertEagerElementBuilder
			.values(10001,"EL1",1000)
			.values(10002,"EL2",1000)
			.values(10003,"EL3",1000);
		
		Operation operation = sequenceOf(
				insertContainerBuilder.build(),
				insertElementBuilder.build(),
				insertCachedElementBuilder.build(),
				insertEagerElementBuilder.build());
		
		DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), operation);
        dbSetup.launch();
	}
	
	@BeforeMethod
	public void initCache(){
		Container c = (Container)getSession().get(Container.class, 1000);
		Hibernate.initialize(c.getElements()); //Elle est lazy
		Hibernate.initialize(c.getCachedElements()); //Elle aussi
		getSession().clear(); //Eviter les 1st level cache
	}
	
	@Test
	public void collectionCacheIsHitWhenLoadingLazyFetchedCollection(){
		long initialHitCount = elementsCollectionCache.getHitCount();
		Container c = (Container)getSession().get(Container.class, 1000);
		c.getElements().isEmpty();
		assertEquals(elementsCollectionCache.getHitCount(), initialHitCount+1);
	}
	
	@Test
	public void collectionCacheAndElementCacheAreHitWhenLoadingLazyFetchedCollectionOfCachedElement(){
		long initialCollectionHitCount = cachedElementsCollectionCache.getHitCount();
		long initialCachedElementHitCount = cachedElementsCache.getHitCount();
		Container c = (Container)getSession().get(Container.class, 1000);
		Hibernate.initialize(c.getCachedElements());
		assertEquals(cachedElementsCollectionCache.getHitCount(), initialCollectionHitCount+1, "Collection cache hit ince");
		assertEquals(cachedElementsCache.getHitCount(), initialCachedElementHitCount+3, "Element cache hit 3 times");
	}
	
	@Test
	public void collectionCacheIsNotHitWhenCollectionIsEager(){
		long initialHitCount = eagerCollectionElementsCache.getHitCount();
		Container c = (Container)getSession().get(Container.class, 1000);
		assertEquals(eagerCollectionElementsCache.getHitCount(), initialHitCount);
	}
}
