package be.fabrice.fetch.eager;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;

import be.fabrice.utils.TransactionalTestBase;
import be.fabrice.utils.logging.SimpleSql;

@ContextConfiguration(locations="classpath:fetch/eager/test-spring.xml")
public class JoinFetchRiskyBehaviourTest extends TransactionalTestBase {
	
	@BeforeMethod
	public void initData(){
		getSession().clear();
		Operation operations = sequenceOf(
				deleteAllFrom("enfant","parent","bienfant","biparent"),
				insertInto("parent").columns("id","name")
					.values(1,"parent1")
					.values(2,"parent2")
					.build(),
				insertInto("enfant").columns("id","name","gender","parent_fk")
					.values(1,"female1",'F',1)
					.values(2,"female2",'F',1)
					.values(3,"male1",'M',1)
					.values(4,"male2",'M',1)
					.values(5,"female3",'F',2)
					.values(6,"female4",'F',2)
					.build(),
				insertInto("biparent").columns("id","name")
					.values(1,"parent1")
					.build(),
				insertInto("bienfant").columns("id","name","gender","parent_fk")
					.values(1,"female1",'F',1)
					.values(2,"female2",'F',1)
					.values(3,"male1",'M',1)
					.values(4,"male2",'M',1)
					.build());
		
		DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), operations);
		dbSetup.launch();
		
		SimpleSql.reinitSqlList();
	}

	//Récupération d'un parent1 avec tous ses enfants, sans join fetch
	@Test
	public void findParent1WithoutFetch(){
		Parent parent = (Parent) getSession().createQuery("from Parent p where p.name=:n").setParameter("n", "parent1").uniqueResult();
		
		assertThat(parent).isNotNull();
		assertThat(Hibernate.isInitialized(parent.getEnfants())).isFalse(); //Lazy
		assertThat(parent.getEnfants()).hasSize(4);
	}

	//Récupération d'un parent1 avec tous ses enfants, avec join fetch, intéressant pour retrouver de manière eager les dépendances
	@Test
	public void findParent1WithFetch(){
		Parent parent = (Parent) getSession().createQuery("from Parent p join fetch p.enfants where p.name=:n").setParameter("n", "parent1").uniqueResult();
		
		assertThat(parent).isNotNull();
		assertThat(Hibernate.isInitialized(parent.getEnfants())).isTrue(); //Eager
		assertThat(parent.getEnfants()).hasSize(4);
	}
	
	
	//Récupération des parent qui ont des enfants filles, sans fetch
	@Test
	public void findParentWithGirlsNoFetch(){
		List<Parent> parents = getSession().createQuery("select distinct p from Parent p join p.enfants as enfant where enfant.gender = :g").setParameter("g", 'F').list();
		assertThat(parents).hasSize(2);
	}
	
	//Récupération des parent qui ont des enfants filles, avec fetch identique... ou presque
	@Test
	public void findParentWithGirlsWithFetch(){
		List<Parent> parents = getSession().createQuery("select distinct p from Parent p join fetch p.enfants as enfant where enfant.gender = :g").setParameter("g", 'F').list();
		assertThat(parents).hasSize(2);
	}
	
	//En effet, l'effet du fetch, c'est la récupération du parent 1 avec uniquement ses filles (critère sur filles)
	@Test
	public void findParentWithGirlsWithFetchIsWierd(){
		List<Parent> parents = getSession().createQuery("select distinct p from Parent p join fetch p.enfants as enfant where enfant.gender = :g").setParameter("g", 'F').list();
		assertThat(parents).hasSize(2);
		
		//Cependant
		for(Parent p:parents){
			assertThat(Hibernate.isInitialized(p.getEnfants())).isTrue(); //Bien sûr
			assertThat(p.getEnfants()).hasSize(2); //Même le parent 1, alors qu'il a 4 enfant
		}
	}
	
	//Récupération des parents avec des filles, puis des parents avec des garçons => problèmes
	@Test
	public void testParentWithGirlsThenWithBoys(){
		List<Parent> parents = getSession().createQuery("select distinct p from Parent p join fetch p.enfants as enfant where enfant.gender = :g").setParameter("g", 'F').list();
		assertThat(parents).hasSize(2);

		Parent parent1 = (Parent) getSession().createQuery("select distinct p from Parent p join fetch p.enfants as enfant where enfant.gender = :g").setParameter("g", 'M').uniqueResult();
		assertThat(parent1.getName()).isEqualTo("parent1"); //Le seul qui a des garçons
		
		//Cependant
		assertThat(parent1.getEnfants()).extracting("gender").doesNotContain('M');//Ne contient que des filles
	}
	
	//Récupération des parent avec des filles, puis du parent1 => problèmes
	@Test
	public void testParentWithGirlsThenWithId(){
		List<Parent> parents = getSession().createQuery("select distinct p from Parent p join fetch p.enfants as enfant where enfant.gender = :g").setParameter("g", 'F').list();
		assertThat(parents).hasSize(2);

		Parent parent1 = (Parent) getSession().get(Parent.class, 1);
		
		//Cependant
		assertThat(parent1.getEnfants()).hasSize(2); //Alors qu'il en a 4
		assertThat(parent1.getEnfants()).extracting("gender").doesNotContain('M');//Ne contient que des filles
	}
	
	//Modification du parent1 avec ses filles, ajout d'un enfant, suppression des garçons?
	@Test
	public void testModifyIncorrectCollection(){
		Parent parent1 = (Parent) getSession().createQuery("select distinct p from Parent p join fetch p.enfants as enfant where p.name=:n and enfant.gender = :g")
				.setParameter("n","parent1")
				.setParameter("g", 'F').uniqueResult();
		
		//On ne fait pas attention et on ajoute ce qu'on pense être un 5e enfant
		Enfant e = new Enfant();
		e.setName("newFemale");
		e.setGender('F');
		parent1.getEnfants().remove(0);
		parent1.getEnfants().add(e);
		
		getSession().flush(); //Provoque la persistance
		
		List<Enfant> males = getSession().createQuery("from Enfant e where e.gender = :g").setParameter("g", 'M').list();
		
		assertThat(males).hasSize(2); //Ouf... Ils n'ont pas été supprimés
	}
	
	//Modification du parent1 avec ses filles, ajout d'un enfant, suppression des garçons?
	@Test
	public void testDeleteEntityWithIncorrectCollection(){
		Parent parent1 = (Parent) getSession().createQuery("select distinct p from Parent p join fetch p.enfants as enfant where p.name=:n and enfant.gender = :g")
				.setParameter("n","parent1")
				.setParameter("g", 'F').uniqueResult();
		
		getSession().delete(parent1);
		getSession().flush(); //Provoque la persistance
		
		List<Enfant> males = getSession().createQuery("from Enfant e where e.gender = :g").setParameter("g", 'M').list();
		assertThat(males).hasSize(2); //Or, ils auraient dû être supprimés
		
		List<Enfant> females = getSession().createQuery("from Enfant e where e.gender = :g").setParameter("g", 'F').list();
		assertThat(females).hasSize(2); //Puisque les filles ont été supprimées
	}
	
	//Et donc, quand la relation est bidirectionnelle
	@Test(expectedExceptions=ConstraintViolationException.class)
	public void testDeleteBidirectionnalEntityWithPartialList(){
		BiParent parent = (BiParent) getSession().createQuery("select distinct p from BiParent p join fetch p.enfants as enfant where p.name=:n and enfant.gender = :g")
				.setParameter("n","parent1")
				.setParameter("g", 'F').uniqueResult();
		
		assertThat(parent.getEnfants()).hasSize(2); //Que les filles
		getSession().delete(parent); //Lance une exception car les filles sont supprimées (cascading), mais pas les garçons qui référencie toujours le parent
		
		getSession().flush();
	}
	
	//Alors que... 
	@Test
	public void testDeleteBidirectionnalEntityWithCompleteList(){
		BiParent parent = (BiParent) getSession().createQuery("select distinct p from BiParent p join fetch p.enfants as enfant where p.name=:n")
				.setParameter("n","parent1").uniqueResult();
		
		assertThat(parent.getEnfants()).hasSize(4); //Les filles et les garçons
		getSession().delete(parent);
		getSession().flush();
		
		List<BiEnfant> enfants = getSession().createQuery("from BiEnfant").list();
		assertThat(enfants).isEmpty(); //Ils ont bien été supprimés
		assertThat(getSession().get(BiParent.class,1)).isNull();
	}
}
