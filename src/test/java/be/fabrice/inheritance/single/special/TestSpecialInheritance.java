package be.fabrice.inheritance.single.special;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.util.List;

import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.inheritance.single.special.entity.ClassA;
import be.fabrice.inheritance.single.special.entity.CommentA;
import be.fabrice.utils.TransactionalTestBase;

@Test(suiteName="Héritage", testName="Single table, cas particulier")
@ContextConfiguration("classpath:inheritance/single/special/test-inheritance-spring.xml")
public class TestSpecialInheritance extends TransactionalTestBase {
	
	@BeforeMethod
	public void beforeMethod(){
		executeSqlScript("inheritance/single/special/test-script.sql", false);
	}

	@Test
	public void testGetAllCommentA(){
		List<CommentA> cs = getSession().createQuery("from CommentA").list();
		assertEquals(cs.size(), 3);
	}
	
	@Test
	public void testFindClassADoesNotWork(){
		ClassA a = (ClassA) getSession().get(ClassA.class,1000L);
		assertNotNull(a);
		assertEquals(a.getComments().size(),3,"But should be 2");
		
		for(CommentA ca:a.getComments()){
			if(ca.getId().equals(2003L)){
				assertNull(ca.getComment(),"Of course, that column is null since this is a CommentB");
			}
		}
	}
}
