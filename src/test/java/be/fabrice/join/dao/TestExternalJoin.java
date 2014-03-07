package be.fabrice.join.dao;

import static org.testng.Assert.assertEquals;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.join.entity.Personne;

@ContextConfiguration(locations="classpath:join/test-join-spring.xml")
public class TestExternalJoin extends AbstractTransactionalTestNGSpringContextTests{
	@Autowired
	private Dao dao;
	
	@BeforeMethod
	public void beforeMethod(){
		executeSqlScript("join/test-script.sql", true);
	}
	
	@Test
	public void testFindByCategory(){
		List<Personne> ps = dao.findPersonneByTravailleurCategory("Employé");
		
		assertEquals(ps.size(), 1);
	}
}
