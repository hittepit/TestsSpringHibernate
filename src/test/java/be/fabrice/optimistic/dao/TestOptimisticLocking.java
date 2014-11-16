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

import be.fabrice.optimistic.entity.Boss;
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
	public void testNewTimestampVersionIsInsertedWhenEntityIsPersisted(){
		Employee employee = new Employee();
		employee.setFirstname("toto");
		employee.setLastname("lehéros");
		
		assertNull(employee.getModification());
		
		dao.save(employee);
		
		assertNotNull(employee.getModification());
	}
	
	@Test
	public void testTimestampVersionIsUpdatedWhenUpdateIsDone(){
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
	public void testTimestampVersionUpdatingAnOldVersionThrowsAnException(){
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
			fail("Not the correct exception: "+ex1);
		}finally{
			session2.close();
			session.close();
			deleteFromTables("EMP");
		}
	}
	
	@Test
	public void testNewIntegerVersionIsInsertedWhenEntityIsPersisted(){
		Boss boss = new Boss();
		boss.setName("toto");
		
		assertNull(boss.getVersion());
		
		dao.save(boss);
		
		assertNotNull(boss.getVersion());
	}
	
	@Test
	public void testIntegerVersionIsUpdatedWhenUpdateIsDone(){
		Boss boss = dao.findBoss(2000L);
		Integer vInit = boss.getVersion();
		
		boss.setName("toto");
		
		dao.save(boss);
		sessionFactory.getCurrentSession().flush(); //Test specific
		
		Integer vFinal = boss.getVersion();
		assertEquals(Integer.valueOf(vInit+1), vFinal,"Version must be +1");
	}
	
	@Test
	public void testIntegerVersionUpdatingAnOldVersionThrowsAnException(){
		Session session = sessionFactory.openSession();
		Transaction t = session.beginTransaction();
		Boss b = new Boss();
		b.setName("test");
		session.saveOrUpdate(b);
		t.commit();

		Session session1 = sessionFactory.openSession();
		Session session2 = sessionFactory.openSession();
		
		assertNotSame(session1, session2,"Test would not be usefull otherwise");
		
		Transaction t1 = session1.beginTransaction();
		Transaction t2 = session2.beginTransaction();
		
		Boss bossV1 = (Boss) session1.get(Boss.class, b.getId());
		Boss bossV2 = (Boss) session2.get(Boss.class, b.getId());
		
		assertNotSame(bossV1, bossV2,"Again, not useful otherwise");
		
		bossV1.setName("toto");
		t1.commit();
		session1.close();

		try{
			bossV2.setName("tutu");
			t2.commit();
			fail("Must fail");
		}catch(StaleObjectStateException ex){
		}catch(Exception ex1){
			fail("Not the correct exception: "+ex1);
		}finally{
			session2.close();
			session.close();
			deleteFromTables("EMP");
		}
	}
	
	@Test
	public void testDeletingAnOldVersionThrowsAnException(){
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
			session2.delete(empV2);
			t2.commit();
			fail("Must fail");
		}catch(StaleObjectStateException ex){
		}catch(Exception ex1){
			fail("Not the correct exception: "+e);
		}finally{
			session2.close();
			session.close();
			deleteFromTables("EMP");
		}
	}
	
	@Test
	public void testUpdatingAnConcurrentlyDeletedVersionThrowsAnException(){
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
		
		session2.delete(empV2);
		t2.commit();
		session2.close();

		try{
			empV1.setFirstname("toto");
			t1.commit();
			fail("Must fail");
		}catch(StaleObjectStateException ex){
		}catch(Exception ex1){
			fail("Not the correct exception: "+e);
		}finally{
			session1.close();
			session.close();
			deleteFromTables("EMP");
		}
	}
	
	@Test
	public void testDeletingAnConcurrentlyDeletedVersionSurprisinglyThrowsAnException(){
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
		
		session2.delete(empV2);
		t2.commit();
		session2.close();

		try{
			session1.delete(empV1);
			t1.commit();
			fail("Must fail");
		}catch(StaleObjectStateException ex){
		}catch(Exception ex1){
			fail("Not the correct exception: "+e);
		}finally{
			session1.close();
			session.close();
			deleteFromTables("EMP");
		}
	}
	
	@Test
	public void testModifyingChildrenDoesNotImpactParent(){
		Session session = sessionFactory.openSession();
		Transaction t = session.beginTransaction();
		Employee e = new Employee();
		e.setFirstname("test");
		e.setLastname("test");
		Boss b = new Boss();
		b.setName("TestBoss");
		b.add(e);
		session.saveOrUpdate(b);
		t.commit();

		Session session1 = sessionFactory.openSession();
		Session session2 = sessionFactory.openSession();
		
		assertNotSame(session1, session2,"Test would not be usefull otherwise");
		
		Transaction t1 = session1.beginTransaction();
		Transaction t2 = session2.beginTransaction();
		
		Boss b1 = (Boss) session1.get(Boss.class, b.getId());
		Boss b2 = (Boss) session2.get(Boss.class, b.getId());
		
		assertNotSame(b1, b2,"Again, not useful otherwise");
		
		b1.getEmployees().get(0).setFirstname("toto");
		t1.commit();
		session1.close();
		
		b2.setName("oufti");
		t2.commit();
		session2.close();
	}
}
