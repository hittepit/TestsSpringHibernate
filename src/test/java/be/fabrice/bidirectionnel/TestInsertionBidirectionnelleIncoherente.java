package be.fabrice.bidirectionnel;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.ObjectDeletedException;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.utils.TransactionalTestBase;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;

/**
 * L'objectif de ces tests est de montrer certains problème qui peuvent subvenir lorsque le modèle n'est
 * pas cohérent. Notamment, certaines attentes ne seront pas remplies.
 * 
 * @author fabrice.claes
 *
 */
@ContextConfiguration(locations="classpath:bidirectionnel/test-bidirectionnel-spring.xml")
@Test(description="Manipulation de relations bidirectionnelles incohérentes",
		testName="Manipulation de relations bidirectionnelles incohérentes",
		suiteName="Relations bidirectionnelles")
public class TestInsertionBidirectionnelleIncoherente extends TransactionalTestBase {
	
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
	
	@Test(description="Une relation entre deux nouveaux objets, établie seulement du coté many-to-one doit rester incohérente quand Hibernate la sauve")
	public void testInsertOfEmployeurInsertTravailleurIfTravailleurIncoherentButTravailleurRemainsIncoherent(){
		Employeur e = new Employeur();
		e.setName("test");
		Travailleur t = new Travailleur();
		t.setNom("toto");
		List<Travailleur> ts = new ArrayList<Travailleur>();
		ts.add(t);
		
		e.setTravailleurs(ts); // Travailleur attaché à l'employeur, mais pas l'inverse
		
		getSession().saveOrUpdate(e);
		
		assertEquals(countRowsInTable("EMP"),2);
		assertEquals(countRowsInTable("TRAV"),3);
		
		assertNull(t.getEmployeur(), "La relation du travailleur vers l'exmployeur n'existe toujours pas");
	}
	
	@Test(description= "Une relation entre eux nouveaux objets, établie seulement du côté many-to-one doit rester incohérente quand Hibernate la sauve")
	public void testInsertOfBothInsertBothIfEmployeurIncoherentButEmployeurRemainsIncoherent(){
		Employeur e = new Employeur();
		e.setName("test");
		Travailleur t = new Travailleur();
		t.setNom("toto");
		t.setEmployeur(e);
		
		getSession().saveOrUpdate(e);
		getSession().saveOrUpdate(t);
		
		assertEquals(countRowsInTable("EMP"),2);
		assertEquals(countRowsInTable("TRAV"),3);
		
		assertNull(e.getTravailleurs());
	}
	
	@Test(description= "Une relation établie seulement du côté many-to-one doit rester incohérente quand Hibernate la sauve")
	public void testInsertNewTravailleurForExistingEmployeurDoesnotUpdateEmployeur(){
		Employeur e = (Employeur) getSession().get(Employeur.class,1000);
		assertEquals(e.getTravailleurs().size(),2,"Come on... The script said 2 travailleurs...");
		
		Travailleur t = new Travailleur();
		t.setNom("a new one");
		t.setEmployeur(e);
		
		getSession().save(t);
		sessionFactory.getCurrentSession().flush(); //Nécessaire car pas de flush
		
		assertNotNull(t.getId(),"Travailleur should have been inserted");
		assertEquals(e.getTravailleurs().size(),2,"Travailleurs list should not be updated");
	}
	
	@Test
	public void testSaveExistingEmployeurWhenIncoherentNewTravailleurDoesNotCreateAnything(){
		Employeur e = (Employeur) getSession().get(Employeur.class,1000);
		assertEquals(e.getTravailleurs().size(),2,"Come on... The script said 2 travailleurs...");
		
		Travailleur t = new Travailleur();
		t.setNom("a new one");
		t.setEmployeur(e);
		
		getSession().save(e);
		sessionFactory.getCurrentSession().flush();
		
		assertEquals(e.getTravailleurs().size(),2,"Travailleurs list should not be updated");
		assertNull(t.getId(),"New travailleur should not have been inserted");
	}
	
	
	@Test
	public void testIncoherentSuppressionWillThrowException() {
		Employeur e = (Employeur) getSession().get(Employeur.class,1000);
		//Si laissé en lazy, ça pourrait fonctionner, mais il est difficile de savoir si la liste a été initialisée ou non.
		//Ici, on l'initialise
		e.getTravailleurs().isEmpty(); 
		Travailleur t = (Travailleur) getSession().get(Travailleur.class,1002);
		
		getSession().delete(t);
		try{
			sessionFactory.getCurrentSession().flush();
			fail("Should not work because object to be deleted still refrenced in employeur");
		}catch(ObjectDeletedException e1){
			
		}
		
		List<TravailleurDto> travailleurs = jdbcTemplate.query("select * from TRAV where ID=?", new TravailleurDtoRowMapper(), 1002);
		assertEquals(travailleurs.size(),1,"Travailleur should not have been deleted (transaction rollback)");
		assertEquals(e.getTravailleurs().size(),2,"Travailleur must still be in employeur list");
	}

}
