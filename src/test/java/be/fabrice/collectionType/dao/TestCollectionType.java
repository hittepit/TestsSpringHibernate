package be.fabrice.collectionType.dao;

import static org.testng.Assert.assertEquals;

import org.hibernate.collection.PersistentBag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.collectionType.entity.Facture;

@ContextConfiguration(locations="classpath:collectionType/test-collection-spring.xml")
public class TestCollectionType extends AbstractTransactionalTestNGSpringContextTests {
	@Autowired
	private Dao dao;
	
	@BeforeMethod
	public void beforeMethod(){
		executeSqlScript("collectionType/test-script.sql", false);
	}
	
	@Test
	public void testCollectionType(){
		Facture f = dao.find(1000L);
		assertEquals(f.getLignes().getClass(), PersistentBag.class);
	}
}
