package be.fabrice.bidirectionnel;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.utils.TransactionalTestBase;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;

@ContextConfiguration(locations="classpath:bidirectionnel/test-bidirectionnel-spring.xml")
@Test(description="Manipulation dangereuse de relations bidirectionnelles",
		testName="Manipulation dangereuse de relations bidirectionnelles",
		suiteName="Relations bidirectionnelles")
public class TestBadMethodBehaviour extends TransactionalTestBase {
	
	@BeforeMethod
	public void initTestData(){
		Operation operations = sequenceOf(
				deleteAllFrom("TRAV","EMP"),
				insertInto("EMP").columns("ID","NOM").values(1000,"Anybody").build(),
				insertInto("TRAV").columns("ID","NOM","EMP_ID")
					.values(1001,"Happy one",1000)
					.values(1002,"Sad one",1000)
					.build()
		);
		
		DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), operations);
		dbSetup.launch();
	}

	@Test
	public void testBadMethod(){
		Employeur e = (Employeur) getSession().get(Employeur.class, 1000);
		
		assertThat(e.getTravailleurs()).hasSize(2);
		
		e.badMethodRemoveTravailleurWithName("Happy one");
		
		Travailleur t = (Travailleur) getSession().createQuery("from Travailleur t where t.nom = :name").setParameter("name", "Happy one").uniqueResult();

		assertThat(t).isNull();
		assertThat(e.getTravailleurs()).hasSize(1);
	}
}
