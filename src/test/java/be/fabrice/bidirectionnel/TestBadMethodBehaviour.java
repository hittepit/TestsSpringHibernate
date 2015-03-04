package be.fabrice.bidirectionnel;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.FlushMode;
import org.hibernate.PropertyValueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.utils.MockFlushEntityListener;
import be.fabrice.utils.MockSessionFlushListener;
import be.fabrice.utils.TransactionalTestBase;
import be.fabrice.utils.logging.SimpleSql;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;

@ContextConfiguration(locations="classpath:bidirectionnel/test-bidirectionnel-spring.xml")
@Test(description="Manipulation dangereuse de relations bidirectionnelles",
		testName="Manipulation dangereuse de relations bidirectionnelles",
		suiteName="Relations bidirectionnelles")
public class TestBadMethodBehaviour extends TransactionalTestBase {
	@Autowired
	private MockSessionFlushListener mockSessionFlushListener;
	@Autowired
	private MockFlushEntityListener mockFlushEntityListener;
	
	private FlushMode flushMode;
	
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
		
		mockSessionFlushListener.resetInvocation();
		mockFlushEntityListener.resetInvocation();
		
		flushMode = getSession().getFlushMode();
		getSession().setFlushMode(FlushMode.AUTO); //Garantit le flush mode
	}
	
	@AfterMethod
	public void restoreFlushMode(){
		getSession().setFlushMode(flushMode);
	}

	@Test(description="everything must work when new travailleur added, not persisted, then removed, and session flushed")
	public void noInsertionButRemovedThenFlushedIsOk(){
		Employeur e = (Employeur) getSession().get(Employeur.class, 1000);
		
		SimpleSql.reinitSqlList();
		
		Travailleur newT = new Travailleur();
		newT.setNom("Nouveau");
		e.addTravailleur(newT);
		
		e.clearTravailleurs();
		
		assertThat(mockFlushEntityListener.getInvocation()).isEqualTo(0); //No entity flushed
		
		getSession().flush(); //Force Update DB
		
		System.out.println(SimpleSql.getSqlList());
		
		assertThat(SimpleSql.contains("delete from TRAV where .*")).isTrue();
		
		assertThat(getSession().createCriteria(Travailleur.class).list()).isEmpty();
	}

	@Test(expectedExceptions=PropertyValueException.class, 
			description="no orphan delete must be casted when new travailleur is added, persisted because accidental flush, then removed")
	public void testBadMethodWithAccidentalWithNoUpdatesContainerEntity(){
		Employeur e = (Employeur) getSession().get(Employeur.class, 1000);

		SimpleSql.reinitSqlList();
		
		Travailleur newT = new Travailleur();
		newT.setNom("Nouveau");
		e.addTravailleur(newT);
		
		getSession().createQuery("from Employeur t where t.name = :name").setParameter("name", "None").list();
		
		//Il y a bien eu un flush des 4 entités en session 
		assertThat(mockFlushEntityListener.getInvocation()).isEqualTo(4);
		
		//requêtes exécutées: le select qui provoque le flush + une insertion et rien d'autre
		assertThat(SimpleSql.getSqlList()).hasSize(2);
		assertThat(SimpleSql.contains("insert into TRAV .*")).isTrue();
		assertThat(SimpleSql.contains("select .* from EMP .*")).isTrue();
		
		e.clearTravailleurs();
		
		getSession().flush(); //Force update DB -> exception
	}

	@Test(expectedExceptions=PropertyValueException.class)
	public void testBadMethodWithAccidentalWithNoUpdatesContainerEntityButManualWithdrawOfTravailleur(){
		Employeur e = (Employeur) getSession().get(Employeur.class, 1000);

		SimpleSql.reinitSqlList();
		
		Travailleur newT = new Travailleur();
		newT.setNom("Nouveau");
		e.addTravailleur(newT);
		
		getSession().createQuery("from Employeur t where t.name = :name").setParameter("name", "None").list();
		
		//Il y a bien eu un flush des 4 entités en session 
		assertThat(mockFlushEntityListener.getInvocation()).isEqualTo(4);
		
		//requêtes exécutées: le select qui provoque le flush + une insertion et rien d'autre
		assertThat(SimpleSql.getSqlList()).hasSize(2);
		assertThat(SimpleSql.contains("insert into TRAV .*")).isTrue();
		assertThat(SimpleSql.contains("select .* from EMP .*")).isTrue();
		
		List<Travailleur> ts = new ArrayList<Travailleur>(e.getTravailleurs());
		for(Travailleur t:ts){
			e.removeTravailleur(t); //=e.clearTravailleurs()
		}
		
		getSession().flush(); //Force update DB -> exception
	}

	@Test
	public void testBadMethodWithAccidentalFlushThatUpdatesContainerEntity(){
		Employeur e = (Employeur) getSession().get(Employeur.class, 1000);
		
		Travailleur newT = new Travailleur();
		newT.setNom("Nouveau");
		e.addTravailleur(newT);
		
		e.setName("other");

		getSession().createQuery("from Employeur t where t.name = :name").setParameter("name", "None").uniqueResult();
		
		//Il y a bien eu un flush des 4 entités en session -> une insertion, un update
		assertThat(mockFlushEntityListener.getInvocation()).isEqualTo(4);
		
		e.clearTravailleurs();
		
		getSession().flush(); //Force update DB
	}

	@Test
	public void testBadMethodManualFlushNoUpdateOfContainer(){
		Employeur e = (Employeur) getSession().get(Employeur.class, 1000);
		
		Travailleur newT = new Travailleur();
		newT.setNom("Nouveau");
		e.addTravailleur(newT);
		
		getSession().flush();
		
		e.clearTravailleurs();
		
		getSession().flush();
	}

	@Test
	public void testBadMethodManualFlushWithUpdateOfContainer(){
		Employeur e = (Employeur) getSession().get(Employeur.class, 1000);
		
		Travailleur newT = new Travailleur();
		newT.setNom("Nouveau");
		e.addTravailleur(newT);
		
		e.setName("toto");
		
		getSession().flush();
		
		e.clearTravailleurs();
		
		getSession().flush();
	}

	@Test(expectedExceptions=PropertyValueException.class)
	public void testWithManualInsertOfTravailleurAccidentalFlushNoUpdateOfContainer(){
		Employeur e = (Employeur) getSession().get(Employeur.class, 1000);
		
		Travailleur newT = new Travailleur();
		newT.setNom("Nouveau");
		e.addTravailleur(newT);
		
		getSession().save(newT);
		
		getSession().createQuery("from Employeur t where t.name = :name").setParameter("name", "None").uniqueResult();
		
		e.clearTravailleurs();
		
		getSession().flush();
	}

	@Test
	public void testWithManualInsertOfTravailleurAccidentalFlushUpdateOfContainer(){
		Employeur e = (Employeur) getSession().get(Employeur.class, 1000);
		
		Travailleur newT = new Travailleur();
		newT.setNom("Nouveau");
		e.addTravailleur(newT);
		
		getSession().save(newT);
		
		e.setName("autre");
		
		getSession().createQuery("from Employeur t where t.name = :name").setParameter("name", "None").uniqueResult();
		
		e.clearTravailleurs();
		
		getSession().flush();
	}
	
	//TODO tester en vidant partiellement les travailleurs les tests qui échouent
	//TODO tester en vidant proprement
}
