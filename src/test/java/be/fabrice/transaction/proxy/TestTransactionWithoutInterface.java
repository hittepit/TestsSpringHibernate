package be.fabrice.transaction.proxy;

import static org.assertj.core.api.Assertions.fail;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.testng.annotations.Test;

@ContextConfiguration(locations="classpath:transaction/test-application-context.xml")
public class TestTransactionWithoutInterface extends AbstractTransactionalTestNGSpringContextTests {
	@Autowired
	private ProxiedService service;
	@Autowired
	PlatformTransactionManager transactionManager;
	
	@Test(description="the transactional service is a descendent of the base class")
	public void testProxyClass(){
		assertTrue(service instanceof ProxiedService, "Must be an instance of the base class");
		assertNotEquals(service.getClass(), ProxiedService.class, "Must not be the base class itself, it's a proxy");
	}
	
	@Test(description="An exception thrown within a transactional public method of a proxy marks the transaction for rollback")
	public void testRollback(){
		TransactionStatus status = transactionManager.getTransaction(null);
		assertFalse(status.isRollbackOnly(), "Pas de rollback au départ");
		
		try{
			service.test();
			fail("There must be an exception!");
		}catch(RuntimeException e){
			assertTrue(status.isRollbackOnly(), "Current transaction is marked for rollback");
		}
	}
	
	@Test(description="Call of a transactional protected method does not declare a transaction, even with a proxy")
	public void testProtectedCall(){
		TransactionStatus status = transactionManager.getTransaction(null);
		assertFalse(status.isRollbackOnly(), "Pas de rollback au départ");
		
		try{
			service.test2();
			fail("There must be an exception!");
		}catch(RuntimeException e){
			assertFalse(status.isRollbackOnly(), "Inner call does not play transactional");
		}
	}
	
	@Test(description="Call of a transactional package method does not declare a transaction, even with a proxy")
	public void testPackageCall(){
		TransactionStatus status = transactionManager.getTransaction(null);
		assertFalse(status.isRollbackOnly(), "Pas de rollback au départ");
		
		try{
			service.test3();
			fail("There must be an exception!");
		}catch(RuntimeException e){
			assertFalse(status.isRollbackOnly(), "Inner call does not play transactional");
		}
	}
	
	@Test(description="An inner call of a transactional method does not declare a transaction, even with a proxy")
	public void testInnerCall(){
		TransactionStatus status = transactionManager.getTransaction(null);
		assertFalse(status.isRollbackOnly(), "Pas de rollback au départ");
		
		try{
			service.foo();
			fail("There must be an exception!");
		}catch(RuntimeException e){
			assertFalse(status.isRollbackOnly(), "Inner call does not play transactional");
		}
	}
}
