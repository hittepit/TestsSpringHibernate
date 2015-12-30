package be.fabrice.transaction;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.testng.annotations.Test;

@ContextConfiguration(locations="classpath:transaction/test-application-context.xml")
public class TestPropagation extends AbstractTransactionalTestNGSpringContextTests {
	@Autowired
	private Dao dao;
	
	@Autowired
	private PlatformTransactionManager transactionManager;

	//REQUIRED propage la transaction et rollback si exception dans méthode required
	@Test
	public void testRequiredMarksForRollback(){
		TransactionStatus status = transactionManager.getTransaction(null);
		try{
			dao.requiredWithException();
		}catch(Exception e){
			//Objectif: ne pas laisser se propager l'exception
		}
		assertThat(status.isRollbackOnly()).isTrue();
	}
	
	@Test
	public void testNoTransactionDoesNotMarkForRollback(){
		TransactionStatus status = transactionManager.getTransaction(null);
		try{
			dao.nonTransactionException();
		}catch(Exception e){
			//Objectif: ne pas laisser se propager l'exception
		}
		assertThat(status.isRollbackOnly()).isFalse();
	}
	
	//REQUIres_NEW ne propage pas la transaction et le rollback si exception 
	@Test
	public void testRequiresNewDoesNotMarkForRollback(){
		TransactionStatus statusTransactionParente = transactionManager.getTransaction(null);
		try{
			dao.requiresNewWithException();
		}catch(Exception e){
			//Objectif: ne pas laisser se propager l'exception
		}
		assertThat(statusTransactionParente.isRollbackOnly()).isFalse();
	}
	
	//NESTED ne propage pas la transaction et la rollback si exception 
	@Test
	public void testNestedDoesNotMarkForRollback(){
		TransactionStatus statusTransactionParente = transactionManager.getTransaction(null);
		try{
			dao.nestedWithException();
		}catch(Exception e){
			//Objectif: ne pas laisser se propager l'exception
		}
		assertThat(statusTransactionParente.isRollbackOnly()).isFalse();
	}
	
	//NOT_SUPPORTED suspend la transaction, pas de rollback si exception 
	@Test
	public void testNotSupportedDoesNotMarkForRollback(){
		TransactionStatus statusTransactionParente = transactionManager.getTransaction(null);
		try{
			dao.notSupportedWithException();;
		}catch(Exception e){
			//Objectif: ne pas laisser se propager l'exception
		}
		assertThat(statusTransactionParente.isRollbackOnly()).isFalse();
	}
}
