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

import be.fabrice.bidirectionnel.entity.Employeur;
import be.fabrice.bidirectionnel.entity.Travailleur;

/**
 * L'objectif de ce test est de démontrer le fonctionnement d'une relation bidirectionnelle et en particulier
 * qu'Hibernate fonctionne nettement mieux car le modèle est cohérent.
 * 
 * Un problème assez courant est que le modèle n'est pas cohérent et que les ajouts ou suppressions, sont faites
 * malgré tout.
 * 
 * @author fabrice.claes
 *
 */
@ContextConfiguration(locations="classpath:bidirectionnel/test-bidirectionnel-spring.xml")
public class TestInsertionBidirectionnelle extends AbstractTransactionalTestNGSpringContextTests{
	/**
	 * <p>Petite classe utilitaire pour contenir les données reçues en JDBC pour un employeur.</p>
	 * <p>L'entité {@link Employeur} aurait pu être utilisée dans ce cas-ci, mais ce n'est pas propre.
	 * De plus, l'entité {@link Employeur} contient une liste de {@link Travailleur}. Dans ce cas simple
	 * d'utilisation, cette liste ne serait utilisée.</p>
	 * @author fabrice.claes
	 *
	 */
	private class EmployeurSimple{
		private Integer id;
		private String name;
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
	
	/**
	 * <p>Petite classe utilitaire pour contenur les données JDBC d'un travailleur.</p>
	 * <p>Cette classe est préférée à l'entité car cette dernière contient un lien vers un {@link Employeur}
	 * alors que le JDBC renvoie juste la foreign key. Restons simple.</p>
	 * @author fabrice.claes
	 *
	 */
	private class TravailleurSimple{
		private Integer id;
		private String name;
		private Integer employeurId;
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Integer getEmployeurId() {
			return employeurId;
		}
		public void setEmployeurId(Integer employeurId) {
			this.employeurId = employeurId;
		}
	}
	
	/**
	 * RowMapper pour mapper (bien la lapalissade...) les données d'un ResultSet vers un {@link EmployeurSimple}.
	 * Utilisé dans les tests.
	 * @author fabrice.claes
	 *
	 */
	private class EmployeurRowMapper implements RowMapper<EmployeurSimple>{
	   	 public EmployeurSimple mapRow(ResultSet rs, int rowNum) throws SQLException {
	   		 EmployeurSimple e= new EmployeurSimple();
	   		 e.setId(rs.getInt("ID"));
	   		 e.setName(rs.getString("NOM"));
	   		 return e;
	   	 }
    }
	
	/**
	 * RowMapper pour mapper les données d'un ResultSet vers un {@link TravailleurSimple}.
	 * Utilisé dans les tests.
	 * @author fabrice.claes
	 *
	 */
	private class TravailleurRowMapper implements RowMapper<TravailleurSimple>{
	   	 public TravailleurSimple mapRow(ResultSet rs, int rowNum) throws SQLException {
	   		 TravailleurSimple t= new TravailleurSimple();
	   		 t.setId(rs.getInt("ID"));
	   		 t.setName(rs.getString("NOM"));
	   		 Object fk = rs.getObject("EMP_ID");
	   		 t.setEmployeurId(fk==null?null:(Integer)fk);
	   		 return t;
	   	 }
    }

	@Autowired
	private EmployeurDao employeurDao;

	@Autowired
	private TravailleurDao travailleurDao;
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Test
	public void TestInsertEmployeur() {
		Employeur e = new Employeur();
		e.setName("test");
		employeurDao.save(e);
		
		assertNotNull(e.getId());
		assertEquals(countRowsInTable("EMP"),1);
		
		List<EmployeurSimple> employeurs = jdbcTemplate.query("select * from EMP where ID=?", new EmployeurRowMapper(), e.getId());
		assertEquals(employeurs.size(),1);
		EmployeurSimple first = employeurs.get(0);
		assertEquals(first.getId(),e.getId());
		assertEquals(first.getName(),"test");
	}
	
	@Test
	public void TestInsertTravailleur() {
		Travailleur t = new Travailleur();
		t.setNom("toto");
		travailleurDao.save(t);
		
		assertNotNull(t.getId());
		assertEquals(countRowsInTable("TRAV"),1);
		
		List<TravailleurSimple> travailleurs = jdbcTemplate.query("select * from TRAV where ID=?", new TravailleurRowMapper(), t.getId());
		assertEquals(travailleurs.size(),1);
		TravailleurSimple first = travailleurs.get(0);
		assertEquals(first.getId(),t.getId());
		assertEquals(first.getName(),"toto");
		assertNull(first.getEmployeurId());
	}
	
	@Test
	public void TestInsertOfBothCascading(){
		Employeur e = new Employeur();
		e.setName("test");
		Travailleur t = new Travailleur();
		t.setNom("toto");
		e.addTravailleur(t);
		
		employeurDao.save(e);
		
		assertEquals(countRowsInTable("EMP"),1);
		assertEquals(countRowsInTable("TRAV"),1);
		
		List<TravailleurSimple> travailleurs = jdbcTemplate.query("select * from TRAV where ID=?", new TravailleurRowMapper(), t.getId());
		assertEquals(travailleurs.size(),1);
		TravailleurSimple travailleur = travailleurs.get(0);
		assertEquals(travailleur.getEmployeurId(),e.getId());
	}
	
