package be.fabrice.flush.dao;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.flush.entity.Dummy;
import be.fabrice.flush.entity.Person;

/**
 * <p>Le flush des entités de la session permet de persister les modifications faites sur les ntités vers la base de données.</p>
 * <p>Ce test a pour objectif de démontrer le fonctionnement du flush de la session.
 * Quand est-il fait? A quoi sert-il?</p>
 * <p>Le flush est normalement fait lorsque la transaction est commitée, ce qui n'est pas possible dans ce
 * type de test puisque {@link AbstractTransactionalTestNGSpringContextTests} impose un rollback sur la transaction.
 * Par conséquent, il faudra provoquer le flush manuellement.</p>
 * @author fabrice.claes
 *
 */
@ContextConfiguration("classpath:flush/test-flush-spring.xml")
public class TestFlush extends AbstractTransactionalTestNGSpringContextTests{
	private class PersonRowMapper implements RowMapper<Person>{
		public Person mapRow(ResultSet rs, int index) throws SQLException {
			Person p = new Person();
			p.setId(rs.getInt("ID"));
			p.setName(rs.getString("NOM"));
			return p;
		}
	}

	@Autowired
	private MockSessionFlushListener mockSessionFlushListener;
	@Autowired
	private MockFlushEntityListener mockFlushEntityListener;
	@Autowired
	private SessionFactory sessionFactory;
	@Autowired
	private Dao dao;
	
	@BeforeMethod
	public void beforeMethod(){
		mockSessionFlushListener.resetInvocation();
		mockFlushEntityListener.resetInvocation();
		executeSqlScript("flush/test-script.sql", false);
	}
	
	/**
	 * L'insert est particulier car il ne provoque pas de flush de la session. En fait, le session.save
	 * (ce qui sera fait derrière le saveOrUpdate du Dao) insère automatiquement l'entité, sans provoquer
	 * de flush.
	 */
	@Test
	public void testInsertDoesNotFlushTheSessionEventIfEntityIsInserted(){
		Person p = new Person();
		p.setName("test");
		dao.save(p);
		assertEquals(mockSessionFlushListener.getInvocation(),0,"No flush was fired");
		assertEquals(mockFlushEntityListener.getInvocation(),0,"No Specific Flush either");
		assertNotNull(p.getId(),"But entity has been inserted");
		List<Person> persons = jdbcTemplate.query("select * from PERSON where ID=?",new PersonRowMapper(),p.getId());
		assertEquals(persons.size(),1,"Entity has been inserted");
	}
	
	/**
	 * Même si une entité est mise à jour, elle ne sera pas persistée tant qu'il n'y a pas de flush. Une erreur est
	 * de croire que appeler session.saveOrUpdate persistera les modifications. C'est faux. Le seul effet de cette
	 * méhode, dans une pile spring-hibernate, avec des transactions gérées par Spirng comme ici, sera de forcer le 
	 * FlushMode de la session afin que le flush soit fait à la fin de la transaction lorsque le commit sera fait.
	 */
	@Test
	public void testUpdateDoesNotCallFlushAndEntityIsNotUpdated(){
		Person p = dao.find(1000);
		p.setName("toto");
		dao.save(p); //Ne sert à rien...
		
		assertEquals(mockSessionFlushListener.getInvocation(),0,"No flush was fired");
		List<Person> persons = jdbcTemplate.query("select * from PERSON where ID=?",new PersonRowMapper(),1000);
		assertEquals(persons.size(),1);
		assertEquals(persons.get(0).getName(),"Anybody","Entity has not been updated");
	}
	
	/**
	 * Dans ce cas, on force manuellement le flush (normalement c'est automatique). Dans ce cas, l'entité dirty est flushée
	 * et les modifications persistées.
	 */
	@Test
	public void testUpdateIsMadeIfFlushIsFired(){
		Person p = dao.find(1000);
		p.setName("toto");
		sessionFactory.getCurrentSession().flush();
		
		assertEquals(mockSessionFlushListener.getInvocation(),1,"Flush was fired");
		List<Person> persons = jdbcTemplate.query("select * from PERSON where ID=?",new PersonRowMapper(),1000);
		assertEquals(persons.size(),1);
		assertEquals(persons.get(0).getName(),"toto","Entity has been updated");
	}

	/**
	 * <p>Ce qui surprend, c'est que le flush est parfois fait tout seul.</p>
	 * <p>Hibernate doit garantir que ce que la session retrouvera sera cohérent. Dans ce test, nous
	 * modifions de l'entité pour lui donner le nom "toto", puis nous recherchons les personnes s'appelant "toto".
	 * Si le flush n'était pas fait, cette dernière requête ne retrouverait aucune entité, ce qui serait incohérent puisque
	 * la session (notre contexte de persistance) en contient une. Dans le doute, dés qu'une requête est faite
	 * Hibernate fait un flush pour que l'état actuel des entités en session soit synchronisé avec la DB. </p>
	 * 
	 */
	@Test
	public void testRequestOnNonIdColumnFiresAFlush(){
		Person p = dao.find(1000);
		p.setName("toto");
		Dummy d = dao.findDummy(1000);
		d.setName("ggggg");

		List<Person> ps = dao.findByName("toto");
		assertEquals(mockSessionFlushListener.getInvocation(),0,"No global flush");
		assertEquals(mockFlushEntityListener.getInvocation(),2,"Flush made because could be necessary");	
		assertEquals(mockFlushEntityListener.getEntityClassFlushed().size(),2,"Toutes les entités dirty sont flushées");
		assertEquals(ps.size(),1,"Evidemment");
	}

	/**
	 * <p>Un session.get, contrairement à une query, ne provoque pas de flush. Hibernate se contente de renvoyer
	 * l'entité dans la session, sans avoir besoin d'accéder à la DB.</p>
	 */
	@Test
	public void testSessionGetDoesNotFireAFlush(){
		Person p = dao.find(1000);
		p.setName("toto");

		Person p2 = dao.find(1001);
		assertEquals(mockSessionFlushListener.getInvocation(),0,"No Flush");
	}

	/**
	 * Intelligence limitée, puisque qu'un query sur l'id, qui pourrait ne pas nécessiter un accès à la DB
	 * (juste à la session), provoque en fait un flush.
	 */
	@Test
	public void testFindByIdFiresASpecificFlush(){
		Person p = dao.find(1000);
		p.setName("toto");

		Person p2 = dao.findById(1001);
		assertEquals(mockSessionFlushListener.getInvocation(),0,"No global flush");
		assertEquals(mockFlushEntityListener.getInvocation(),1,"Specific Flush beacuse could be necessary");
		assertEquals(mockFlushEntityListener.getEntityClassFlushed().size(),1);
	}
	
}
