package be.fabrice.dirty;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashSet;

import org.hibernate.collection.PersistentBag;
import org.hibernate.collection.PersistentSet;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;

import be.fabrice.utils.TransactionalTestBase;

@ContextConfiguration(locations="classpath:dirty/test-dirty-spring.xml")
public class TestDirtyChecking extends TransactionalTestBase {

	@BeforeMethod
	public void initData(){
		Operation operations = sequenceOf(
				deleteAllFrom("DETAIL","DETAILBIS","MASTER"),
				insertInto("MASTER").columns("ID","NAME")
					.values(1000,"Test").build(),
				insertInto("DETAIL").columns("ID","NAME","DETAIL_FK")
					.values(1001,"Detail 1",1000)
					.values(1002,"Detail seul",null)
					.build(),
				insertInto("DETAILBIS").columns("ID","NAME","DETAILBIS_FK")
					.values(2001,"Detail bis 1",1000)
					.values(2002,"Detail bis seul",null)
					.build()
				);
		
		DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), operations);
		dbSetup.launch();
	}
	
	@Test
	public void testDirtyOnHibernateCollectionsForList(){
		Detail d = (Detail) getSession().get(Detail.class, 1002);
		Master m = (Master) getSession().get(Master.class, 1000);
		
		assertThat(m.getDetails()).isInstanceOf(PersistentBag.class);
		PersistentBag bag = (PersistentBag) m.getDetails();
		
		assertThat(bag.isDirty()).isFalse();
		m.getDetails().add(d);
		assertThat(bag.isDirty()).isTrue();
	}
	
	@Test
	public void testDirtyOnList(){
		Detail d = (Detail) getSession().get(Detail.class, 1002);
		Master m = new Master();
		m.setName("test 2");
		assertThat(m.getDetails()).isInstanceOf(ArrayList.class);
		
		getSession().save(m);

		assertThat(m.getDetails()).isInstanceOf(PersistentBag.class); //A été changé
		PersistentBag bag = (PersistentBag) m.getDetails();
		
		assertThat(bag.isDirty()).isFalse();
		m.getDetails().add(d);
		assertThat(bag.isDirty()).isTrue();
		
	}
	
	@Test
	public void testDirtyOnHibernateCollectionsForSet(){
		DetailBis d = (DetailBis) getSession().get(DetailBis.class, 2002);
		Master m = (Master) getSession().get(Master.class, 1000);
		
		assertThat(m.getDetailsBis()).isInstanceOf(PersistentSet.class);
		PersistentSet bag = (PersistentSet) m.getDetailsBis();
		
		assertThat(bag.isDirty()).isFalse();
		m.getDetailsBis().add(d);
		assertThat(bag.isDirty()).isTrue();
	}
	
	@Test
	public void testDirtyOnSet(){
		DetailBis d = (DetailBis) getSession().get(DetailBis.class, 2002);
		Master m = new Master();
		m.setName("test 2");
		assertThat(m.getDetailsBis()).isInstanceOf(HashSet.class);
		
		getSession().save(m);

		assertThat(m.getDetailsBis()).isInstanceOf(PersistentSet.class); //A été changé
		PersistentSet bag = (PersistentSet) m.getDetailsBis();
		
		assertThat(bag.isDirty()).isFalse();
		m.getDetailsBis().add(d);
		assertThat(bag.isDirty()).isTrue();
		
	}
}
