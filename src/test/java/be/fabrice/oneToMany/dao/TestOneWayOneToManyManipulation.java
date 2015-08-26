package be.fabrice.oneToMany.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.oneToMany.entity.Employeur;
import be.fabrice.oneToMany.entity.Travailleur;

/**
 * Pour voir qu'il y a bien un insert suivi d'un update, il faut regarder les logs. Pas de test automatique.
 * @author fabrice.claes
 *
 */
@ContextConfiguration(locations="classpath:oneToMany/test-oneToMany-spring.xml")
public class TestOneWayOneToManyManipulation extends AbstractTransactionalTestNGSpringContextTests{
	@Autowired
	private EmployeurDao dao;
	@Autowired
	private SessionFactory sessionFactory;
	
	@BeforeMethod
	public void beforeMethod(){
		executeSqlScript("oneToMany/test-script.sql", false);
	}

	@Test
	public void testInsertWithCascadingMakesInsertThenUpdateOnTravailleur(){
		List<Travailleur> travailleurs = new ArrayList<Travailleur>();
		Travailleur t = new Travailleur();
		t.setNom("t1");
		travailleurs.add(t);
		t = new Travailleur();
		t.setNom("t2");
		travailleurs.add(t);t = new Travailleur();
		t.setNom("t3");
		travailleurs.add(t);
		
		Employeur e = new Employeur();
		e.setName("e1");
		e.setTravailleurs(travailleurs);
		
		dao.save(e);
		sessionFactory.getCurrentSession().flush(); 
	}

	@Test
	public void testUpdateWithCascadingMakesInsertThenUpdateOnTravailleur(){
		Employeur e = dao.find(1000);
		
		Travailleur t = new Travailleur();
		t.setNom("t1");
		
		e.getTravailleurs().add(t);
		
		dao.save(e);
		sessionFactory.getCurrentSession().flush(); 
	}
	
	@Test
	public void testFindEmployeurWithTravailleur(){
		Travailleur t = dao.findTravailleur(2000);
		
		Employeur e = dao.find(t);
		assertNotNull(e);
		
		assertEquals(e.getTravailleurs().size(), 2);
	}
	
	@Test
	public void attachTheSameTravailleurToTwoEmployeur(){
		Employeur e1 = new Employeur();
		e1.setName("Employeur 1");
		
		Employeur e2 = new Employeur();
		e2.setName("Employeur 2");
		
		dao.save(e1);
		dao.save(e2);
		
		Travailleur t = new Travailleur();
		t.setNom("t 1");
		
		e1.add(t);
		e2.add(t);
		
		Session session = sessionFactory.getCurrentSession();
				
		session.flush();
		
		//A ce moment, le modèle est incohérent par rapport à la situation DB
		//En effet...
		
		session.clear();
		
		//En réalité...
		
		e1 = (Employeur) session.get(Employeur.class, e1.getId());
		e2 = (Employeur) session.get(Employeur.class, e2.getId());
		
		assertThat(e1.getTravailleurs()).isEmpty(); //Ne contient pas le travailleur
		assertThat(e2.getTravailleurs()).isNotEmpty(); //Contient le travailleur
	}
}
