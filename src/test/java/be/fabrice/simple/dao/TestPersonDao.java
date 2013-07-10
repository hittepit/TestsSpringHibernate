package be.fabrice.simple.dao;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.sql.ResultSet;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.simple.entity.Person;

/**
 * <p>Démonstration de tests sur un Dao Spring-Hibernate</p>
 * <p>L'objectif (d'un test unitaire) est vérifier que les méthodes de la classe testée sont correctement
 * écrites. Cela peut se faire de deux manières:
 * <ol>
 * <li>mocker la session Hibernate et à vérifier
 * que ses méthodes sont bien appelées avec les bons paramètres. Néanmoins, cette manière de procéder 
 * est très lourde et elle ne permet pas de vérifier par exemple que la requête de la méthode 
 * findByLastName est correctement écrite. Juste qu'elle est bien passée en paramètre d'une méthode de la session.</li>
 * <li>utiliser une DB de test avec les données tout juste nécessaires à l'exécution du test. Cette manière est
 * très efficace, un peu plus lente que la précédente, mais elle fait surtout intervenir un élément
 * extérieur (la DB).</li>
 * </ol></p>
 * <p>Compte tenu de son efficacité, c'est cette seconde méthode qui sera préférée et démontrée ici. La db utilisée
 * est une db mémoire (H2).</p>
 * @author fabrice.claes
 *
 */
@ContextConfiguration(locations="classpath:simple/test-simple-spring.xml")
public class TestPersonDao extends AbstractTransactionalTestNGSpringContextTests {
	
	/**
	 * <p>Classe basique pour contenir les propriétés de la table PERSON. Le rôle de cette classe est différent
	 * de l'entité correspondante. L'expérience montre qu'utiliser une entité dans un autre contexte qu'Hibernate
	 * mène souvent à des effets indésirables.</p>
	 * <p>Ceci dit, dans la cas très simple de ce test, l'entité aurait pu être utilisée sans risque. A des fins
	 * de démonstration donc.</p>
	 */
	private class PersonSimple{
		private Integer id;
		private String firstname;
		private String lastname;

		public PersonSimple(Integer i, String f, String l){
			this.id = i;
			this.firstname = f;
			this.lastname = l;
		}

		public Integer getId() {
			return id;
		}

		public String getFirstname() {
			return firstname;
		}

		public String getLastname() {
			return lastname;
		}
	}
	
	private class PersonRowMapper implements RowMapper<PersonSimple>{
		public PersonSimple mapRow(ResultSet rs, int index) throws SQLException {
			return new PersonSimple(rs.getInt("ID"),rs.getString("FIRSTNAME"),rs.getString("LASTNAME"));
		}
	}
	
	@Autowired
	private PersonDao personDao;
	
	@Autowired
	private SessionFactory sessionFactory;
	
	/**
	 * <p>Initialisation de chaque méthode de test de la classe. Comme on est "transactional", un rollback 
	 * est automatique effectué après chaque méthode de test.</p>
	 * <p>A noter que c'est une méthode de {@link AbstractTransactionalTestNGSpringContextTests} qui est utilisée
	 * et certainement pas la méthode save du Dao. L'utilisation de save serait contraire aux principes de base
	 * des tests unitaires dans la mesure où une méthode de la classe qu'on est en train de tester serait utilisée
	 * pour en tester une autre.</p>
	 */
	@BeforeMethod
	public void beforeMethod(){
		executeSqlScript("simple/test-script.sql", false);
	}
	
	/**
	 * <p>C'est un test tout simple. Néanmoins, il ne devrait jamais être prioritaire car la méthode
	 * est très directe. La valeur ajoutée du test est donc très faible et pourrait se résumer par "Hibernate
	 * est capable de renvoyer une entité à partir de son ID. Le but ici est de tester PersonHibernateDao, pas
	 * Hibernate.</p>
	 */
	@Test
	public void testSimpleFind(){
		Person p = personDao.find(1000);
		assertEquals(p.getFirstname(), "F1");
		assertEquals(p.getLastname(),"L1");
	}
	
