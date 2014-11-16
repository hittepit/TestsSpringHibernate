package be.fabrice.inheritance.table.dao;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.hibernate.proxy.HibernateProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.inheritance.table.entity.Boss;
import be.fabrice.inheritance.table.entity.Employeur;
import be.fabrice.inheritance.table.entity.Societe;
import be.fabrice.inheritance.table.entity.Travailleur;

@ContextConfiguration("classpath:inheritance/table/test-inheritance-spring.xml")
public class TestInheritanceTablePerClass extends AbstractTransactionalTestNGSpringContextTests{
	@Autowired
	private Dao dao;
	
	@BeforeMethod
	public void beforeMethod(){
		executeSqlScript("inheritance/table/test-script.sql", false);
	}
	
	/**
	 * Ce test montre que l'employeur référencé par le travailleur, même si c'est une référence du type {@link Employeur}
	 * sera du bon type au final (ici Boss)... A CONDITION que ce soit EAGER. Voir testEmployeurTypeIsNotCorrectWhenLazyLoaded.
	 */
	@Test
	public void testEmployeurBossIsRightTypeWhenEager(){
		Travailleur t = dao.findTravailleur(1002);
		assertTrue(t.getEmployeur() instanceof Boss);
	}
	
	/**
	 * Ce test montre que l'employeur référencé par le travailleur, même si c'est une référence du type {@link Employeur}
	 * sera du bon type au final (ici Societe)... A CONDITION que ce soit EAGER. Voir testEmployeurTypeIsNotCorrectWhenLazyLoaded.
	 */
	@Test
	public void testEmployeurSocieteIsRightTypeWhenEager(){
		Travailleur t = dao.findTravailleur(1001);
		assertTrue(t.getEmployeur() instanceof Societe);
	}
	
	/**
	 * <p>Si la référence est de type {@link Employeur} et que la référence est lazy loadée, le proxy généré
	 * étendra le type {@link Employeur} (il ne saura d'ailleurs pas dire de quel type réel il est puisque l'entité n'est pas encore chargée).</p>
	 * <p>Ceci reste vrai même si le proxy est initialisé</p>
	 * <p>Par contre, l'entité cachée derrière le proxy, une fois initialisée sera du bon type.<p>
	 */
	@Test
	public void testEmployeurTypeIsNotCorrectWhenLazyLoaded(){
		Travailleur t = dao.findTravailleur(1001);
		assertFalse(t.getLazyEmployeur() instanceof Societe);
		
		Employeur e = (Employeur)((HibernateProxy)t.getLazyEmployeur()).getHibernateLazyInitializer().getImplementation();
		assertTrue(e instanceof Societe);
	}

	/**
	 * Un find sur la classe {@link Boss} renvoie bien des bosses.
	 */
	@Test
	public void testEmployeursBossAreCorrectType(){
		List<Boss> employeurs = dao.findAllBosses();
		assertEquals(employeurs.size(),2);
		for(Boss b :employeurs){
			assertTrue(b instanceof Boss,"Si jamais il y a une erreur ici, faut m'envoyer l'erreur, ça serait grandiose");
		}
	}
	
	/**
	 * Un find sur la classe {@link Societe} renvoie bien des sociétés.
	 */
	@Test
	public void testEmployeursSocieteAreCorrectType(){
		List<Societe> employeurs = dao.findAllSocietes();
		assertEquals(employeurs.size(),2);
		for(Societe s :employeurs){
			assertTrue(s instanceof Societe,"Si jamais il y a une erreur ici, faut m'envoyer l'erreur, ça serait grandiose");
		}
	}
	
	/**
	 * Un find sur tous les {@link Employeur} renvoie une liste d'employeurs où chaque objet est du bon type.
	 */
	@Test
	public void testAllEmployeursAreCorrectType(){
		List<Employeur> employeurs = dao.findAllEmployeurs();
		assertEquals(employeurs.size(),4);
		for(Employeur e:employeurs){
			if(e.getId()==1000 || e.getId()==1001){
				assertTrue(e instanceof Societe);
			} else {
				assertTrue(e instanceof Boss);
			}
		}
	}
}
