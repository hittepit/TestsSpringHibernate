package be.fabrice.lazy;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.collection.PersistentBag;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.lazy.entity.Element;
import be.fabrice.lazy.entity.Groupe;
import be.fabrice.utils.TransactionalTestBase;

@ContextConfiguration("classpath:lazy/test-spring.xml")
public class TestLazyInitialiation extends TransactionalTestBase{
	@BeforeMethod
	public void beforeMethod(){
		executeSqlScript("lazy/test-script.sql", false);
	}
	
	@Test
	public void testCollectionType(){
		Groupe g= (Groupe) getSession().get(Groupe.class,1000);
		assertEquals(g.getElements().getClass(), PersistentBag.class);
	}
	
	@Test
	public void testCollectionNotIntializedAtBeginning(){
		Session session = getSession();
		Groupe g = (Groupe) session.get(Groupe.class, 1000);
		assertFalse(Hibernate.isInitialized(g.getElements()),"Collection should not be initialized");
	}
	
	@Test
	public void testAccessToOneElementInitialize(){
		Groupe g = (Groupe) getSession().get(Groupe.class, 1000);
		g.getElements().get(0);
		assertTrue(Hibernate.isInitialized(g.getElements()),"Collection should be initialized");
	}
	
	@Test
	public void testSizeInitialize(){
		Groupe g = (Groupe) getSession().get(Groupe.class, 1000);
		g.getElements().size();
		assertTrue(Hibernate.isInitialized(g.getElements()),"Collection should be initialized");
	}
	
	@Test
	public void testIsEmptyInitialize(){
		Groupe g = (Groupe) getSession().get(Groupe.class, 1000);
		g.getElements().isEmpty();
		assertTrue(Hibernate.isInitialized(g.getElements()),"Collection should be initialized");
	}
	
	@Test
	public void testIteratorInitialize(){
		Groupe g = (Groupe) getSession().get(Groupe.class, 1000);
		g.getElements().iterator();
		assertTrue(Hibernate.isInitialized(g.getElements()),"Collection should be initialized");
	}
	
	@Test
	public void testHibernateInitializeInitialize(){
		Groupe g = (Groupe) getSession().get(Groupe.class, 1000);
		Hibernate.initialize(g.getElements());
		assertTrue(Hibernate.isInitialized(g.getElements()),"Collection should be initialized");
	}
	
	@Test
	public void testAdInitialize(){
		Groupe g = (Groupe) getSession().get(Groupe.class, 1000);
		g.getElements().add(new Element());
		assertTrue(Hibernate.isInitialized(g.getElements()),"Collection should be initialized");
	}
	
	@Test
	public void testCallToGetterDoesNotInitialize(){
		Groupe g = (Groupe) getSession().get(Groupe.class, 1000);
		g.getElements();
		assertFalse(Hibernate.isInitialized(g.getElements()),"Collection should not be initialized");
	}
	
}
