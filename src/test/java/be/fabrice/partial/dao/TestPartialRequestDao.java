package be.fabrice.partial.dao;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.partial.entity.Travailleur;

@ContextConfiguration(locations="classpath:partial/test-partial-spring.xml")
public class TestPartialRequestDao extends AbstractTransactionalTestNGSpringContextTests{
	@Autowired
	private Dao dao;
	
	@BeforeMethod
	public void beforeMethod(){
		executeSqlScript("partial/test-script.sql", false);
	}

	@Test
	public void testSelectTravailleurHasCategoryDefined(){
		Travailleur t = dao.findJustTravailleur("Toto");
		assertNotNull(t);
		assertNotNull(t.getCategory(),"Fetched by a second select");
	}
	
	@Test
	public void testSelectStarHasCategoryDefined(){
		Travailleur t = dao.findStarTravailleur("Tutu");
		assertNotNull(t);
		assertNotNull(t.getCategory(),"Fetched by a second select");
	}
	@Test 
	public void testSelectTravailleurByCatgeoryCodeHasCategoryDefined(){
		List<Travailleur> ts = dao.findJustTravailleurByCategory("O");
		assertEquals(ts.size(), 1);
		assertNotNull(ts.get(0).getCategory(),"Fetched by a second select");
	}
}
