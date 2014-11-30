package be.fabrice.evict.dao;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.evict.entity.Chien;

/**
 * L'objectif ici est de montrer une utilisation erronée d'evict, car pour de mauvaises raisons.
 * La fonctionnalité qui est recherchée est de renommer tous les chiens de la même
 * race qu'un chien dont on a l'id.
 * Voilà ce que ça donne.
 * @author fabrice.claes
 *
 */
@Test(suiteName="Evict should be evicted",testName="Mauvaise utilisation de evict")
@ContextConfiguration(locations="classpath:evict/test-evict-spring.xml")
public class TestEvictMisuse extends AbstractTransactionalTestNGSpringContextTests {
	@Autowired
	private Dao dao;
	@Autowired
	private SessionFactory sessionfactory;
	
	@BeforeMethod
	public void beforeMethod(){
		executeSqlScript("evict/test-script.sql", true);
	}
	
	/**
	 * <p>Première implémentation, erronée.</p>
	 * <p>Grâce à l'id, le chien dont la race servira de référence est récupéré. Comme la recherche
	 * se fera par l'exemple, les propriétés qui ne sont pas utilisées sont mises à null. Ici, il s'agit du nom
	 * (name) du chien.</p>
	 * <p>La requête qui est effectuée ensuite provoque un flush de la session et les objets attachés, dont chien,
	 * sont vérifiés (dirty checking) et s'ils sont dirty, updatés. Chien est dirty puisque son nom a changé. Son nouveau
	 * nom (colonne à null) est persisté.<p>
	 * <p>Tous les chiens de la race basset sont bien récupéré mais le chien de référence n'a plus de nom, d'où le nom erroné
	 * à la fin.</p>
	 * <p>C'est un effet de bord, que l'on va corriger avec un evict (ce qui fonctionnera ici, mais est
	 * une erreur en fait).</p> 
	 */
	@Test
	public void testRenameButWithSideEffects(){
		Integer idChienDeReference = 2001;
		Chien chien = dao.findChien(idChienDeReference);
		assertEquals(chien.getName(),"c1","Bien sûr, c'est en DB");
		chien.setName(null); //Ne fait pas partie du critère de recherche
		
		List<Chien> chiens = dao.findChiens(chien);
		assertEquals(chiens.size(), 3);
		
		for(Chien c:chiens){
			c.setName(c.getName()+"basset");
			dao.save(c);
		}
		sessionfactory.getCurrentSession().flush(); //Needed in that cas but it's not the problem indeed
		
		Chien c = dao.findChien(2002);
		assertEquals(c.getName(),"c2basset");
		c = dao.findChien(idChienDeReference);
		assertNotEquals(c.getName(),"c1basset");
		assertEquals(c.getName(),"nullbasset");
	}
	
	/**
	 * <p>Afin d'éviter les effets de bord, le chien est "evicté" de la session avant d'être modifié. N'étant pas dans la session
	 * il n'y a pas de dirty checking pour lui, donc pas d'update...</p>
	 * <p>Tout semble rentré dans l'ordre. Dans le cas réellement rencontré, cela provoquait d'autres effets de bord
	 * et ce n'était donc finalement pas une solution acceptable.</p>
	 * <p>Mais la solution n'est de toute façon pas correcte...</p>
	 */
	@Test
	public void testRenameWithEvict(){
		Integer idChienDeReference = 2001;
		Chien chien = dao.findChien(idChienDeReference);
		assertEquals(chien.getName(),"c1","Bien sûr, c'est en DB");
		
		sessionfactory.getCurrentSession().evict(chien);
		
		chien.setName(null); //Ne fait pas partie du critère de recherche
		
		List<Chien> chiens = dao.findChiens(chien);
		assertEquals(chiens.size(), 3);
		
		for(Chien c:chiens){
			c.setName(c.getName()+"basset");
			dao.save(c);
		}
		sessionfactory.getCurrentSession().flush(); //Needed in that cas but it's not the problem indeed
		
		Chien c = dao.findChien(2002);
		assertEquals(c.getName(),"c2basset");
		c = dao.findChien(idChienDeReference);
		assertEquals(c.getName(),"c1basset");
	}
	
	/**
	 * <p>Le evict n'était pas correct car HIbernate a pour objectif d'établir une correspondance entre le modèle
	 * objet et la base de données. Le premier fonctionnement était correct: si une entité est modifiée, elle est
	 * persistée.</p>
	 * <p>La deuxième solution est un work-around mais ne résoud pas le problème fondamental: si on change une entité,
	 * la modification sera persistée.</p>
	 * <p>Le vrai problème est que l'id de référence permet de récupérer une entité dont seule la propriété
	 * 'race' nous intéresse. Grâce à cette propriété, nous pouvons créer un exemple, exemple qui n'est PAS
	 * l'entité.</p>
	 * <p>Voici la solution correcte, sans evict, sans effet de bord.</p>
	 */
	@Test
	public void testRenameWithoutEvictAndWithoutSideEffects(){
		Integer idChienDeReference = 2001;
		Chien chien = dao.findChien(idChienDeReference);
		assertEquals(chien.getName(),"c1","Bien sûr, c'est en DB");

		Chien exempleChienPourRecherche = new Chien(); //!!!!!!!
		exempleChienPourRecherche.setRace(chien.getRace()); //La seule propriété qui nous intéresse
		
		List<Chien> chiens = dao.findChiens(exempleChienPourRecherche);
		assertEquals(chiens.size(), 3);
		
		for(Chien c:chiens){
			c.setName(c.getName()+"basset");
			dao.save(c);
		}
		sessionfactory.getCurrentSession().flush(); //Needed in that cas but it's not the problem indeed
		
		Chien c = dao.findChien(2002);
		assertEquals(c.getName(),"c2basset");
		c = dao.findChien(idChienDeReference);
		assertEquals(c.getName(),"c1basset");
	}
}
