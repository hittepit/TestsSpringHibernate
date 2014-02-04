package be.fabrice.optimistic.dao;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.fail;

import java.sql.Timestamp;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StaleObjectStateException;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.optimistic.entity.Employee;

@ContextConfiguration(locations="classpath:optimistic/test-optimistic-spring.xml")
public class TestOptimisticLocking extends AbstractTransactionalTestNGSpringContextTests{
	@Autowired
	private SessionFactory sessionFactory;
	@Autowired
	private Dao dao;
	
	@BeforeMethod
	public void beforeMethod(){
		executeSqlScript("optimistic/test-script.sql", false);
	}
	
	@Test
	public void testNewVersionIsInsertedWhenEntityIsPersisted(){
		Employee employee = new Employee();
		employee.setFirstname("toto");
		employee.setLastname("lehéros");
		
		assertNull(employee.getModification());
		
		dao.save(employee);
		
		assertNotNull(employee.getModification());
	}
	
	@Test
	public void testVersionIsUpdatedWhenUpdateIsDone(){
		Employee employee = dao.findEmployee(1001L);
		Timestamp tInit = employee.getModification();
		
		
		employee.setFirstname("toto");
		
		dao.save(employee);
		sessionFactory.getCurrentSession().flush(); //Test specific
		
		Timestamp tFinal = employee.getModification();
		assertNotEquals(tInit, tFinal,"Must be different");
		assertEquals(tInit.compareTo(tFinal), -1, "tFinal must be more recent");
	}
	
	@Test
	public void testUpdatingAnOldVersionThrowsAnException(){
		Session session = sessionFactory.openSession();
		Transaction t = session.beginTransaction();
		Employee e = new Employee();
		e.setFirstname("test");
		e.setLastname("test");
		session.saveOrUpdate(e);
		t.commit();

		Session session1 = sessionFactory.openSession();
		Session session2 = sessionFactory.openSession();
		
		assertNotSame(session1, session2,"Test would not be usefull otherwise");
		
		Transaction t1 = session1.beginTransaction();
		Transaction t2 = session2.beginTransaction();
		
		Employee empV1 = (Employee) session1.get(Employee.class, e.getId());
		Employee empV2 = (Employee) session2.get(Employee.class, e.getId());
		
		assertNotSame(empV1, empV2,"Again, not useful otherwise");
		
		empV1.setFirstname("toto");
		t1.commit();
		session1.close();

		try{
			empV2.setFirstname("tutu");
			t2.commit();
			fail("Must fail");
		}catch(StaleObjectStateException ex){
		}catch(Exception ex1){
			fail("Not the correct exception: "+e);
		}
		finally{
			session2.close();
			session.close();
			deleteFromTables("EMP");
		}
	}
}
