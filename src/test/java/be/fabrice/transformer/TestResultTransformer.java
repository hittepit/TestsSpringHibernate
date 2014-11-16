package be.fabrice.transformer;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.transformer.dao.Dao;
import be.fabrice.transformer.entity.ProprieteVO;
import be.fabrice.utils.TransactionalTestBase;

@ContextConfiguration(locations="classpath:transformer/test-spring.xml")
public class TestResultTransformer extends TransactionalTestBase{
	@Autowired
	private Dao dao;
	
	@BeforeMethod
	public void beforeMethod(){
		executeSqlScript("transformer/test-script.sql", false);
	}
	
	/**
	 * 
	 */
	@Test
	public void testFindProprietesPourUnJoueur(){
		List<ProprieteVO> vos = dao.find(10);
		/*
		On doit avoir 4 objets, 1 par propriétédef-personnage
		donc
		def				perso 		init	val
		100 (Argent)	100 (Tork)	1000	(1->120, 2->100, 3-> 20, 4->100)
		100 (Argent)	101 (Rana)	2000	(1->150, 2->10, 3->25, 4->100)
		101 (XP)		100 (Tork)	null	(1->1, 2-> 2, 3->1, 4->0)
		101 (XP)		101 (Rana)	null	(1-> 1, 2->0, 3->2, 4->4)
		*/
		assertEquals(vos.size(), 4);
		ProprieteVO template = new ProprieteVO();
		template.setProprieteDefinitionId(100);
		template.setPersonnageId(100);
		ProprieteVO realVo = vos.get(vos.indexOf(template));
		assertNotNull(realVo);
		assertEquals(realVo.getValeurInit(),1000.0);
		assertEquals(realVo.getValeursParTour().get(2),100.0);
		assertEquals(realVo.getValeursParTour().get(3),20.0);
		
		template = new ProprieteVO();
		template.setProprieteDefinitionId(101);
		template.setPersonnageId(101);
		realVo = vos.get(vos.indexOf(template));
		assertNotNull(realVo);
		assertNull(realVo.getValeurInit());
		assertEquals(realVo.getValeursParTour().get(2),0.0);
		assertEquals(realVo.getValeursParTour().get(3),2.0);
	}
}
