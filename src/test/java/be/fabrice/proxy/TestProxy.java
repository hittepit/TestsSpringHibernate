package be.fabrice.proxy;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.utils.TransactionalTestBase;

/**
 * <p>Le but de ce test est de montrer certains aspects de l'utilisation de proxies par Hibernate,
 * soit parce que la propriété est lazy-loadée, soit parce que load est utilisé.</p>
 * Plusieurs aspects sont examinés:
 * <ul>
 * <li>pièges dans l'écriture de la méthode equals avec des proxies</li>
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
 * <li>initialisation des proxies;</li>
 * <li>différences entre session.get et session.load.</li>
 * </ul>
 * <p>Attention que le comportement des proxies peut être affecté par la librairie utilisée pour le proxying. Ici, c'est javassist qui est
 * utilisé (par défaut dans cette version d'Hibernate).</p>
 * <p>Attention aussi qu'en debug, l'accès à certains éléments d'un proxy peut l'initialiser.</p>
 * @author fabrice.claes
 *
 */
@ContextConfiguration(locations="classpath:proxy/test-proxy-spring.xml")
public class TestProxy extends TransactionalTestBase {
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
		
		Travailleur t = (Travailleur) getSession().get(Travailleur.class,1001);
		
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
		
		Travailleur t = (Travailleur) getSession().get(Travailleur.class,1001);
		
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
		
		Travailleur t = (Travailleur) getSession().get(Travailleur.class,1001);
		
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
		
		Travailleur t = (Travailleur) getSession().get(Travailleur.class,1001);
		
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
		
		Travailleur t = (Travailleur) getSession().get(Travailleur.class,1001);
		
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
		
		Travailleur t = (Travailleur) getSession().get(Travailleur.class,1001);

		assertEquals(t.getEmployeurCorrect().getName(), employeurNotManaged.getName());
		assertEquals(t.getEmployeurCorrect().getId(), employeurNotManaged.getId());
		assertEquals(t.getEmployeurCorrect(), employeurNotManaged);
	}
	
	/**
	 * Le proxy est initialisé lorsqu'on accède à un getter de propriété
	 */
	@Test
	public void testProxyIntializedIfPropertyAccessedThroughGetter(){
		Travailleur t = (Travailleur) getSession().get(Travailleur.class,1001);
		
		assertFalse(Hibernate.isInitialized(t.getEmployeur()), "Proxy non initialisé");
		t.getEmployeur().getName();
		assertTrue(Hibernate.isInitialized(t.getEmployeur()), "Proxy initialisé");
	}

	/**
	 * Alors que la seule propriété connue du proxy est l'id de l'entité (nécessaire pour qu'il
	 * puisse la charger), la passage par le getter de l'id (getId) initialise le proxy.
	 */
	@Test
	public void testProxyIntializedIfIdAccessedThroughGetter(){
		Travailleur t = (Travailleur) getSession().get(Travailleur.class,1001);
		
		assertFalse(Hibernate.isInitialized(t.getEmployeur()), "Proxy non initialisé");
		t.getEmployeur().getId();
		assertTrue(Hibernate.isInitialized(t.getEmployeur()), "Proxy initialisé");
	}
	
	/**
	 * Ce test montre que si un proxy est déjà chargé en session, c'est un proxy qui sera renvoyé par
	 * la méthode session.get. Voir aussi le comportement de session.get dans testGetDoesNotLoadAProxy.
	 */
	@Test
	public void testProxyIsReturnedIfAlreadyLoadedInSession(){
		Travailleur t = (Travailleur) getSession().get(Travailleur.class,1001);
		Employeur e = (Employeur) getSession().get(Employeur.class,1000);
		
		assertFalse(e.getClass().equals(Employeur.class),"C'est un proxy, parce que déjà chargé");
		assertSame(e, t.getEmployeur(),"En fait...");
	}
	
	/**
	 * L'utilisation de la méthode session.get ne renvoie pas de proxy (sauf si un proxy a déjà été chargé en
	 * session, voir testProxyIsReturnedIfAlreadyLoadedInSession)
	 */
	@Test 
	public void testGetDoesNotLoadAProxy(){
		Employeur e = (Employeur) getSession().get(Employeur.class,1000);
		assertEquals(e.getClass(),Employeur.class,"Ce n'est pas un proxy");
	}
	
	/**
	 * La méthode session.load renvoie un proxy si l'entité n'a pas encore été chargée en session. Voir aussi
	 * testLoadLoadsTheEntityIfAlreadyLoaded
	 */
	@Test
	public void testLoadLoadsAProxyIfNotYetLoaded(){
		Employeur e = (Employeur) getSession().load(Employeur.class,1000);
		assertFalse(Hibernate.isInitialized(e));
	}
	
	/**
	 * Si l'entité a déjà été chargée en session, session.load renvoie l'entité.
	 */
	@Test
	public void testLoadLoadsTheEntityIfAlreadyLoaded(){
		Employeur e1 = (Employeur) getSession().get(Employeur.class,1000);
		Employeur e2 = (Employeur) getSession().load(Employeur.class,1000);
		assertEquals(e2.getClass(),Employeur.class,"Ce n'est pas un proxy");
		assertSame(e1,e2);
	}
	
	/**
	 * Montre qu'il est possible de récupérer l'entité cachée derrière un proxy. En général, ce n'est pas utile,
	 * donc ne devrait jamais être utilisé.
	 */
	@Test
	public void testItIsPossibleToGetTheEntity(){
		Employeur e = (Employeur) getSession().load(Employeur.class,1000);
		assertFalse(Hibernate.isInitialized(e));
		assertTrue(e instanceof HibernateProxy);
		Employeur entity = (Employeur)((HibernateProxy)e).getHibernateLazyInitializer().getImplementation();
		assertEquals(entity.getClass(),Employeur.class);
	}
}
