package be.fabrice.manyToOne.dao;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.manyToOne.entity.Employeur;
import be.fabrice.manyToOne.entity.Travailleur;

@ContextConfiguration(locations="classpath:manyToOne/test-manyToOne-spring.xml")
public class TestFindTravailleurs extends AbstractTransactionalTestNGSpringContextTests {
	@Autowired
	private TravailleurDao travailleurDao;
	@Autowired
	private EmployeurDao employeurDao;
	
	@BeforeMethod
	public void beforeMethod(){
		executeSqlScript("manyToOne/test-script.sql", false);
	}
	
	@Test
	public void testItIsPossibleToFindOtherEndOfMonoDirectionalRelation(){
		Employeur emp = employeurDao.find(1000);
		assertNotNull(emp);
		
		List<Travailleur> travailleurs = travailleurDao.findTravailleurs(emp);
		assertEquals(travailleurs.size(), 2);
		assertTrue(travailleurs.contains(new Travailleur(){
			{setNom("Trav1");} //Puisque le equals est sur le nom...
		}));
		assertTrue(travailleurs.contains(new Travailleur(){
			{setNom("Trav2");}
		}));
	}
	
	@Test
	public void testAgainItIsPossibleToFindOtherEndOfMonoDirectionalRelation(){
		Employeur emp = employeurDao.find(1001);
		assertNotNull(emp);
		
		List<Travailleur> travailleurs = travailleurDao.findTravailleurs(emp);
		assertEquals(travailleurs.size(), 1);
		assertTrue(travailleurs.contains(new Travailleur(){
			{setNom("Trav3");}
		}));
	}
	
	@Test
	public void testAgainIfNoTravailleurGetsAnEmptyList(){
		Employeur emp = employeurDao.find(1002);
		assertNotNull(emp);
		
		List<Travailleur> travailleurs = travailleurDao.findTravailleurs(emp);
		assertTrue(travailleurs.isEmpty());
	}
}
