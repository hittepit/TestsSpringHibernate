package be.fabrice.model.dao;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.model.entity.Rectangle;

class RectangleVo{
	Long id;
	double longueur;
	double largeur;
}


class RectangleVoMapper implements RowMapper<RectangleVo>{

	@Override
	public RectangleVo mapRow(ResultSet rs, int rowNum) throws SQLException {
		RectangleVo r = new RectangleVo();
		r.id = rs.getLong("ID");
		r.longueur = rs.getDouble("LONGUEUR");
		r.largeur = rs.getDouble("LARGEUR");
		return r;
	}
	
}

@ContextConfiguration(locations="classpath:model/test-model-spring.xml")
public class TestRobustModelWithHibernate extends AbstractTransactionalTestNGSpringContextTests {
	@Autowired
	private Dao dao;
	
	@BeforeMethod
	public void beforeMethod(){
		executeSqlScript("model/test-script.sql", false);
	}
	
	@Test
	public void testHibernateCanFindAnFillEntitiesWitoutPublicParameterlessConstructorAndWithoutSetters(){
		Rectangle rectangle = dao.findRectangle(1000L);
		
		assertNotNull(rectangle,"Has been instanciated");
		
		assertEquals(rectangle.getId(),Long.valueOf(1000L));
		assertEquals(rectangle.getLongueur(),10.0);
		assertEquals(rectangle.getLargeur(),2.5);
	}
	
	@Test
	public void testTransientRectangleInsertionIsPossible(){
		Rectangle rectangle = new Rectangle(5.5, 3.0);

		dao.save(rectangle);
		
		assertNotNull(rectangle.getId(), "Must have been generated");
		
		List<RectangleVo> rects =  jdbcTemplate.query("select * from RECT where ID=?",new RectangleVoMapper(),rectangle.getId());
		assertEquals(rects.size(),1,"Il ne doit y en avoir qu'un");
		RectangleVo rvo = rects.get(0);
		assertEquals(rvo.longueur,5.5);
		assertEquals(rvo.largeur,3.0);
	}
}
