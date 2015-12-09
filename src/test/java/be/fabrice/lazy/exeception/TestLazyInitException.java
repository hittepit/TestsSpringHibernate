package be.fabrice.lazy.exeception;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.lazy.exception.Master;
import be.fabrice.utils.TransactionalTestBase;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;

@ContextConfiguration(locations="classpath:lazy/exception/test-spring.xml")
public class TestLazyInitException extends TransactionalTestBase{
	
	@BeforeMethod
	public void initData(){
		deleteAllFrom("detail","master");
		Operation operation = sequenceOf(
			insertInto("master")
				.columns("id","name")
				.values(100L,"master1")
				.values(101L,"master2")
				.build(),
			insertInto("detail")
				.columns("id","name","master_id")
				.values(1000L,"detail1-1",100L)
				.values(1001L,"detail1-2",100L)
				.values(1002L,"detail1-3",100L)
				.values(1003L,"detail1-4",100L)
				.values(1004L,"detail1-5",100L)
				.values(1005L,"detail2-1",101L)
				.values(1006L,"detail2-2",101L)
				.values(1007L,"detail2-3",101L)
				.build()
				);
		DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), operation);
		dbSetup.launch();
	}
	
	@Test
	public void testOK(){
		List<Master> masters = getSession().createQuery("from Master").list();
		assertThat(masters).hasSize(2);
		
		for(Master m:masters){
			assertThat(Hibernate.isInitialized(m.getDetails())).isTrue();
			if(m.getId().equals(100L)){
				assertThat(m.getDetails()).hasSize(5);
			}
		}
	}
}
