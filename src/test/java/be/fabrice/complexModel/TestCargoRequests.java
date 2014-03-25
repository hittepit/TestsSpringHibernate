package be.fabrice.complexModel;

import static org.testng.Assert.assertEquals;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.utils.TransactionalTestBase;

@ContextConfiguration(locations="classpath:complexModel/test-spring.xml")
public class TestCargoRequests extends TransactionalTestBase{
	@BeforeMethod
	public void beforeMethod(){
		executeSqlScript("complexModel/test-script.sql", false);
	}
	
	@Test
	public void testFindCargoWithArticleAndRouteUsingSql(){
		String from = "Anvers";
		String article = "Livres";
		String sql = "select c.id as CID from ARTICLE a "
				+ "left outer join CONTAINER co on co.ID = a.CONTAINER_ID "
				+ "left outer join Cargo c on co.CARGO_ID = c.ID "
				+ "left outer join ROUTE r on r.CARGO_ID = c.ID "
				+ "where r.DE = ? and a.NOM = ?";

		List<Integer> ids = jdbcTemplate.query(sql,new RowMapper<Integer>(){
			public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getInt("CID");
			}
		},new Object[]{from,article});
		
		assertEquals(ids.size(), 1);
		assertEquals(ids.get(0), Integer.valueOf(10));
	}
	
	@Test
	public void testFindCargoWithArticleAndRouteUsingOneToMany(){
		String from = "Anvers";
		String article = "Livres";
		
		String hql = "select a.container.cargo from Article a "
				+ "left join a.container.cargo.routes as r "
				+ "where a.nom = :nomArticle and r.de = :from";
		List<Cargo> cargos = getSession().createQuery(hql).setParameter("nomArticle", article).setParameter("from", from).list();
		assertEquals(cargos.size(), 1);
		assertEquals(cargos.get(0).getNom(), "Alpha");
	}
	
	@Test
	public void testFindCargoWithArticleAndRouteWithoutOneToMany(){
		String from = "Anvers";
		String article = "Livres";
		
		String hql = "select a.container.cargo from Article a, Route r "
				+ "where a.container.cargo = r.cargo and a.nom = :nomArticle and r.de = :from";
		List<Cargo> cargos = getSession().createQuery(hql).setParameter("nomArticle", article).setParameter("from", from).list();
		assertEquals(cargos.size(), 1);
		assertEquals(cargos.get(0).getNom(), "Alpha");
	}
}
