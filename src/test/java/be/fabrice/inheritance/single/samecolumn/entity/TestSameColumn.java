package be.fabrice.inheritance.single.samecolumn.entity;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.testng.Assert.assertNotNull;

import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;

import be.fabrice.utils.TransactionalTestBase;

@ContextConfiguration(locations="classpath:inheritance/single/samecolumn/test-inheritance-spring.xml")
public class TestSameColumn extends TransactionalTestBase {
	@BeforeMethod
	public void initData(){
		Operation operations = sequenceOf(
				deleteAllFrom("PARENT"),
				insertInto("PARENT").columns("ID","type","commonValue", "common", "first", "second")
					.values(1001,'F',0,1,2,null)
					.values(1002,'S',10,11,null,12)
					.build()
				);
		
		DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), operations);
		dbSetup.launch();
	}
	
	@Test
	public void test(){
		FirstChild f = (FirstChild) getSession().get(FirstChild.class, 1001);
		assertNotNull(f);
	}
}
