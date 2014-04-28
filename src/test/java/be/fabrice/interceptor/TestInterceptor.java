package be.fabrice.interceptor;

import static org.testng.Assert.assertEquals;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import be.fabrice.utils.TransactionalTestBase;

@ContextConfiguration(locations="classpath:interceptor/test-spring.xml")
public class TestInterceptor extends TransactionalTestBase{
	@Autowired
	private Dao dao;
	
	@BeforeClass
	public void beforeMethod(){
		executeSqlScript("interceptor/test-script.sql", false);
	}
	
	@Test
	public void testInsertIsIntercepted(){
		Person p = new Person();
		p.setFirstname("a");
		p.setLastname("b");
		dao.save(p);
		
		assertEquals(countRowsInTable("PERSON"),1,"No insertion");
	}
	
	@Test
	public void testUpdateIsIntercepted(){
		Person p = dao.find(1000);
		p.setFirstname("H");
		dao.save(p);
		
		List<Object[]> r = jdbcTemplate.query("select * from PERSON where ID=?",new RowMapper<Object[]>(){
			public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new Object[]{rs.getInt("ID"),rs.getString("F"),rs.getString("L")};
			}}
		,1000);
		
		assertEquals(r.size(), 1);
		assertEquals(r.get(0)[1],"Toto","No change");
	}
	
	@Test
	public void testDeleteIsIntercepted(){
		Person p = dao.find(1000);
		dao.delete(p);
		
		List<Object[]> r = jdbcTemplate.query("select * from PERSON where ID=?",new RowMapper<Object[]>(){
			public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new Object[]{rs.getInt("ID"),rs.getString("F"),rs.getString("L")};
			}}
		,1000);
		
		assertEquals(r.size(), 1, "Not deleted");
	}
	
	@AfterClass
	public void clean(){
		deleteFromTables("PERSON");
	}
}
