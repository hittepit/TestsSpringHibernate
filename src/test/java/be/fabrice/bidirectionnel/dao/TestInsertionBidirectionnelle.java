package be.fabrice.bidirectionnel.dao;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.Test;

import be.fabrice.bidirectionnel.dao.EmployeurDao;
import be.fabrice.bidirectionnel.dao.TravailleurDao;
import be.fabrice.bidirectionnel.entities.Employeur;
import be.fabrice.bidirectionnel.entities.Travailleur;

@ContextConfiguration(locations="classpath:test-bidirectionnel-spring.xml")
public class TestInsertionBidirectionnelle extends AbstractTransactionalTestNGSpringContextTests{
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
	
	private class EmployeurRowMapper implements RowMapper<EmployeurSimple>{
	   	 public EmployeurSimple mapRow(ResultSet rs, int rowNum) throws SQLException {
	   		 EmployeurSimple e= new EmployeurSimple();
	   		 e.setId(rs.getInt("ID"));
	   		 e.setName(rs.getString("NOM"));
	   		 return e;
	   	 }
    }
	
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
	public void TestInsertOfBoth(){
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
}
