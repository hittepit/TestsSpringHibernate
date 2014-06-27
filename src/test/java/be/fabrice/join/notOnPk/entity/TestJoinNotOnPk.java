package be.fabrice.join.notOnPk.entity;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.utils.TransactionalTestBase;

@ContextConfiguration(locations="classpath:join/notOnPk/test-spring.xml")
public class TestJoinNotOnPk extends TransactionalTestBase {
	
	@BeforeMethod
	public void beforeMethod(){
		executeSqlScript("join/notOnPk/test-script.sql", true);
	}

	@Test
	public void testFindPersonWithParam(){
		Personne p = (Personne) getSession().get(Personne.class,1000L);
		assertNotNull(p);
		assertNotNull(p.getParametres());
		assertEquals(p.getName(), "Toto");
		assertEquals(p.getParametres().getValeur(),"Test1");
	}

	@Test
	public void testFindPersonWithoutParam(){
		Personne p = (Personne) getSession().get(Personne.class,1001L);
		assertNotNull(p);
		assertNull(p.getParametres());
		assertEquals(p.getName(), "Tutu");
	}
}
