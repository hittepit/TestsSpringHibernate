package be.fabrice.circular;

import static org.testng.Assert.assertEquals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.Test;

@ContextConfiguration(locations="classpath:/circular/test-circular-spring.xml")
public class TestReferenceCiculaire extends AbstractTransactionalTestNGSpringContextTests{
	@Autowired
	private BeanBase beanBase;

	@Autowired
	private ApplicationContext applicationContext;
	
	@Test
	public void find(){
		assertEquals(beanBase.doit(), 0);
	}

	@Test
	public void test() {
		BeanBase beanBase = applicationContext.getBean(BeanBase.class);
		final BeanUn beanUn1 = beanBase.getBeanUn();
		BeanUn beanUn = applicationContext.getBean(BeanUn.class);
		final BeanBase beanBase1 = beanUn.getBeanBase();
	}
}
