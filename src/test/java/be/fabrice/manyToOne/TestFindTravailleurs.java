package be.fabrice.manyToOne;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.utils.TransactionalTestBase;

@ContextConfiguration(locations="classpath:manyToOne/test-manyToOne-spring.xml")
public class TestFindTravailleurs extends TransactionalTestBase {
	@BeforeMethod
	public void beforeMethod(){
		executeSqlScript("manyToOne/test-script.sql", false);
	}
	
	@Test
	public void testFindOneToMany1(){
		Employeur emp = (Employeur)getSession().get(Employeur.class, 1000);
		assertNotNull(emp);
		
		String hql = "from Travailleur t where t.employeur = :e";
		
		List<Travailleur> travailleurs = getSession().createQuery(hql).setParameter("e", emp).list();
		assertEquals(travailleurs.size(), 2);
		assertTrue(travailleurs.contains(new Travailleur(){
			{setNom("Trav1");} //Puisque le equals est sur le nom...
		}));
		assertTrue(travailleurs.contains(new Travailleur(){
			{setNom("Trav2");}
		}));
	}
	
	@Test
	public void testFindOneToMany2(){
		Employeur emp = (Employeur)getSession().get(Employeur.class, 1001);
		assertNotNull(emp);
		
		String hql = "from Travailleur t where t.employeur = :e";
		List<Travailleur> travailleurs = getSession().createQuery(hql).setParameter("e", emp).list();
		
		assertEquals(travailleurs.size(), 1);
		assertTrue(travailleurs.contains(new Travailleur(){
			{setNom("Trav3");}
		}));
	}
	
	@Test
	public void testFindOneToMany3(){
		Employeur emp = (Employeur)getSession().get(Employeur.class, 1002);
		assertNotNull(emp);
		
		String hql = "from Travailleur t where t.employeur = :e";
		List<Travailleur> travailleurs = getSession().createQuery(hql).setParameter("e", emp).list();
		assertTrue(travailleurs.isEmpty());
	}
	
	/**
	 * Il est nécessaire de regarder les logs pour comprendre ce "faux" test:
	 * <ul>
	 * <li>la propriété employeur est trouvée à l'aide d'un join dans le select du travailleur</li>
	 * <li>la propriété employeurWithSelect est trouvée  l'aide d'un select séparé</li>
	 * </ul>
	 */
	@Test
	public void testFindTravailleurUsesJoinByDefautButMayUseSelect(){
		getSession().get(Travailleur.class, 2000);
	}
}
