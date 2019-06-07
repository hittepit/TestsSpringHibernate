package be.fabrice.criteria.alias.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.SQLGrammarException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.criteria.alias.entity.Employe;

@ContextConfiguration(locations="classpath:criteria/alias/test-alias-spring.xml")
public class TestDao extends AbstractTransactionalTestNGSpringContextTests{
	@Autowired
	private Dao dao;
	@Autowired
	private SessionFactory sessionFactory;
	
	@BeforeMethod
	private void beforeMethod(){
		executeSqlScript("criteria/alias/test-script.sql", false);
	}
	
	@Test
	public void testFindBySocIdWithIncorrectCriteriaWorks(){
		List<Employe> emps = dao.incorrectFindEmployes(1000L);
		assertEquals(emps.size(), 3);
	}
	
	@Test(expectedExceptions=SQLGrammarException.class)
	public void testCountWithIncorrectCriteriaDoesNotWork(){
		dao.incorrectCountEmployes(1000L);
	}
	
	@Test
	public void testFindBySocIdWithCOrrectCriteriaWorks(){
		List<Employe> emps = dao.findEmployes(1000L);
		assertEquals(emps.size(), 3);
	}
	
	@Test
	public void testCountWithCorrectCriteriaWorks(){
		Long size = dao.countEmployes(1000L);
		assertEquals(size, Long.valueOf(3l));
	}
	
	@Test
	public void testThreeDifferentWaysForCriteria1(){
		Session session = sessionFactory.getCurrentSession();
		List<Employe> es =  session.createCriteria(Employe.class)
		.createCriteria("patron")
		.add(Restrictions.eq("name", "Pat1"))
		.list();
		assertThat(es).hasSize(3);
	}
	
	@Test
	public void testThreeDifferentWaysForCriteria2(){
		Session session = sessionFactory.getCurrentSession();
		List<Employe> es =  session.createCriteria(Employe.class)
		.createCriteria("patron", "p")
		.add(Restrictions.eq("name", "Pat1"))
		.list();
		assertThat(es).hasSize(3);
	}
	
	@Test
	public void testThreeDifferentWaysForCriteria3(){
		Session session = sessionFactory.getCurrentSession();
		List<Employe> es =  session.createCriteria(Employe.class)
		.createCriteria("patron", "p")
		.add(Restrictions.eq("p.name", "Pat1"))
		.list();
		assertThat(es).hasSize(3);
	}
}