	@Test
	public void TestAddTravailleurToExistingEmployeur(){
		executeSqlScript("bidirectionnel/test-script.sql", false);
		
		Employeur e = employeurDao.find(1000);
		
		Travailleur t = new Travailleur();
		t.setNom("a new one");
		e.addTravailleur(t);
		
		employeurDao.save(e);
		sessionFactory.getCurrentSession().flush(); //Nécessaire car pas de flush
		
		List<TravailleurSimple> travailleurs = jdbcTemplate.query("select * from TRAV where ID=?", new TravailleurRowMapper(), t.getId());
		assertEquals(travailleurs.size(),1);
		TravailleurSimple first = travailleurs.get(0);
		assertEquals(first.getId(),t.getId());
		assertEquals(first.getName(),"a new one");
		assertEquals(first.getEmployeurId(),Integer.valueOf(1000));
	}
	
	@Test
	public void TestInsertOfEmployeurInsertTravailleurIfTravailleurIncoherentButTravailleurRemainsIncoherent(){
		Employeur e = new Employeur();
		e.setName("test");
		final Travailleur t = new Travailleur();
		t.setNom("toto");
		List<Travailleur> ts = new ArrayList<Travailleur>(){
			{add(t);}
		};
		e.setTravailleurs(ts);
		
		employeurDao.save(e);
		
		assertEquals(countRowsInTable("EMP"),1);
		assertEquals(countRowsInTable("TRAV"),1);
		
		assertNull(t.getEmployeur());
	}
	
	@Test
	public void TestInsertOfBothInsertBothIfEmployeurIncoherentButEmployeurRemainsIncoherent(){
		Employeur e = new Employeur();
		e.setName("test");
		Travailleur t = new Travailleur();
		t.setNom("toto");
		t.setEmployeur(e);
		
		employeurDao.save(e);
		travailleurDao.save(t);
		
		assertEquals(countRowsInTable("EMP"),1);
		assertEquals(countRowsInTable("TRAV"),1);
		
		assertNull(e.getTravailleurs());
	}
	
	@Test
	public void TestInsertNewTravailleurForExistingEmployeurDoesnotUpdateEmployeur(){
		executeSqlScript("bidirectionnel/test-script.sql", false);
		
		Employeur e = employeurDao.find(1000);
		assertEquals(e.getTravailleurs().size(),2,"Come on... The script said 2 travailleurs...");
		
		Travailleur t = new Travailleur();
		t.setNom("a new one");
		t.setEmployeur(e);
		
		travailleurDao.save(t);
		sessionFactory.getCurrentSession().flush(); //Nécessaire car pas de flush
		
		assertNotNull(t.getId(),"Travailleur should have been inserted");
		assertEquals(e.getTravailleurs().size(),2,"Travailleurs list should not be updated");
	}
	
	@Test
	public void TestSaveExistingEmployeurWhenIncoherentNewTravailleurDoesNotCreateAnything(){
		executeSqlScript("bidirectionnel/test-script.sql", false);
		
		Employeur e = employeurDao.find(1000);
		assertEquals(e.getTravailleurs().size(),2,"Come on... The script said 2 travailleurs...");
		
		Travailleur t = new Travailleur();
		t.setNom("a new one");
		t.setEmployeur(e);
		
		employeurDao.save(e);
		sessionFactory.getCurrentSession().flush();
		
		assertEquals(e.getTravailleurs().size(),2,"Travailleurs list should not be updated");
		assertNull(t.getId(),"New travailleur should not have been inserted");
	}
	
	@Test
	public void TestSuppressionOfTravailleurInEmployeurResultsInTravailleurDeletion(){
		executeSqlScript("bidirectionnel/test-script.sql", false);
		
		Employeur e = employeurDao.find(1000);
		Travailleur t = travailleurDao.find(1002);
		
		e.removeTravailleur(t);
		
		employeurDao.save(e);
		sessionFactory.getCurrentSession().flush();
		
		List<TravailleurSimple> travailleurs = jdbcTemplate.query("select * from TRAV where ID=?", new TravailleurRowMapper(), 1002);
		assertTrue(travailleurs.isEmpty(),"Travailleur should have been deleted");
	}
	
	@Test
	public void testIncoherentSuppressionWillThrowException() {
		executeSqlScript("bidirectionnel/test-script.sql", false);
		
		Employeur e = employeurDao.find(1000);
		//Si laissé en lazy, ça pourrait fonctionner, mais il est difficile de savoir si la liste a été initialisée ou non.
		//Ici, on l'initialise
		e.getTravailleurs().isEmpty(); 
		Travailleur t = travailleurDao.find(1002);
		
		travailleurDao.delete(t);
		try{
			sessionFactory.getCurrentSession().flush();
			fail("Should not work because object to be deleted still refrenced in employeur");
		}catch(ObjectDeletedException e1){
			
		}
		
		List<TravailleurSimple> travailleurs = jdbcTemplate.query("select * from TRAV where ID=?", new TravailleurRowMapper(), 1002);
		assertEquals(travailleurs.size(),1,"Travailleur should not have been deleted (transaction rollback)");
		assertEquals(e.getTravailleurs().size(),2,"Travailleur must still be in employeur list");
	}
}
