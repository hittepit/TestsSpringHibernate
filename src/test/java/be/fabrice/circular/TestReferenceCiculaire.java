package be.fabrice.circular;

import static org.testng.Assert.assertEquals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.Test;

@ContextConfiguration(locations="classpath:/circular/test-circular-spring.xml")
public class TestReferenceCiculaire extends AbstractTransactionalTestNGSpringContextTests{
	@Autowired
	private BeanBase beanBase;
	
	@Test
	public void find(){
		assertEquals(beanBase.doit(), 0);
	}
}
