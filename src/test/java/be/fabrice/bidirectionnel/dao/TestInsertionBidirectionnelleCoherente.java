package be.fabrice.bidirectionnel.dao;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.ObjectDeletedException;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.Test;

import be.fabrice.bidirectionnel.Employeur;
import be.fabrice.bidirectionnel.Travailleur;
import be.fabrice.utils.TransactionalTestBase;

/**
 * L'objectif de ce test est de démontrer qu'une relation bidirectionnelle cohérente
 * facilite le travail d'Hibernate.
 * 
 * @author fabrice.claes
 *
 */
@ContextConfiguration(locations="classpath:bidirectionnel/test-bidirectionnel-spring.xml")
@Test(description="Insertion de relations bidirectionnelles cohérentes",
		testName="Insertion avec les relations bidirectionnelles cohérentes",
		suiteName="Relations bidirectionnelles")
public class TestInsertionBidirectionnelleCoherente extends TransactionalTestBase{
	@Test(description="New container and element must both be persisted when correctly related and cascading is definied")
	public void testInsertOfBothCascading(){
		Employeur e = new Employeur();
		e.setName("test");
		Travailleur t = new Travailleur();
		t.setNom("toto");
		e.addTravailleur(t);
		
		getSession().save(e);
		
		assertEquals(countRowsInTable("EMP"),1);
		assertEquals(countRowsInTable("TRAV"),1);
		
		List<TravailleurDto> travailleurs = jdbcTemplate.query("select * from TRAV where ID=?", new TravailleurDtoRowMapper(), t.getId());
		assertEquals(travailleurs.size(),1);
		TravailleurDto travailleur = travailleurs.get(0);
		assertEquals(travailleur.getEmployeurId(),e.getId());
	}
	
	@Test
	public void testAddTravailleurToExistingEmployeur(){
		executeSqlScript("bidirectionnel/test-script.sql", false);
		
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
	
	@Test
	public void testSuppressionOfTravailleurInEmployeurResultsInTravailleurDeletion(){
		executeSqlScript("bidirectionnel/test-script.sql", false);
		
		Employeur e = (Employeur) getSession().get(Employeur.class,1000);
		Travailleur t = (Travailleur) getSession().get(Travailleur.class, 1002);
		
		e.removeTravailleur(t);
		
		getSession().saveOrUpdate(e);
		getSession().flush();
		
		List<TravailleurDto> travailleurs = jdbcTemplate.query("select * from TRAV where ID=?", new TravailleurDtoRowMapper(), 1002);
		assertTrue(travailleurs.isEmpty(),"Travailleur should have been deleted");
	}
}
