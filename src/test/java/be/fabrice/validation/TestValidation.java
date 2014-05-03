package be.fabrice.validation;

import org.hibernate.PropertyValueException;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.DataException;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.utils.TransactionalTestBase;

@ContextConfiguration(locations="classpath:validation/test-spring.xml")
public class TestValidation extends TransactionalTestBase {
	@BeforeMethod
	public void beforeMethod(){
		executeSqlScript("validation/test-script.sql", false);
	}
	
	@Test(expectedExceptions=PropertyValueException.class)
	public void testHibernateCheckNullableProperty(){
		SimpleEntity s = new SimpleEntity();
		s.setNotNullValue(null);
		s.setLongLengthLimitedValue("toto");
		s.setSmallLengthLimitedValue("toto");
		s.setUniqueValue(10);
		s.setNonUniqueValue(10);
		
		getSession().save(s);
	}
	
	@Test
	public void testHibernateDoesNotCheckLengthOfProperties(){
		SimpleEntity s = new SimpleEntity();
		s.setNotNullValue(1);
		s.setLongLengthLimitedValue("toto");
		s.setSmallLengthLimitedValue("123456789101112");
		s.setUniqueValue(10);
		s.setNonUniqueValue(10);
		
		getSession().save(s);
	}
	
	@Test(expectedExceptions=DataException.class)
	public void testDatabaseConstraintsOnLengthAreUsed(){
		SimpleEntity s = new SimpleEntity();
		s.setNotNullValue(1);
		s.setLongLengthLimitedValue("123456789101112");
		s.setSmallLengthLimitedValue("toto");
		s.setUniqueValue(10);
		s.setNonUniqueValue(10);
		
		getSession().save(s);
	}
	
	@Test
	public void testHibernateDoesNotCheckUnicity(){
		SimpleEntity s = new SimpleEntity();
		s.setNotNullValue(1);
		s.setLongLengthLimitedValue("toto");
		s.setSmallLengthLimitedValue("toto");
		s.setUniqueValue(3);
		s.setNonUniqueValue(10);
		
		getSession().save(s);
	}
	
	@Test(expectedExceptions=ConstraintViolationException.class)
	public void testDatabaseUnicityConstraintsAreUsed(){
		SimpleEntity s = new SimpleEntity();
		s.setNotNullValue(1);
		s.setLongLengthLimitedValue("toto");
		s.setSmallLengthLimitedValue("toto");
		s.setUniqueValue(10);
		s.setNonUniqueValue(2);
		
		getSession().save(s);
	}
	
	@AfterMethod
	public void afterMethod(){
		executeSqlScript("validation/drop.sql", false);
	}
}
