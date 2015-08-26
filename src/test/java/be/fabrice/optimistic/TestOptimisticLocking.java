package be.fabrice.optimistic;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.fail;

import java.sql.Timestamp;
import java.util.Date;

import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;
import org.hibernate.Transaction;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.utils.TransactionalTestBase;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;

@ContextConfiguration(locations="classpath:optimistic/test-optimistic-spring.xml")
@Test(description="Optimistic locking behaviour",groups="OptimiticLockingTests",testName="Optimistic locking",suiteName="Optimistic locking suite")
public class TestOptimisticLocking extends TransactionalTestBase{
	
	@BeforeMethod
	public void beforeMethod(){
		Operation deleteAll = deleteAllFrom("EMP","BOSS");
		Operation bossInsert = insertInto("BOSS")
				.columns("ID","NAME","VERSION")
				.values(2000,"Boss",0)
				.build();
		Operation empInsert = insertInto("EMP")
				.columns("ID","FIRSTNAME","LASTNAME","MODIFICATION","BOSS_ID")
				.values(1000,"F1","L1","2014-02-04 00:00:00.000",2000)
				.values(1001,"F2","L2","2014-02-04 00:00:00.000",2000)
				.values(1002,"F3","L3","2014-02-04 00:00:00.000",2000)
				.build();
		
		Operation operation = sequenceOf(deleteAll, bossInsert, empInsert);
		DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), operation);
		
		dbSetup.launch();
	}
	
	@Test(description="timestamp must be created when transient entity is persisted")
	public void timestampMustBeCreatedWhenTransientEntityIsPersisted(){
		Employee employee = new Employee();
		employee.setFirstname("toto");
		employee.setLastname("lehéros");
		
		assertNull(employee.getModification());
		
		getSession().save(employee);
		
		assertNotNull(employee.getModification());
	}
	
	@Test(description="timestamp must be updated when entity is updated")
	public void timestampMustBeUpdatedWhenEntityIsUpdated(){
		Employee employee = (Employee) getSession().get(Employee.class,1001L);
		Timestamp tInit = employee.getModification();
		
		
		employee.setFirstname("toto");
		
		getSession().update(employee);
		getSession().flush();
		
		Timestamp tFinal = employee.getModification();
		assertNotEquals(tInit, tFinal,"Must be different");
		assertEquals(tInit.compareTo(tFinal), -1, "tFinal must be more recent");
	}
	
	@Test(description="version number must be created when transient entity is persisted")
	public void versionNumberMustBeCreatedWhenTransientEntityIsPersisted(){
		Boss boss = new Boss();
		boss.setName("toto");
		
		assertNull(boss.getVersion());
		
		getSession().save(boss);
		
		assertNotNull(boss.getVersion());
	}
	
	@Test(description="version number must be incremented when entity is updated")
	public void versionNumberMustBeIncrementedWhenEntityIsUpdated(){
		Boss boss = (Boss) getSession().get(Boss.class,2000L);
		Integer vInit = boss.getVersion();
		
		boss.setName("toto");
		
		getSession().update(boss);
		getSession().flush(); //Test specific
		
		Integer vFinal = boss.getVersion();
		assertEquals(vFinal, Integer.valueOf(vInit+1), "Version must be +1");
	}
	
	@Test(description="update of an out-of-date entity must throw a StaleObjectStateException")
	public void updateOfAnOutOfDateEntityMustThrowAnException(){
		Session session1 = sessionFactory.openSession();
		Session session2 = sessionFactory.openSession();
		
		Transaction t1 = session1.beginTransaction();
		Transaction t2 = session2.beginTransaction();
		
		Employee empV1 = (Employee) session1.get(Employee.class, 1000L);
		Employee empV2 = (Employee) session2.get(Employee.class, 1000L);
		
		assertNotSame(empV1, empV2,"Not useful otherwise");
		
		empV1.setFirstname("toto");
		t1.commit();
		session1.close();

		Date actualTimestamp = empV1.getModification();
		
		assertNotEquals(empV2.getModification(), actualTimestamp, "empV2 is out of date");
		
		try{
			empV2.setFirstname("tutu");
			t2.commit();
			fail("Must fail because out-of-date");
		}catch(StaleObjectStateException ex){
		}catch(Exception ex1){
			fail("Not the correct exception: "+ex1);
		}finally{
			session2.close();
		}
	}
	
	@Test(description="update of an out-of-version entity must throw a StaleObjectStateException")
	public void updateOfAnOutOfVersionEntityMustThrowAnException(){
		Session session1 = sessionFactory.openSession();
		Session session2 = sessionFactory.openSession();
		
		Transaction t1 = session1.beginTransaction();
		Transaction t2 = session2.beginTransaction();
		
		Boss bossV1 = (Boss) session1.get(Boss.class, 2000L);
		Boss bossV2 = (Boss) session2.get(Boss.class, 2000L);
		
		assertNotSame(bossV1, bossV2,"Not useful otherwise");
		
		bossV1.setName("toto");
		t1.commit();
		session1.close();

		Integer actualVersion = bossV1.getVersion();
		
		assertNotEquals(bossV2.getVersion(),actualVersion,"Boss V2 is out of version (out of date)");
		
		try{
			bossV2.setName("tutu");
			t2.commit();
			fail("Must fail because out of version");
		}catch(StaleObjectStateException ex){
		}catch(Exception ex1){
			fail("Not the correct exception: "+ex1);
		}finally{
			session2.close();
		}
	}
	
	@Test(description="deleting an out-of-date entity must throw a StaleObjectStateException")
	public void deletingAnOutOfDateEntityMustThrowAStaleObjectStateException(){
		Session session1 = sessionFactory.openSession();
		Session session2 = sessionFactory.openSession();
		
		Transaction t1 = session1.beginTransaction();
		Transaction t2 = session2.beginTransaction();
		
		Employee empV1 = (Employee) session1.get(Employee.class, 1000L);
		Employee empV2 = (Employee) session2.get(Employee.class, 1000L);
		
		assertNotSame(empV1, empV2,"Not useful otherwise");
		
		empV1.setFirstname("toto");
		t1.commit();
		session1.close();

		Date actualTimestamp = empV1.getModification();
		
		assertNotEquals(empV2.getModification(), actualTimestamp, "empV2 is out of date");

		try{
			session2.delete(empV2);
			t2.commit();
			fail("Must fail because empV2 is out-of-date");
		}catch(StaleObjectStateException ex){
		}catch(Exception ex1){
			fail("Not the correct exception: "+ex1);
		}finally{
			session2.close();
		}
	}
	
	@Test(description="version number of the container must be increased when adding an element to One-to-Many Collection")
	public void versionNumberOfTheContainerMustBeIncreasedWhenAddingAnElementToACollection(){
		Boss boss = (Boss) getSession().get(Boss.class, 2000L);
		
		Integer versionBeforeCollectionUpdate = boss.getVersion(); 
		
		Employee newEmployee = new Employee();
		newEmployee.setFirstname("new name");
		newEmployee.setLastname("new lastname");
		
		boss.getEmployees().add(newEmployee);
		
		getSession().saveOrUpdate(boss);
		getSession().flush();
		
		assertNotNull(newEmployee.getModification(), "Entity has been persisted and versionned");
		
		assertNotEquals(boss.getVersion(), versionBeforeCollectionUpdate, "Wierd, but boss verson has been updated");
	}
}
