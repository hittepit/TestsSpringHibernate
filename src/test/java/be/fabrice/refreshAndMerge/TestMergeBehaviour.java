package be.fabrice.refreshAndMerge;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;

import be.fabrice.refresh.Entity1;
import be.fabrice.utils.TransactionalTestBase;

@Test(suiteName="Fonctionnement du merge", testName="Tests sur merge")
@ContextConfiguration(locations="classpath:refresh/test-refresh-spring.xml")
public class TestMergeBehaviour extends TransactionalTestBase  {
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

	@Test(description="a transient instance is persisted when merge is called")
	public void testMergeSaveUnsaved(){
		Entity1 e = new Entity1();
		e.setS1("test1");
		e.setS2("test2");
		
		Entity1 persistentE = (Entity1) getSession().merge(e);
		
		assertThat(persistentE.getId()).isNotNull();
		
		List<Entity1> es = getSession().createCriteria(Entity1.class).list();
		
		assertThat(es).hasSize(2);
	}

	@Test(description="a new persistent instance is returned by merge when merge is called with a transient instance")
	public void testMergeSaveUnsavedReturn(){
		Entity1 e = new Entity1();
		e.setS1("test1");
		e.setS2("test2");
		
		Entity1 persistentE = (Entity1) getSession().merge(e);
		
		assertThat(persistentE.getId()).isNotNull();

		assertThat(persistentE != e).isTrue();
		
		assertThat(e.getId()).isNull();
	}

	@Test(description="a detached persistent instance is saved and a new attached persistent instance is returned when merge is called")
	public void testMergeSavePersistentDetached(){
		Entity1 e = new Entity1();
		e.setId(1000);
		e.setS1("test1");
		e.setS2("test2");
		
		Entity1 persistentE = (Entity1) getSession().merge(e);

		assertThat(persistentE != e).isTrue();
		
		assertThat(persistentE.getS1()).isEqualTo("test1");
		
		List<Entity1> es = getSession().createCriteria(Entity1.class).list();
		assertThat(es).hasSize(1); //Sanity check
		
		assertThat(es.get(0).getS1()).isEqualTo("test1");
	}

	@Test(description="a managed persistent instance is updated and is returned when merge is called")
	public void testMergeSavePersistentManaged(){
		Entity1 e = (Entity1) getSession().createQuery("from Entity1 e where e.id = :id").setParameter("id", 1000).uniqueResult();
		e.setS1("test1");
		e.setS2("test2");
		
		Entity1 persistentE = (Entity1) getSession().merge(e);

		assertThat(persistentE == e).isTrue();
		
		assertThat(persistentE.getS1()).isEqualTo("test1");
		
		List<Entity1> es = getSession().createCriteria(Entity1.class).list();
		assertThat(es).hasSize(1); //Sanity check
		
		assertThat(es.get(0).getS1()).isEqualTo("test1");
	}
}
