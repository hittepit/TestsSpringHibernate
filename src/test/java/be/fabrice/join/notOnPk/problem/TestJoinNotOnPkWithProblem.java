package be.fabrice.join.notOnPk.problem;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import org.hibernate.PropertyAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.utils.TransactionalTestBase;

@ContextConfiguration(locations="classpath:join/notOnPk/problem/test-spring.xml")
public class TestJoinNotOnPkWithProblem extends TransactionalTestBase {
	
	@BeforeMethod
	public void beforeMethod(){
		executeSqlScript("join/notOnPk/problem/test-script.sql", true);
	}

	/**
	 * If the relation is lazy, Hibernate try to set FK as a Long while the join column is a Varchar.
	 */
	@Test
	public void testFindPersonWithLazyParamThrowsAnExceptionWhenFetching(){
		try{
			getSession().get(Personne.class,1000L);
			fail();
		}catch(PropertyAccessException e){
			assertTrue(e.getCause() instanceof IllegalArgumentException);
		}
	}

	/**
	 * With Eager fetching, Parametres is null because the generated request is
	 * select personne0_.id as id1_1_, personne0_.code as code1_1_, 
	 * 			personne0_.name as name1_1_, parametres1_.id as id0_0_, 
	 * 			parametres1_.CODE_P as CODE3_0_0_, parametres1_.valeur as valeur0_0_ 
	 * from PERS personne0_ 
	 * left outer join PARAMS parametres1_ 
	 * 			on personne0_.id=parametres1_.CODE_P 
	 * where personne0_.id=?
	 * 
	 * what is incorrect...
	 */
	@Test
	public void testFindPersonWithEagerParamReturnsNoParams(){
		Personne p = (Personne) getSession().createQuery("from Personne p left join fetch p.parametres where p.id=:id")
				.setLong("id", 1000L).uniqueResult();
		assertNotNull(p);
		assertNull(p.getParametres(),"Not correct !");
	}
}
