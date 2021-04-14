package be.fabrice.join.navigation;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.assertj.core.api.iterable.Extractor;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;

@ContextConfiguration(locations="classpath:navigation/test-navigation-spring.xml")
public class TestFindingMastersHavingDogsNamed extends AbstractTransactionalTestNGSpringContextTests {
	@Autowired
	private DataSource dataSource;
	@Autowired
	private SessionFactory sessionFactory;

	@BeforeMethod
	public void init() {
		Operation operations = sequenceOf(
				deleteAllFrom("DOG","MASTER"),
				insertInto("MASTER").columns("ID","NOM")
					.values(1000,"Albert")
					.values(1001, "Bernard")
					.values(1002, "Charlie")
					.build(),
				insertInto("DOG").columns("ID","NOM","MASTER_FK")
					.values(1001,"Bill",1000)
					.values(1002,"Patch",1000)
					.values(1003, "Bill", 1001)
					.values(1004, "Patch", 1002)
					.values(1005, "Patch", null)
					.build()
		);
		
		DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), operations);
		dbSetup.launch();		
	}
	
	@AfterClass
	public void clean() {
		Operation operations = deleteAllFrom("DOG","MASTER");
		
		DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), operations);
		dbSetup.launch();		
	}

	@Test
	public void strangeProblem() {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("select d from Dog as d inner join d.master as m where m.nom = :nom")
				.setParameter("nom", "Albert");
		List<Dog> dogs = query.list();
		assertThat(dogs).hasSize(2);
		Dog d = dogs.get(0);
		Master master = d.getMaster();
		assertThat(master.getNom()).isEqualTo("Albert");
		Iterator<Dog> it = master.getDogs().iterator();
		while (it.hasNext()) {
			Dog dog = it.next();
			it.remove();
		}
		assertThat(master.getDogs()).isNotEmpty(); //Car le hashcode de Dog a changé
	}

	@Test
	public void strangeProblemSolution() {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("select m from Master as m where m.nom = :nom")
				.setParameter("nom", "Albert");
		Master master = (Master) query.uniqueResult();
		assertThat(master.getNom()).isEqualTo("Albert");
		assertThat(master.getDogs()).hasSize(2);
		Iterator<Dog> it = master.getDogs().iterator();
		while (it.hasNext()) {
			Dog dog = it.next();
			it.remove();
		}
		assertThat(master.getDogs()).isEmpty();
	}

	@Test
	public void searchFromMasterWithLeftJoin() {
		Session session = sessionFactory.getCurrentSession();
		List<Master> masters = session.createQuery("from Master m left join m.dogs d where d.nom=:nom")
			.setParameter("nom", "Bill")
			.list();

		assertThat(masters).hasSize(2);
		assertThat(masters).extracting(new Extractor<Object, Class<?>>() {
			@Override
			public Class<?> extract(Object input) {
				return input.getClass();
			}
		}).doesNotContain(Master.class,Dog.class); //Object[]
	}
	
	@Test
	public void searchFromMasterWithInnerJoin() {
		Session session = sessionFactory.getCurrentSession();
		List<Master> masters = session.createQuery("from Master m inner join m.dogs d where d.nom=:nom")
			.setParameter("nom", "Bill")
			.list();
		
		assertThat(masters).hasSize(2);
		assertThat(masters).extracting(new Extractor<Object, Class<?>>() {
			@Override
			public Class<?> extract(Object input) {
				return input.getClass();
			}
		}).doesNotContain(Master.class,Dog.class); //Object[]
	}
	
	
	@Test
	public void searchFromMasterWithRightJoinAndWithOrphanDogContainsOrphanDogAndNoMaster() {
		Session session = sessionFactory.getCurrentSession();
		List<Master> masters = session.createQuery("from Master m right join m.dogs d where d.nom=:nom")
			.setParameter("nom", "Patch")
			.list();
		
		assertThat(masters).hasSize(3);
		assertThat(masters).extracting(new Extractor<Object, Class<?>>() {
			@Override
			public Class<?> extract(Object input) {
				return input.getClass();
			}
		}).doesNotContain(Master.class,Dog.class); //Object[]
	}
	
	@Test
	public void searchFromMasterWithRightJoinAndWithoutOrphanDog() {
		Session session = sessionFactory.getCurrentSession();
		List<Master> masters = session.createQuery("from Master m right join m.dogs d where d.nom=:nom")
			.setParameter("nom", "Bill")
			.list();
		
		assertThat(masters).hasSize(2);
		assertThat(masters).extracting(new Extractor<Object, Class<?>>() {
			@Override
			public Class<?> extract(Object input) {
				return input.getClass();
			}
		}).doesNotContain(Master.class,Dog.class); //Object[]
	}
	
	@Test
	public void searchFromMasterWithSelectWithRightJoinAndOrphanDog() {
		Session session = sessionFactory.getCurrentSession();
		List<Master> masters = session.createQuery("select m from Master m right join m.dogs d where d.nom=:nom")
			.setParameter("nom", "Patch")
			.list();
		
		assertThat(masters).hasSize(3);
		assertThat(masters).containsNull(); //Donc pas correct
	}
	
	@Test
	public void searchFromMasterWithSelectWithLeftJoinAndOrphanDog() {
		Session session = sessionFactory.getCurrentSession();
		List<Master> masters = session.createQuery("select m from Master m left join m.dogs d where d.nom=:nom")
			.setParameter("nom", "Patch")
			.list();
		
		assertThat(masters).hasSize(2);
		assertThat(masters).extracting("nom").contains("Albert","Charlie");
	}
	
	@Test
	public void searchFromMasterWithSelectWithInnerJoinAndOrphanDog() {
		Session session = sessionFactory.getCurrentSession();
		List<Master> masters = session.createQuery("select m from Master m inner join m.dogs d where d.nom=:nom")
			.setParameter("nom", "Patch")
			.list();
		
		assertThat(masters).hasSize(2);
		assertThat(masters).extracting("nom").contains("Albert","Charlie");
	}
	
	@Test
	public void searchFromDog() {
		Session session = sessionFactory.getCurrentSession();
		List<Master> masters = session.createQuery("select d.master from Dog d where d.nom=:nom")
			.setParameter("nom", "Bill")
			.list();
		
		assertThat(masters).hasSize(2);
		assertThat(masters).extracting("nom").contains("Albert","Bernard");
	}
	
	@Test
	public void searchFromDogNoProblemWithOrphanDogsBecauseInnerJoinIsMade() {
		Session session = sessionFactory.getCurrentSession();
		List<Master> masters = session.createQuery("select d.master from Dog d where d.nom=:nom")
			.setParameter("nom", "Patch")
			.list();
		
		assertThat(masters).hasSize(2);
		assertThat(masters).extracting("nom").contains("Albert","Charlie");
	}
}
