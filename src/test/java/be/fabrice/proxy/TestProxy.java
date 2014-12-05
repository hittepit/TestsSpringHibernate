package be.fabrice.proxy;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.sql.DataSource;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;

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
	@Autowired
	private DataSource dataSource;
	
	@BeforeMethod
	public void beforeTest(){
		executeSqlScript("proxy/test-script.sql", false);
		
		Operation deletes = deleteAllFrom("OWGC","OWING");
		Operation owgc = insertInto("OWGC").columns("ID","NAME")
				.values(1000,"test")
				.values(1001,"test2")
				.build();
		Operation owing = insertInto("OWING").columns("ID","NAME")
				.values(1000,"test")
				.values(1001,"test2")
				.build();
		
		Operation operation = sequenceOf(deletes,owgc,owing);
		
		DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), operation);
		dbSetup.launch();
	}
	
	@Test
	public void loadMustLoadAProxyIfEntityWasNotLoadedYet(){
		ObjectWithGetClass o = (ObjectWithGetClass) getSession().load(ObjectWithGetClass.class, 1000);
		assertFalse(Hibernate.isInitialized(o));
	}
	
	@Test
	public void loadMustLoadAnEntityIfEntityWasLoadedBefore(){
		ObjectWithGetClass o1 = (ObjectWithGetClass) getSession().get(ObjectWithGetClass.class, 1000);
		assertTrue(Hibernate.isInitialized(o1), "It's not a proxy indeed");
		ObjectWithGetClass o2 = (ObjectWithGetClass) getSession().load(ObjectWithGetClass.class, 1000);
		assertTrue(Hibernate.isInitialized(o2));
		assertSame(o2, o1, "It's the same object");
	}
	
	public void listMustNotContaintProxyIfPromyxWasLoadedBefore(){
		
	}
	
	public void listMayContainProxyIfProxyWasLoadedBefore(){
		
	}
	
	@Test
	public void callOfGetterMustInitalizeTheProxy(){
		ObjectWithGetClass o = (ObjectWithGetClass) getSession().load(ObjectWithGetClass.class, 1000);
		assertFalse(Hibernate.isInitialized(o));
		o.getName();
		assertTrue(Hibernate.isInitialized(o));
	}

	@Test
	public void callOfEqualsMustInitializeTheProxy(){
		ObjectWithGetClass o = (ObjectWithGetClass) getSession().load(ObjectWithGetClass.class, 1000);
		assertFalse(Hibernate.isInitialized(o));
		o.equals(null);
		assertTrue(Hibernate.isInitialized(o));
	}
	
	
	//TODO tester les résultats des invokes
	
	@Test
	public void reflexiveInvocationOfPublicMethodOfTheProxyMustInitializeTheProxy() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		ObjectWithGetClass o = (ObjectWithGetClass) getSession().load(ObjectWithGetClass.class, 1000);
		assertFalse(Hibernate.isInitialized(o));
		Method m = o.getClass().getMethod("getName", new Class[]{});
		m.invoke(o, new Object[]{});
		assertTrue(Hibernate.isInitialized(o));

	}
	
	@Test
	public void reflexiveInvocationOfPublicMethodOfTheBaseClassMustInitializeTheProxy() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		ObjectWithGetClass o = (ObjectWithGetClass) getSession().load(ObjectWithGetClass.class, 1000);
		assertFalse(Hibernate.isInitialized(o));
		Method m = ObjectWithGetClass.class.getMethod("getName", new Class[]{});
		m.invoke(o, new Object[]{});
		assertTrue(Hibernate.isInitialized(o));
	}
	
	@Test(expectedExceptions=NoSuchMethodException.class)
	public void privateMethodDoesNotExistInTheProxyClass() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		ObjectWithGetClass o = (ObjectWithGetClass) getSession().load(ObjectWithGetClass.class, 1000);
		assertFalse(Hibernate.isInitialized(o));
		o.getClass().getDeclaredMethod("getNameInPrivate", new Class[]{});
	}
	
	@Test
	public void privateMethodOfTheBaseClassDoesNotInitializeTheProxy() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		ObjectWithGetClass o = (ObjectWithGetClass) getSession().load(ObjectWithGetClass.class, 1000);
		assertFalse(Hibernate.isInitialized(o));
		Method m = ObjectWithGetClass.class.getDeclaredMethod("getNameInPrivate", new Class[]{});
		m.setAccessible(true);
		assertNull(m.invoke(o, new Object[]{}));
		assertFalse(Hibernate.isInitialized(o));
		m.setAccessible(false);
	}
	
	/**
	 * <p>Ce test montre que le equals ne fonctionne pas, car la méthode equals est définie avec une comparaison de classe.
	 * <em>if (getClass() != obj.getClass())</em>
	 * Le premier getClass retourne {@link ObjectWithGetClass}, le deuxième retourne la classe du proxy</p>
	 * 
	 * <p>En fait, c'est même pire que ça comme le montre un futur test.</p>
	 */
	@Test
	public void equalsWithGetClassEqualityMustReturnFalseWhenProxyIsOnTheRightSide(){
		ObjectWithGetClass referenceObject = new ObjectWithGetClass();
		referenceObject.setId(1000);
		referenceObject.setName("test");
		
		ObjectWithGetClass o = (ObjectWithGetClass) getSession().load(ObjectWithGetClass.class, 1000);
		assertFalse(referenceObject.equals(o));
	}

	/**
	 * <p>Le plus étrange, c'est qu'un objet n'est pas égal à un proxy (dans le cas d'un equals avec getClass)
	 * mais que le proxy est égal à l'objet. Ce qui est une violation du principe de commutativité de l'égalité.</p>
	 * <p>Dans le deuxième cas, la méthode equals est celle de {@link ObjectWithGetClass} et donc this.getClass() ne
	 * retourne plus le proxy, mais {@link ObjectWithGetClass}</p>
	 * <p>Cependant, le deuxème equals, appelé sur le proxy, l'initialise...</p>
	 */
	@Test
	public void equalsCommutativityWithOneProxyMustBeBrokenWhenImplementedWithGetClass(){
		ObjectWithGetClass referenceObject = new ObjectWithGetClass();
		referenceObject.setId(1000);
		referenceObject.setName("test");
		
		ObjectWithGetClass o = (ObjectWithGetClass) getSession().load(ObjectWithGetClass.class, 1000);
		assertFalse(Hibernate.isInitialized(o));
		assertFalse(referenceObject.equals(o));
		assertFalse(Hibernate.isInitialized(o)," Not Initialized");
		assertTrue(o.equals(referenceObject), "Wierd but using the equals of ObjectWithClass");
		assertTrue(Hibernate.isInitialized(o), "Initialized!");
	}

	/**
	 * Le fait d'initialiser le proxy avant le equals ne change rien car la classes du proxy est toujours différente de la classe
	 * de l'objet de référence.
	 */
	@Test
	public void equalsCommutativityWithOneInitializedMustBeBrokenWhenImplementedWithGetClass(){
		ObjectWithGetClass referenceObject = new ObjectWithGetClass();
		referenceObject.setId(1000);
		referenceObject.setName("test");
		
		ObjectWithGetClass o = (ObjectWithGetClass) getSession().load(ObjectWithGetClass.class, 1000);
		Hibernate.initialize(o);
		assertFalse(referenceObject.equals(o));
		assertTrue(o.equals(referenceObject), "Wierd but using the equals of ObjectWithClass");
		
	}
	
	/**
	 * <p>Ce test montre que le equals ne fonctionne pas, même avec un instanceOf, car la méthode equals 
	 * compare directement les propriétés et comme le proxy n'est pas initialisé, elles valent null</p>
	 * <p>Attention aux passages en debug car il peuvent initialiser des proxies. Quelque chose qui ne fonctionne
	 * pas en exécution normale peut subitement fonctionner (en tout cas fonctionner différemment) lorsqu'on
	 * passe en debu. </p>
	 */
	@Test
	public void equalsWithInstanceOfButNoGetterMustReturnFalseWhenProxyIsOnTheRightSide(){
		ObjectWithInstanceOfButNotGetter referenceObject = new ObjectWithInstanceOfButNotGetter();
		referenceObject.setId(1000);
		referenceObject.setName("test");
		
		ObjectWithInstanceOfButNotGetter o = (ObjectWithInstanceOfButNotGetter) getSession().load(ObjectWithInstanceOfButNotGetter.class, 1000);
		assertFalse(referenceObject.equals(o));
	}
	
	/**
	 * <p>L'égalité n'est toujours pas commutative, mais la raison est un peu différente.</p>
	 * <p>Pour le premier equals, les propriétés du proxy sont bien à null, d'où le résultat incorrect.</p>
	 * <p>Dans le deuxième equals, comme on accède à une méthode du proxy, il est initialisé et donc les propriétés
	 * ne sont plus nulles.</p>
	 */
	@Test
	public void equalsCommutativityWithOneProxyMustBeBrokenWhenImplementedWithInstanceOfAndNoGetter(){
		ObjectWithInstanceOfButNotGetter referenceObject = new ObjectWithInstanceOfButNotGetter();
		referenceObject.setId(1000);
		referenceObject.setName("test");
		
		ObjectWithInstanceOfButNotGetter o = (ObjectWithInstanceOfButNotGetter) getSession().load(ObjectWithInstanceOfButNotGetter.class, 1000);
		assertFalse(Hibernate.isInitialized(o));
		assertFalse(referenceObject.equals(o));
		assertFalse(Hibernate.isInitialized(o),"Not initialized yet");
		assertTrue(o.equals(referenceObject));
		assertTrue(Hibernate.isInitialized(o), "Initialized!!!");
	}

	//TODO montrer que le fait d'initialiser le proxy ne change rien dans ce type d'égalité
	
	//TODO corriger le reste
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
