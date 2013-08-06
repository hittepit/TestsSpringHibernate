package be.fabrice.proxy.dao;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.proxy.entity.Employeur;
import be.fabrice.proxy.entity.EmployeurCorrect;
import be.fabrice.proxy.entity.EmployeurPresqueCorrect;
import be.fabrice.proxy.entity.Travailleur;

/**
 * Le but de ce test est de montrer certains pièges dans l'écriture de la méthode equals
 * lors de l'utilisation de proxies (lazy loading de relations ManyToOne).<br />
 * Pour les besoins ce test, plusieurs classes sont définies (sans signification business malgré les
 * apparences):
 * <ul>
 * <li>{@link Travailleur} qui est la classe de base des tests et qui possède trois références lazy
 * vers les trois classes suivantes;</li>
 * <li> {@link Employeur} pour lequel la méthode equals est définie avec une comparaison de classe (getClass) et des attributs (ceci dit
 * le equals ne va pas jusqu'à vérifier les propriétés puisqu'il s'arrête sur le getClass);</li>
 * <li> {@link EmployeurPresqueCorrect} où le getClass dans le equals a été modifié avec un instanceof, mais qui compare les
 * attributs par accès direct;</li>
 * <li> {@link EmployeurCorrect} où la méthode equals est correcte dans ce cadre-ci (comparaison des classes avec instanceof et accès aux propriétés
 * via les getters.</li>
 * </ul>
 * Dans tous les cas, des employeurs sont considérés comme égaux s'ils sont de la même classe et si leur nom est le même.
 * @author fabrice.claes
 *
 */
@ContextConfiguration(locations="classpath:proxy/test-proxy-spring.xml")
public class TestProxy extends AbstractTransactionalTestNGSpringContextTests {
	@Autowired
	private TravailleurDao travailleurDao;
	
	@BeforeMethod
	public void beforeTest(){
		executeSqlScript("proxy/test-script.sql", false);
	}
	
	/**
	 * Ce test montre que le equals ne fonctionne pas, alors que le nom est le même. En fait, t.getEmployeur renvoie un
	 * proxy et le getClass sur le proxy n'est pas le même que le getClass sur la classe {@link Employeur}.
	 */
	@Test
	public void testEqualsWithClassEqualityDoesNotWorkWithProxies(){
		Employeur employeurNotManaged = new Employeur();
		employeurNotManaged.setId(1000);
		employeurNotManaged.setName("Anybody");
		
		Travailleur t = travailleurDao.find(1001);
		
		assertNotEquals(t.getEmployeur(), employeurNotManaged);
		assertEquals(t.getEmployeur().getName(), employeurNotManaged.getName());
		assertEquals(t.getEmployeur().getId(), employeurNotManaged.getId());
	}
	
	/**
	 * Test équivalent à testEqualsWithClassEqualityDoesNotWorkWithProxies sauf que le proxy est initialisé
	 * avant de vérifier l'égalité. L'initialisation n'a donc aucun impact positif sur ce type de equals.
	 */
	@Test
	public void testEqualsWithClassEqualityDoesNotWorkWithProxiesEvenIfInitialized(){
		Employeur employeurNotManaged = new Employeur();
		employeurNotManaged.setId(1000);
		employeurNotManaged.setName("Anybody");
		
		Travailleur t = travailleurDao.find(1001);
		
		assertEquals(t.getEmployeur().getName(), employeurNotManaged.getName());
		assertEquals(t.getEmployeur().getId(), employeurNotManaged.getId());
		assertNotEquals(t.getEmployeur(), employeurNotManaged);
	}
	
	/**
	 * Ce test montre que le equals ne fonctionne pas, même si la comparaison des classes se fait avec un
	 * instanceof. En fait, le this.name utilisé dans le equals ou le this est le proxy renvoie null, ce qui
	 * n'est pas égal à "Anybody else".
	 */
	@Test
	public void testEqualsWithAccessToPropertiesDoesNotWorkWithProxies(){
		EmployeurPresqueCorrect employeurNotManaged = new EmployeurPresqueCorrect();
		employeurNotManaged.setId(1001);
		employeurNotManaged.setName("Anybody else");
		
		Travailleur t = travailleurDao.find(1001);
		
		assertNotEquals(t.getEmployeurPresqueCorrect(), employeurNotManaged);
		assertEquals(t.getEmployeurPresqueCorrect().getName(), employeurNotManaged.getName());
		assertEquals(t.getEmployeurPresqueCorrect().getId(), employeurNotManaged.getId());
	}
	
	/**
	 * Par rapport à testEqualsWithAccessToPropertiesDoesNotWorkWithProxies, ce test initialise le proxy avant
	 * de tester le equals, ce qui n'arrange rien.
	 */
	@Test
	public void testEqualsWithAccessToPropertiesDoesNotWorkWithProxiesEvenIfInitialized(){
		EmployeurPresqueCorrect employeurNotManaged = new EmployeurPresqueCorrect();
		employeurNotManaged.setId(1001);
		employeurNotManaged.setName("Anybody else");
		
		Travailleur t = travailleurDao.find(1001);
		
		assertEquals(t.getEmployeurPresqueCorrect().getName(), employeurNotManaged.getName());
		assertEquals(t.getEmployeurPresqueCorrect().getId(), employeurNotManaged.getId());
		assertNotEquals(t.getEmployeurPresqueCorrect(), employeurNotManaged);
	}
	
	/**
	 * Cette fois l'implémentation du equals utilise un instanceof et passe par un getName() plutôt
	 * qu'un accès direct à name. Le equals fonctionne correctement.
	 */
	@Test
	public void testCorrectEqualsWorksWithProxies(){
		EmployeurCorrect employeurNotManaged = new EmployeurCorrect();
		employeurNotManaged.setId(1002);
		employeurNotManaged.setName("Another Anybody else");
		
		Travailleur t = travailleurDao.find(1001);
		
		assertEquals(t.getEmployeurCorrect(), employeurNotManaged);
		assertEquals(t.getEmployeurCorrect().getName(), employeurNotManaged.getName());
		assertEquals(t.getEmployeurCorrect().getId(), employeurNotManaged.getId());
	}
	
	/**
	 * Par rapport à testCorrectEqualsWorksWithProxies, ce test initialise le proxy avant de tester le
	 * equals, ce qui ne l'empêche pas de fonctionner.
	 */
	@Test
	public void testCorrectEqualsWorksWithProxiesEvenIfInitialized(){
		EmployeurCorrect employeurNotManaged = new EmployeurCorrect();
		employeurNotManaged.setId(1002);
		employeurNotManaged.setName("Another Anybody else");
		
		Travailleur t = travailleurDao.find(1001);
		assertEquals(t.getEmployeurCorrect().getName(), employeurNotManaged.getName());
		assertEquals(t.getEmployeurCorrect().getId(), employeurNotManaged.getId());
		assertEquals(t.getEmployeurCorrect(), employeurNotManaged);
	}
}
