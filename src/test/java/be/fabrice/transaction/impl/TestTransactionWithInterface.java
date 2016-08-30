package be.fabrice.transaction.impl;

import static org.assertj.core.api.Assertions.fail;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.testng.annotations.Test;

@ContextConfiguration(locations="classpath:transaction/test-application-context.xml")
public class TestTransactionWithInterface extends AbstractTransactionalTestNGSpringContextTests {
	@Autowired
	private InterfacedService service;
	@Autowired
	PlatformTransactionManager transactionManager;
	
	@Test(description="the transactional service is a descendent of the base class")
	public void testProxyClass(){
		assertTrue(service instanceof InterfacedService, "Must be an instance of the interface");
		assertFalse(service instanceof InterfacedServiceImpl, "Must not be an instance of the base class, is not a derived class");
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
