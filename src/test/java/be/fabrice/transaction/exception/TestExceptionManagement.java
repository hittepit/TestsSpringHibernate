package be.fabrice.transaction.exception;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.testng.annotations.Test;

@ContextConfiguration(locations="classpath:transaction/test-application-context.xml")
public class TestExceptionManagement extends AbstractTransactionalTestNGSpringContextTests {
	@Autowired
	private First first;
	@Autowired
	PlatformTransactionManager transactionManager;

	@Test
	public void testExceptionThatDoesNotCross(){
		TransactionStatus status = transactionManager.getTransaction(null);
		assertFalse(status.isRollbackOnly(), "Pas de rollback au départ");
		
		first.foo1();
		
		assertFalse(status.isRollbackOnly(), "Pas de rollback généré par l'exception qui n'a pas franchi la transaction dans bar1");
	}

	@Test
	public void testExceptionThatDoesCross(){
		TransactionStatus status = transactionManager.getTransaction(null);
		assertFalse(status.isRollbackOnly(), "Pas de rollback au départ");
		
		first.foo2();
		
		assertTrue(status.isRollbackOnly(), "L'exception qui a franchi la transaction dans bar2");
	}
}
