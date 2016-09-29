package be.fabrice.inner;

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

@ContextConfiguration(locations="classpath:inner/test-spring.xml")
public class TestInnerClassMapping  extends TransactionalTestBase{

	@BeforeMethod
	public void initData(){
		Operation operations = sequenceOf(
				deleteAllFrom("PERSONNE"),
				insertInto("PERSONNE").columns("ID","NOM","nbEnfants","marie")
					.values(1000,"Toto",0,false)
					.values(1001,"Tutu",2, true)
					.build()
				);
		
		DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), operations);
		dbSetup.launch();
	}

	@Test
	public void testFind(){
		Personne p = (Personne) getSession().get(Personne.class, 1000);
		assertNotNull(p);
	}
}
