package be.fabrice.transaction;

import static org.assertj.core.api.Assertions.assertThat;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.testng.annotations.Test;

@ContextConfiguration(locations="classpath:transaction/test-application-context.xml")
public class TestSessionEvolutionInTransaction extends AbstractTransactionalTestNGSpringContextTests{
	@Autowired
	private Dao dao;
	
	@Autowired
	private Service service;
	
	@Autowired
	private PlatformTransactionManager transactionManager;
	
	@Test(description="session must be the same during the same transaction")
	public void sessionMustBeTheSameDuringTheSameTransaction(){
		Session session1 = dao.propagateTransactionAndGetSession();
		Session session2 = dao.propagateTransactionAndGetSession();
		
		assertThat(session1).isSameAs(session2);
	}
	
	@Test(description="sessions must be different in different transactions")
	public void sessions_must_be_different_in_different_transactions(){
		Session sessionInSameTransaction = dao.propagateTransactionAndGetSession();
		Session sessionInNewTransaction = dao.startNewTransactionAndGetSession();
		Session sessionInOtherNewTransaction = dao.startNewTransactionAndGetSession();
		
		assertThat(sessionInSameTransaction).isNotSameAs(sessionInNewTransaction);
		assertThat(sessionInSameTransaction).isNotSameAs(sessionInOtherNewTransaction);
		assertThat(sessionInOtherNewTransaction).isNotSameAs(sessionInNewTransaction);
	}
	
	@Test(description="session must be different when in new programmatic transaction")
	public void session_must_be_different_when_in_new_programmatic_transaction(){
		final Session initialSession = dao.propagateTransactionAndGetSession();
		TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
		transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRES_NEW);
		
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				Session sessionInNewTransaction = dao.propagateTransactionAndGetSession();
				assertThat(initialSession).isNotSameAs(sessionInNewTransaction);
			}
		});
	}
	
	@Test(description="session in new programmatic transaction must be propagated")
	public void session_in_new_programmatic_transaction_must_be_propagated(){
		Session initialSession = dao.propagateTransactionAndGetSession();
		TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
		transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRES_NEW);
		
		Session[] sessionsInNewTransaction = transactionTemplate.execute(new TransactionCallback<Session[]>() {
			@Override
			public Session[] doInTransaction(TransactionStatus status) {
				return service.propagateTransactionTwice();
			}
		});
		
		assertThat(initialSession).isNotSameAs(sessionsInNewTransaction[0]);
		assertThat(sessionsInNewTransaction[0]).isSameAs(sessionsInNewTransaction[1]);
	}
}
