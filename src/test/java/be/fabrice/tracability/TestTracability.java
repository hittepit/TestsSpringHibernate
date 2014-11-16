package be.fabrice.tracability;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.tracability.entity.Personne;
import be.fabrice.utils.TransactionalTestBase;

@ContextConfiguration(locations="classpath:tracability/test-spring.xml")
public class TestTracability extends TransactionalTestBase{
	class PersonneDTO{
		private String name;
		private Integer modifyingUser;
		private Timestamp modifyingTmp;
	}
	
	class PersonneDTORowMapper implements RowMapper<PersonneDTO>{

		@Override
		public PersonneDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
			PersonneDTO p = new PersonneDTO();
			p.name = rs.getString("NAME");
			p.modifyingUser = rs.getInt("UPDATE_USER_ID");
			p.modifyingTmp = rs.getTimestamp("UPDATE_TIME");
			return p;
		}
		
	}
	
	@BeforeMethod
	public void beforeMethod(){
		executeSqlScript("tracability/test-script.sql",false);
	}

	@Test
	public void testSimpleFind(){
		Personne p = (Personne) getSession().get(Personne.class, 1000);
		
		assertNotNull(p);
		
		assertEquals(p.getName(), "Anybody");
		assertEquals(p.getTrace().getUpdateUserId(), Integer.valueOf(1));
	}
	
	@Test
	public void testUpdate(){
		Personne p = (Personne) getSession().get(Personne.class, 1000);
		p.setName("toto");
		
		getSession().flush(); //Test trick, needed to persist change state
		
		getSession().flush(); //Test trick, needed to persist change in trace from listener
		
		List<PersonneDTO> pdtos = jdbcTemplate.query("select * from PERS where ID=?", new PersonneDTORowMapper(), p.getId());
		assertEquals(pdtos.size(), 1);
		PersonneDTO pdto = pdtos.get(0);
		assertEquals(pdto.name, "toto");
		assertEquals(pdto.modifyingUser,Integer.valueOf(1000));
		assertNotNull(pdto.modifyingTmp);
	}
	
	@Test
	public void testInsert(){
		Personne p =new Personne();
		p.setName("toto");
		
		getSession().saveOrUpdate(p);
		
		getSession().flush(); //Test trick, needed to persist change in trace from listener
		
		List<PersonneDTO> pdtos = jdbcTemplate.query("select * from PERS where ID=?", new PersonneDTORowMapper(), p.getId());
		assertEquals(pdtos.size(), 1);
		PersonneDTO pdto = pdtos.get(0);
		assertEquals(pdto.name, "toto");
		assertEquals(pdto.modifyingUser,Integer.valueOf(1000));
		assertNotNull(pdto.modifyingTmp);
	}
	
	@Test
	public void testDelete(){
		Personne p = (Personne) getSession().get(Personne.class, 1000);
		getSession().delete(p);
		getSession().flush();
		assertEquals(countRowsInTable("PERS"),0);
	}
}
