package be.fabrice.inheritance.join.dao;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.inheritance.join.entity.Boss;
import be.fabrice.inheritance.join.entity.Employeur;
import be.fabrice.inheritance.join.entity.EmployeurVo;

@ContextConfiguration("classpath:inheritance/join/test-inheritance-spring.xml")
public class TestJoinInheritance extends AbstractTransactionalTestNGSpringContextTests{
	@Autowired
	private Dao dao;
	
	@BeforeMethod
	public void beforeMethod(){
		executeSqlScript("inheritance/join/test-script.sql", false);
	}
	
	@Test
	public void testFindBossEmployeurMustReturnABoss(){
		Employeur e = dao.findEmployeur(1000);
		assertNotNull(e);
		assertTrue(e instanceof Boss);
	}
	
	@Test
	public void testFindLazyEmployeurMustEmployeur(){
		Employeur e = dao.findSimpleEmployeur(1000);
		assertNotNull(e);
		assertEquals(e.getCountry(),"Belgium"); //Init
		assertFalse(e instanceof Boss); //Aie...
	}
	
	@Test
	public void testFindEmployeurVoMakesSimplerRequest(){
		EmployeurVo e = dao.findEmployeurVo(1000);
		assertNotNull(e);
		assertEquals(e.getCountry(),"Belgium");
		assertEquals(e.getId(),Integer.valueOf(1000));
	}
	
	@Test
	public void findBossMustReturnABoss(){
		Boss e = dao.findBoss(1000);
		assertNotNull(e);
		assertTrue(e instanceof Boss);
	}
}
