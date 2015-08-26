package be.fabrice.lazyProperties;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.lazyProperties.entity.Personne;
import be.fabrice.utils.TransactionalTestBase;

@ContextConfiguration(locations="classpath:lazyProperties/test-spring.xml")
public class TestLazyProperties extends TransactionalTestBase{
	@BeforeMethod
	public void beforeMethod(){
		executeSqlScript("lazyProperties/test-script.sql", false);
	}
	
	@Test
	public void testFind(){
		Personne p = (Personne)getSession().get(Personne.class,1000);
		
		assertEquals(p.getName(), "Anybody");
		
		assertEquals(p.getPersonneLazyPorperties().getLazyName(),"Toto");
	}
	
	@Test
	public void testInsert(){
		Personne p = new Personne();
		p.setName("test");
		p.setLazyName("testlazy");
		getSession().saveOrUpdate(p);
		getSession().flush(); //Test cheat
		assertNotNull(p.getId());
		
		SqlRowSet rs = jdbcTemplate.queryForRowSet("select * from pers1 where id = ?", p.getId());
		rs.next();
		assertEquals(rs.getString("NAME"), "test","Must have been inserted");
		assertEquals(rs.getString("LAZYNAME"),"testlazy","Must have been inserted");
	}
	
	@Test
	public void testNameUpdate(){
		Personne p = (Personne)getSession().get(Personne.class,1000);
		p.setName("Arthur");
		getSession().flush();
		
		SqlRowSet rs = jdbcTemplate.queryForRowSet("select * from pers1 where id = ?", 1000);
		rs.next();
		assertEquals(rs.getString("NAME"), "Arthur","Must have been updated");
		assertEquals(rs.getString("LAZYNAME"),"Toto","Must not change");
	}
	
	@Test
	public void testLazyNameUpdate(){
		Personne p = (Personne)getSession().get(Personne.class,1000);
		p.setLazyName("Arthur");
		getSession().flush();
		
		SqlRowSet rs = jdbcTemplate.queryForRowSet("select * from pers1 where id = ?", 1000);
		rs.next();
		assertEquals(rs.getString("LAZYNAME"), "Arthur","Must have been updated");
		assertEquals(rs.getString("NAME"),"Anybody","Must not change");
	}
	
	@Test
	public void testBothNameUpdate(){
		Personne p = (Personne)getSession().get(Personne.class,1000);
		p.setName("Charles");
		p.setLazyName("Arthur");
		getSession().flush();
		
		SqlRowSet rs = jdbcTemplate.queryForRowSet("select * from pers1 where id = ?", 1000);
		rs.next();
		assertEquals(rs.getString("LAZYNAME"), "Arthur","Must have been updated");
		assertEquals(rs.getString("NAME"),"Charles","Must have been updated");
	}
	
	@Test
	public void testDelete(){
		Personne p = (Personne)getSession().get(Personne.class,1000);
		getSession().delete(p);
		getSession().flush(); //Cheat with session
		
		assertEquals(countRowsInTable("PERS1"),0, "Must be empty");
	}
}
