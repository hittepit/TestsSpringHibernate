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

import be.fabrice.model.entity.RectangleV3;

@Test(suiteName="Modèles robustes",testName="Persistence d'entités robustes", description="Test sur l'utilisation de modèles robustes avec Hibernate")
@ContextConfiguration(locations = "classpath:model/test-model-spring.xml")
public class TestRectangleV3Persistence extends AbstractTransactionalTestNGSpringContextTests {
	class RectangleVo {
		Long id;
		double longueur;
		double largeur;
	}

	class RectangleVoMapper implements RowMapper<RectangleVo> {
		public RectangleVo mapRow(ResultSet rs, int rowNum) throws SQLException {
			RectangleVo r = new RectangleVo();
			r.id = rs.getLong("ID");
			r.longueur = rs.getDouble("LONGUEUR");
			r.largeur = rs.getDouble("LARGEUR");
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
	public void testFindPossibleWithCoherentValues() {
		Session session = sessionFactory.getCurrentSession();
		RectangleV3 r = (RectangleV3) session.get(RectangleV3.class,1000L);
		assertEquals(r.getLongueur(), 10.0);
		assertEquals(r.getLargeur(), 2.5);
		assertEquals(r.getSurface(),25.0);
	}

	@Test
	public void testFindImpossibleWithNonCoherentValues() {
		Session session = sessionFactory.getCurrentSession();
		try {
			session.get(RectangleV3.class,1001L);
			fail();
		} catch (PropertyAccessException e) {
			// ok
		}
	}

	@Test
	public void testTransientRectangleInsertionIsPossible() {
		Session session = sessionFactory.getCurrentSession();
		RectangleV3 rectangle = new RectangleV3(5.5, 3.0);

		session.save(rectangle);

		assertNotNull(rectangle.getId(), "Must have been generated");

		List<RectangleVo> rects = jdbcTemplate.query("select * from RECT3 where ID=?", new RectangleVoMapper(),
				rectangle.getId());
		assertEquals(rects.size(), 1, "Il ne doit y en avoir qu'un");
		RectangleVo rvo = rects.get(0);
		assertEquals(rvo.longueur, 5.5);
		assertEquals(rvo.largeur, 3.0);
	}

}
