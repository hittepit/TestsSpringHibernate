package be.fabrice.transaction;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations="classpath:transaction/test-application-context.xml")
public class ImpossibleTestNewTransactionWithTransactionalTest extends AbstractTransactionalTestNGSpringContextTests{
	@Autowired
	private DataSource dataSource;
	@Autowired
	private Dao dao;
	
	@BeforeMethod
	public void initData(){
		executeSqlScript("transaction/test-script.sql", false);
	}
	
	@Test(description="method in new transaction will not find data initialized but not commited")
	public void method_in_new_transaction_will_not_find_data_initialized_but_not_commited(){
		Item i = dao.find(100L);
		assertThat(i).isNull();
		
		assertThat(dao.findAll()).isEmpty();
	}
	
}
