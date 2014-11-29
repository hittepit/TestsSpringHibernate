package be.fabrice.bidirectionnel;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;

import be.fabrice.utils.TransactionalTestBase;

/**
 * L'objectif de ce test est de démontrer qu'une relation bidirectionnelle cohérente
 * facilite le travail d'Hibernate.
 * 
 * @author fabrice.claes
 *
 */
@ContextConfiguration(locations="classpath:bidirectionnel/test-bidirectionnel-spring.xml")
@Test(description="Manipulation de relations bidirectionnelles cohérentes",
		testName="Manipulation de relations bidirectionnelles cohérentes",
		suiteName="Relations bidirectionnelles")
public class TestInsertionBidirectionnelleCoherente extends TransactionalTestBase{
	
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
	
	@Test(description="New employer and new worker, correctly related must both be persisted when saving employer and cascading is definied")
	public void testInsertOfBothCascading(){
		Employeur e = new Employeur();
		e.setName("test");
		Travailleur t = new Travailleur();
		t.setNom("toto");
		e.addTravailleur(t); // Etablit correctement la relation bidirectionnelle
		
		getSession().save(e);
		
		List<TravailleurDto> travailleurs = jdbcTemplate.query("select * from TRAV where ID=?", new TravailleurDtoRowMapper(), t.getId());
		assertEquals(travailleurs.size(),1);
		TravailleurDto travailleur = travailleurs.get(0);
		assertEquals(travailleur.getEmployeurId(),e.getId());
	}
	
	@Test(description="New worker added to an existing employer must be persisted when saving employer and cascading is defined")
	public void testAddTravailleurToExistingEmployeur(){
		Employeur e = (Employeur) getSession().get(Employeur.class,1000);
		
		Travailleur t = new Travailleur();
		t.setNom("a new one");
		e.addTravailleur(t);
		
		getSession().saveOrUpdate(e);
		getSession().flush(); //Nécessaire car pas de flush
		
		List<TravailleurDto> travailleurs = jdbcTemplate.query("select * from TRAV where ID=?", new TravailleurDtoRowMapper(), t.getId());
		assertEquals(travailleurs.size(),1);
		TravailleurDto first = travailleurs.get(0);
		assertEquals(first.getId(),t.getId());
		assertEquals(first.getName(),"a new one");
		assertEquals(first.getEmployeurId(),Integer.valueOf(1000));
	}
	
	@Test(description="Worker correctly removed from employer must be deleted when 'delete orphans' is definied")
	public void testSuppressionOfTravailleurInEmployeurResultsInTravailleurDeletion(){
		Employeur e = (Employeur) getSession().get(Employeur.class,1000);
		Travailleur t = (Travailleur) getSession().get(Travailleur.class, 1002);
		
		e.removeTravailleur(t); //Retire correctement la relation bidirectionnelle
		
		getSession().saveOrUpdate(e);
		getSession().flush();
		
		List<TravailleurDto> travailleurs = jdbcTemplate.query("select * from TRAV where ID=?", new TravailleurDtoRowMapper(), 1002);
		assertTrue(travailleurs.isEmpty(),"Travailleur should have been deleted");
	}
}
