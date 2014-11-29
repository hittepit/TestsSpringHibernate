package be.fabrice.bidirectionnel.dao;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.ObjectDeletedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.Test;

import be.fabrice.bidirectionnel.Employeur;
import be.fabrice.bidirectionnel.Travailleur;
import be.fabrice.utils.TransactionalTestBase;

/**
 * L'objectif de ces tests est de montrer certains problème qui peuvent subvenir lorsque le modèle n'est
 * pas cohérent. Notamment, certaines attentes ne seront pas remplies.
 * 
 * @author fabrice.claes
 *
 */
@ContextConfiguration(locations="classpath:bidirectionnel/test-bidirectionnel-spring.xml")
@Test(description="Insertion avec des relations bidirectionnelles incohérentes",
		testName="Insertion de relations bidirectionnelles incohérentes",
		suiteName="Relations bidirectionnelles")
public class TestInsertionBidirectionnelleIncoherente extends TransactionalTestBase {
	
	@Test
	public void testInsertOfEmployeurInsertTravailleurIfTravailleurIncoherentButTravailleurRemainsIncoherent(){
		Employeur e = new Employeur();
		e.setName("test");
		final Travailleur t = new Travailleur();
		t.setNom("toto");
		List<Travailleur> ts = new ArrayList<Travailleur>(){
			{add(t);}
		};
		e.setTravailleurs(ts);
		
		getSession().saveOrUpdate(e);
		
		assertEquals(countRowsInTable("EMP"),1);
		assertEquals(countRowsInTable("TRAV"),1);
		
		assertNull(t.getEmployeur());
	}
	
	@Test
	public void testInsertOfBothInsertBothIfEmployeurIncoherentButEmployeurRemainsIncoherent(){
		Employeur e = new Employeur();
		e.setName("test");
		Travailleur t = new Travailleur();
		t.setNom("toto");
		t.setEmployeur(e);
		
		getSession().saveOrUpdate(e);
		getSession().saveOrUpdate(t);
		
		assertEquals(countRowsInTable("EMP"),1);
		assertEquals(countRowsInTable("TRAV"),1);
		
		assertNull(e.getTravailleurs());
	}
	
	@Test
	public void testInsertNewTravailleurForExistingEmployeurDoesnotUpdateEmployeur(){
		executeSqlScript("bidirectionnel/test-script.sql", false);
		
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
		executeSqlScript("bidirectionnel/test-script.sql", false);
		
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
		executeSqlScript("bidirectionnel/test-script.sql", false);
		
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
