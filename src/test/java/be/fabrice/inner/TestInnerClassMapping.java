package be.fabrice.inner;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertTrue;

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
				deleteAllFrom("PERSONNE","SITUATION2","PERSONNE2"),
				insertInto("PERSONNE2").columns("ID","NOM")
					.values(1000,"Toto")
					.values(1001,"Tutu")
					.build(),
				insertInto("SITUATION2").columns("ID","nbEnfants","marie", "personne_fk")
					.values(2000,0,false,1000)
					.values(2001,2, true,1001)
					.build(),
				insertInto("PERSONNE").columns("ID","NOM","nbEnfants","marie")
					.values(1000,"Toto",0,false)
					.values(1001,"Tutu",2, true)
					.build()
				);
		
		DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), operations);
		dbSetup.launch();
	}

	@Test
	public void testFindProtectedClass(){
		Personne p = (Personne) getSession().get(Personne.class, 1000);
		assertFalse(p.getSituation().isMarie());
		
		p = (Personne) getSession().get(Personne.class, 1001);
		assertTrue(p.getSituation().isMarie());
	}

	@Test
	public void testUpdateProtectedClass(){
		Personne p = (Personne) getSession().get(Personne.class, 1000);
		p.setNbEnfants(3);
		
		getSession().flush();
		getSession().clear();
		
		Personne p2 = (Personne) getSession().get(Personne.class, 1000);
		
		assertNotSame(p, p2, "Avec un clear, c'est normal");
		
		assertEquals(p2.getSituation().getNbEnfants(), 3);
	}

	@Test
	public void testFindInnerClass(){
		Personne2 p = (Personne2) getSession().get(Personne2.class, 1000);
		assertFalse(p.getSituation().isMarie());
		
		p = (Personne2) getSession().get(Personne2.class, 1001);
		assertTrue(p.getSituation().isMarie());
	}

	@Test
	public void testUpdateInnerClass(){
		Personne2 p = (Personne2) getSession().get(Personne2.class, 1000);
		p.setNbEnfants(3);
		
		getSession().flush();
		getSession().clear();
		
		Personne2 p2 = (Personne2) getSession().get(Personne2.class, 1000);
		
		assertNotSame(p, p2, "Avec un clear, c'est normal");
		
		assertEquals(p2.getSituation().getNbEnfants(), 3);
	}
}
