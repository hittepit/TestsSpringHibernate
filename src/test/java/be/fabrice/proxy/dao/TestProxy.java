package be.fabrice.proxy.dao;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.proxy.entity.Employeur;
import be.fabrice.proxy.entity.EmployeurCorrect;
import be.fabrice.proxy.entity.Travailleur;

/**
 * Le but de ce test est de montrer certains pièges lors de l'utilisation de proxies (lazy loading de relations
 * ManyToOne).
 * @author fabrice.claes
 *
 */
@ContextConfiguration(locations="classpath:proxy/test-proxy-spring.xml")
public class TestProxy extends AbstractTransactionalTestNGSpringContextTests {
	@Autowired
	private TravailleurDao travailleurDao;
	
	@BeforeMethod
	public void beforeTest(){
		executeSqlScript("proxy/test-script.sql", false);
	}
	
	@Test
	public void testStandardEclipseGeneratedEqualsDoesNotWorkWellWithProxies(){
		Employeur employeurNotManaged = new Employeur();
		employeurNotManaged.setId(1000);
		employeurNotManaged.setName("Anybody");
		
		Travailleur t = travailleurDao.find(1001);
		
		assertNotEquals(t.getEmployeur(), employeurNotManaged);
		assertEquals(t.getEmployeur().getName(), employeurNotManaged.getName());
		assertEquals(t.getEmployeur().getId(), employeurNotManaged.getId());
	}
	
	@Test
	public void testStandardEclipseGeneratedEqualsDoesNotWorkWellWithProxiesEvenIfInitialized(){
		Employeur employeurNotManaged = new Employeur();
		employeurNotManaged.setId(1000);
		employeurNotManaged.setName("Anybody");
		
		Travailleur t = travailleurDao.find(1001);
		
		assertEquals(t.getEmployeur().getName(), employeurNotManaged.getName());
		assertEquals(t.getEmployeur().getId(), employeurNotManaged.getId());
		assertNotEquals(t.getEmployeur(), employeurNotManaged);
	}
	
	//TODO test avec presque correct
	//TODO comment
	
	@Test
	public void testCorrectEqualsWorksWithProxies(){
		EmployeurCorrect employeurNotManaged = new EmployeurCorrect();
		employeurNotManaged.setId(1002);
		employeurNotManaged.setName("Another Anybody else");
		
		Travailleur t = travailleurDao.find(1001);
		
		assertEquals(t.getEmployeurCorrect(), employeurNotManaged);
		assertEquals(t.getEmployeurCorrect().getName(), employeurNotManaged.getName());
		assertEquals(t.getEmployeurCorrect().getId(), employeurNotManaged.getId());
	}
	
	@Test
	public void testCorrectEqualsWorksWithProxiesEvenIfInitialized(){
		EmployeurCorrect employeurNotManaged = new EmployeurCorrect();
		employeurNotManaged.setId(1002);
		employeurNotManaged.setName("Another Anybody else");
		
		Travailleur t = travailleurDao.find(1001);
		assertEquals(t.getEmployeurCorrect().getName(), employeurNotManaged.getName());
		assertEquals(t.getEmployeurCorrect().getId(), employeurNotManaged.getId());
		assertEquals(t.getEmployeurCorrect(), employeurNotManaged);
	}
}
