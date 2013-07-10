package be.fabrice.simple.dao;

import static org.testng.Assert.assertEquals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.simple.entity.Person;

@ContextConfiguration(locations="classpath:simple/test-simple-spring.xml")
public class TestPersonDao extends AbstractTransactionalTestNGSpringContextTests {
	//TODO: tester que save - find renvoie le même objet et ne garantit pas le passage en DB
	//TODO tests inutiles (contraintes d'unicité)
	@Autowired
	private PersonDao personDao;
	
	@BeforeMethod
	public void beforeMethod(){
		executeSqlScript("simple/test-script.sql", false);
	}
	
	@Test
	public void testSimpleFind(){
		Person p = personDao.find(1000);
		assertEquals(p.getFirstname(), "F1");
		assertEquals(p.getLastname(),"L1");
	}
}
