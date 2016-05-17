package be.fabrice.refresh;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static com.ninja_squad.dbsetup.Operations.sql;
import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;

import be.fabrice.utils.TransactionalTestBase;

@Test(suiteName="Fonctionnement du refresh", testName="Tests sur refresh")
@ContextConfiguration(locations="classpath:refresh/test-refresh-spring.xml")
public class TestRefreshBehaviour extends TransactionalTestBase  {
	@Autowired
	private DataSource dataSource;
	
	@BeforeMethod
	public void beforeTest(){
		Operation deletes = deleteAllFrom("entity1");
		Operation categories = insertInto("entity1").columns("id","s1","s2")
				.values("1000","Test", "Test")
				.build();
		
		Operation operation = sequenceOf(deletes,categories);
		
		DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), operation);
		dbSetup.launch();
	}

	@Test(description="the state must be reload when refresh is called")
	public void testSimpleRefresh(){
		Entity1 e = (Entity1) getSession().get(Entity1.class, 1000);
		assertThat(e.getS2()).isEqualTo("Test");
		
		DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), 
				sql("update entity1 set s2='test2' where id=1000"));
		dbSetup.launch();
		
		getSession().refresh(e);
		
		assertThat(e.getS2()).isEqualTo("test2");
	}

	@Test(description="the new state must lost when refresh is called")
	public void testRefreshAfterChange(){
		Entity1 e = (Entity1) getSession().get(Entity1.class, 1000);
		assertThat(e.getS1()).isEqualTo("Test");
		assertThat(e.getS2()).isEqualTo("Test");
		
		e.setS1("test1");
		
		DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), 
				sql("update entity1 set s2='test2' where id=1000"));
		dbSetup.launch();
		
		getSession().refresh(e);
		
		assertThat(e.getS1()).isEqualTo("Test");
		assertThat(e.getS2()).isEqualTo("test2");
	}
}
