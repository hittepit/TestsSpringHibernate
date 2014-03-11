package be.fabrice.model.dao;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import org.hibernate.PropertyAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.model.entity.Rectangle;

@ContextConfiguration(locations="classpath:model/test-model-spring.xml")
public class TestRectanglePersistence extends AbstractTransactionalTestNGSpringContextTests {
		@Autowired
		private Dao dao;
		
		@BeforeMethod
		public void beforeMethod(){
			executeSqlScript("model/test-script.sql", false);
		}
		
		@Test
		public void testFindPossibleWithCoherentValues(){
			Rectangle r = dao.findRectangle(1000L);
			assertEquals(r.getLongueur(), 10.0);
			assertEquals(r.getLargeur(), 2.5);
		}
		
		@Test
		public void testFindImpossibleWithNonCoherentValues(){
			try{
				dao.findRectangle(1001L);
				fail();
			}catch(PropertyAccessException e){
				//ok
			}
		}
}