	/**
	 * <p>Comme pour le testSimpleFind, ce test-ci n'est pas proritaire et sa valeur ajoutée est proche de zéro.
	 * Il montre néanmoins comment vérifier qu'une insertion via le dao est bien répercutée dans la DB (en 
	 * utilisant autre chose qu'une session Hibernate. On
	 * comparera ce test avec testSimpleFakeUpdate qui est incorrect.</p>
	 */
	@Test
	public void testSimpleInsert(){
		Person p = new Person();
		p.setFirstname("new");
		p.setLastname("one");
		personDao.save(p);
		assertNotNull(p.getId(),"L'entité n'a pas été persistée, elle n'a pas d'id.");
		
		List<PersonSimple> persons = jdbcTemplate.query("select * from PERSON where ID=?",new PersonRowMapper(),p.getId());
		assertEquals(persons.size(),1);
		PersonSimple person = persons.get(0);
		assertEquals(person.getId(),p.getId());
		assertEquals(person.getFirstname(),"new");
		assertEquals(person.getLastname(),"one");
	}
	
	/**
	 * <p>L'objectif de ce test semble d'être le test de la méthode save lorsqu'une entité existante est modifiée.</p>
	 * <p>Pourtant ce test a un défaut énorme: il utilise la méthode find pour retrouerv 'lentité qui vient d'être
	 * modifiée. Or cette entité est dans la session Hibernate et le find n'ira pas accéder à la DB car il est déjà.
	 * Il renvoie donc la même entité (la même instance) et en garantit pas que la modification a été persistée en DB.</p>
	 * <p>La deuxième partie montre d'ailleurs que les données n'y ont pas été persistées (pas de flush).</p>
	 */
	@Test
	public void testSimpleFakeUpdate(){
		Person p1 = personDao.find(1000);
		p1.setFirstname("toto");
		p1.setLastname("tata");
		
		Person p2 = personDao.find(1000);
		assertSame(p2, p1, "C'est la même instance qui est renvoyée par la session.");
		assertEquals(p2.getFirstname(),"toto","Le contraire eut été étonnant...");
		
		//Mais pire...
		List<PersonSimple> persons = jdbcTemplate.query("select * from PERSON where ID=?",new PersonRowMapper(),1000);
		assertEquals(persons.size(),1);
		PersonSimple p3 = persons.get(0);
		assertEquals(p3.getFirstname(),"F1","La modification n'a pas été persistée dans la DB");
		
		//Pour persister un update, il faut un flush (qui serait normalement fait à la fin de la transaction
		//mais comme le test est lui-même transactionnel...)
		sessionFactory.getCurrentSession().flush();
		persons = jdbcTemplate.query("select * from PERSON where ID=?",new PersonRowMapper(),1000);
		assertEquals(persons.size(),1);
		p3 = persons.get(0);
		assertEquals(p3.getFirstname(),"toto","Maintenant la modification a pas été persistée dans la DB");
	}
	
	/**
	 * Sur le Dao, c'est la seule méthode qui mérite d'être testée. Deux tests vont s'en occuper. Le premier
	 * sur un lastname existant, le deuxième sur un lastname non existant. Ces deux conditions d'utilisation
	 * différentes justifient que les tests soient séparés. De plus, s'ils étaient rassemblés en un seul test, 
	 * l'échec de la première condition ne permettrait pas de tester la seconde. En les séparant, bien.
	 */
	@Test
	public void testFindByExistingLastname(){
		List<Person> persons = personDao.findByLastname("L1");
		
		assertEquals(persons.size(), 3);
		//Pour aller plus loin, vérifier que les 3 entités sont les bonnes...
	}
	
	/**
	 * Voir commentaires de testFindByExistingLastname
	 */
	@Test
	public void testFindByNonExistingLastname(){
		List<Person> persons = personDao.findByLastname("whatever");
		
		assertTrue(persons.isEmpty());
	}
	
	/**
	 * <p>Voici un test qui ne doit pas être fait.</p>
	 * <p>L'entité Person définit sur la propriété firstname une contrainte d'unicité. Le test vérifie donc 
	 * qu'il n'est pas possible d'insérer une personne avec un prénom déjà existant.</p>
	 * <p>Néanmoins, le test ne fonctionne que parce que Hibernate a généré la DB de test. En production, c'est rarement
	 * le cas. Il ne garantit donc pas que la contrainte existera dans tous les cas.</p>
	 */
	@Test
	public void testConstraintViolationShouldFail(){
		Person p = new Person();
		p.setFirstname("F1");
		p.setLastname("one");
		try{
			personDao.save(p);
			fail();
		} catch(ConstraintViolationException e){
		}
	}
}
