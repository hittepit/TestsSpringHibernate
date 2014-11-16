package be.fabrice.nested.dao;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.nested.entity.Facture;
import be.fabrice.nested.entity.Ligne;

@ContextConfiguration(locations="classpath:nested/test-nested-spring.xml")
public class TestNestedTransaction extends AbstractTransactionalTestNGSpringContextTests{
	class FactureRowMapper implements RowMapper<Facture>{
		public Facture mapRow(ResultSet rs, int arg) throws SQLException {
			Facture f = new Facture();
			f.setId(rs.getLong("id"));
			f.setnLignes(rs.getInt("nlignes"));
			f.setNum(rs.getString("num"));
			return f;
		}
	}
	
	class LigneRowMapper implements RowMapper<Ligne>{
		public Ligne mapRow(ResultSet rs, int arg1) throws SQLException {
			Ligne l = new Ligne();
			l.setId(rs.getLong("id"));
			l.setPrice(rs.getDouble("price"));
			l.setQuantity(rs.getInt("quantity"));
			Long fk = rs.getLong("FACT_ID");
			l.setFactureId(rs.wasNull()?null:fk);
			return l;
		}
	}
	
	@Autowired
	private FactureService factureService;
	@Autowired
	private SessionFactory sessionFactory;
	
	@BeforeMethod
	public void beforeMethod(){
		deleteFromTables("LIGNE");
		deleteFromTables("FACT");
	}
	
	@Test
	public void testFactureAndLigneMustBeCreated(){
		Facture facture = new Facture();
		facture.setNum("11111");
		List<Ligne> lignes = new ArrayList<Ligne>();
		Ligne l = new Ligne();
		l.setPrice(1.0);
		l.setQuantity(1);
		lignes.add(l);
		l = new Ligne();
		l.setPrice(2.0);
		l.setQuantity(2);
		lignes.add(l);
		l = new Ligne();
		l.setPrice(3.0);
		l.setQuantity(3);
		lignes.add(l);
		
		factureService.save(facture, lignes);
		sessionFactory.getCurrentSession().flush(); //artificiel, fonctionnement du test
		assertNotNull(facture.getId());
		for(Ligne li:lignes){
			assertNotNull(li.getId());
		}
		
		List<Facture> factures = jdbcTemplate.query("select * from FACT where ID=?", new FactureRowMapper(), facture.getId());
		assertEquals(factures.size(), 1);
		assertEquals(factures.get(0).getnLignes(),3);
		List<Ligne> lis = jdbcTemplate.query("select * from LIGNE where FACT_ID=?", new LigneRowMapper(),facture.getId());
		assertEquals(lis.size(),3,"Lignes pas correctes");
		for(Ligne li:lis){
			assertEquals(li.getFactureId(),facture.getId());
		}
	}
	
	@Test
	public void testFactureAndLignesMustBeCreatedExceptedForLigneRejected(){
		Facture facture = new Facture();
		facture.setNum("11111");
		List<Ligne> lignes = new ArrayList<Ligne>();
		Ligne l = new Ligne();
		l.setPrice(1.0);
		l.setQuantity(1);
		lignes.add(l);
		l = new Ligne();
		l.setPrice(-2.0);
		l.setQuantity(2);
		lignes.add(l);
		l = new Ligne();
		l.setPrice(3.0);
		l.setQuantity(3);
		lignes.add(l);
		
		factureService.save(facture, lignes);
		sessionFactory.getCurrentSession().flush(); //artificiel, fonctionnement du test
		assertNotNull(facture.getId());
		for(Ligne li:lignes){
			if(li.getPrice()>=0.0)
				assertNotNull(li.getId());
		}
		
		List<Facture> factures = jdbcTemplate.query("select * from FACT where ID=?", new FactureRowMapper(), facture.getId());
		assertEquals(factures.size(), 1);
		assertEquals(factures.get(0).getnLignes(),2);
		List<Ligne> lis = jdbcTemplate.query("select * from LIGNE where FACT_ID=?", new LigneRowMapper(),facture.getId());
		assertEquals(lis.size(),2,"Lignes pas correctes");
		for(Ligne li:lis){
			assertEquals(li.getFactureId(),facture.getId());
		}
	}
	
	@Test
	public void testRollbackOnParentTransactionDoesnotRollbackChildTransaction(){
		Facture facture = new Facture();
		facture.setNum("11111");
		List<Ligne> lignes = new ArrayList<Ligne>();
		Ligne l = new Ligne();
		l.setPrice(100.0);
		l.setQuantity(100);
		lignes.add(l);
		try{
			factureService.save(facture, lignes);
		}catch(Exception e){}
		sessionFactory.getCurrentSession().flush(); //artificiel, fonctionnement du test
		
		List<Facture> factures = jdbcTemplate.query("select * from FACT where ID=?", new FactureRowMapper(), facture.getId());
		assertTrue(factures.isEmpty());
		List<Ligne> lis = jdbcTemplate.query("select * from LIGNE", new LigneRowMapper());
		assertEquals(lis.size(),1,"Malheureusement, une ligne a été insérée");
		assertEquals(lis.get(0).getPrice(),100.0);
		assertNull(lis.get(0).getFactureId());
	}
}
