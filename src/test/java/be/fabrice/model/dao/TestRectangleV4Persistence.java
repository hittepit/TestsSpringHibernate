package be.fabrice.model.dao;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.hibernate.PropertyAccessException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.model.entity.RectangleV4;

@ContextConfiguration(locations = "classpath:model/test-model-spring.xml")
public class TestRectangleV4Persistence extends AbstractTransactionalTestNGSpringContextTests {
	class RectangleVo {
		Long id;
		double dimension1;
		double dimension2;
	}

	class RectangleVoMapper implements RowMapper<RectangleVo> {
		public RectangleVo mapRow(ResultSet rs, int rowNum) throws SQLException {
			RectangleVo r = new RectangleVo();
			r.id = rs.getLong("ID");
			r.dimension1 = rs.getDouble("DIMENSION1");
			r.dimension2 = rs.getDouble("DIMENSION2");
			return r;
		}
	}
	
	@Autowired
	private SessionFactory sessionFactory;

	@BeforeMethod
	public void beforeMethod() {
		executeSqlScript("model/test-script.sql", false);
	}

	@Test
	public void testFindPossibleWithCoherentValues1() {
		Session session = sessionFactory.getCurrentSession();
		RectangleV4 r = (RectangleV4) session.get(RectangleV4.class,1000L);
		assertEquals(r.getLongueur(), 10.0);
		assertEquals(r.getLargeur(), 2.5);
		assertEquals(r.getSurface(),25.0);
	}

	@Test
	public void testFindPossibleWithCoherentValues2() {
		Session session = sessionFactory.getCurrentSession();
		RectangleV4 r = (RectangleV4) session.get(RectangleV4.class,1001L);
		assertEquals(r.getLongueur(), 10.0);
		assertEquals(r.getLargeur(), 2.5);
		assertEquals(r.getSurface(),25.0);
	}

	@Test
	public void testFindPossibleWithNonCoherentValues() {
		Session session = sessionFactory.getCurrentSession();
		RectangleV4 r = (RectangleV4) session.get(RectangleV4.class,1002L);
		assertEquals(r.getSurface(), -25.0);
	}

	@Test
	public void testTransientRectangleInsertionIsPossible() {
		Session session = sessionFactory.getCurrentSession();
		RectangleV4 rectangle = new RectangleV4(5.5, 3.0);

		session.save(rectangle);

		assertNotNull(rectangle.getId(), "Must have been generated");

		List<RectangleVo> rects = jdbcTemplate.query("select * from RECT4 where ID=?", new RectangleVoMapper(),
				rectangle.getId());
		assertEquals(rects.size(), 1, "Il ne doit y en avoir qu'un");
		RectangleVo rvo = rects.get(0);
		assertEquals(rvo.dimension1, 5.5);
		assertEquals(rvo.dimension2, 3.0);
	}

}
