package be.fabrice.utils;

import javax.sql.DataSource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;

public class TransactionalTestBase extends AbstractTransactionalTestNGSpringContextTests {
	@Autowired
	protected SessionFactory sessionFactory;
	
	@Autowired
	protected DataSource dataSource;
	
	public Session getSession(){
		return sessionFactory.getCurrentSession();
	}

}
