package be.fabrice.fetch.specific;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;

import be.fabrice.utils.TransactionalTestBase;

@ContextConfiguration(locations="classpath:fetch/specific/test-spring.xml")
public class TestFetchSpecificObjectsInCollections extends TransactionalTestBase {
	
	@BeforeMethod
	public void init(){
		Operation operations = sequenceOf(
				deleteAllFrom("review","reviewer"),
				insertInto("REVIEWER").columns("id","name")
					.values(1,"rev1")
					.values(2,"rev2")
					.values(3,"rev3")
					.build(),
				insertInto("REVIEW").columns("id","project","comments","reviewer_fk")
					.values(1,"proj1",12,1)
					.values(2,"proj2",15,1)
					.values(3,"proj3",7,1)
					.values(4,"proj1",11,2)
					.values(5,"proj2",13,2)
					.values(6,"proj3",5,3)
					.build());
		
		DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), operations);
		dbSetup.launch();

	}
	/**
	 * On récupère les reviewer qui ont plus de 10 commentaires (et on voudrait n'avoir que les reviews qui ont plus
	 * de 10 commentaires.
	 */
	@Test(description="all reviews are fetched by default when lazy loaded")
	public void testAllObjectsInCollectionsByDefault(){
		List<Reviewer> reviewers = getSession().createQuery("select distinct r from Reviewer r join r.reviews as review where review.comments > 10 order by r.name").list();
		assertThat(reviewers).hasSize(2);
		
		assertThat(reviewers.get(0).getReviews()).hasSize(3); //Toutes les reviews sont prises (parce que lazy fetched)
	}
	
	@Test(description="number of lines is correct but all reviews are taken")
	public void testCorrectNumberOfLinesButAllObjectsInCollectionsByDefault(){
		List<Reviewer> reviewers = getSession().createQuery("select r from Reviewer r join r.reviews as review where review.comments > 10 order by r.name").list();
		assertThat(reviewers).hasSize(4); //2 lignes rev1 et 2 lignes rev2
		
		//Mais...
		
		assertThat(reviewers.get(0).getReviews()).hasSize(3); //Toutes les reviews sont prises (parce que lazy fetched)
	}
	
	@Test(description="fetch eager contraints the list to the found reviews")
	public void testFetchEager(){
		List<Reviewer> reviewers = getSession().createQuery("select distinct r from Reviewer r join fetch r.reviews as review where review.comments > 10 order by r.name").list();
		
		assertThat(reviewers).hasSize(2);
		
		assertThat(reviewers.get(0).getReviews()).hasSize(2); //Seules les reviews fetchées sont prises (plus de lazy fetched)
	}
	
	@Test(description="eager fetching does not always work")
	public void testEagerLoadingNotWorking(){
		//Load reviewer in session
		Reviewer r = (Reviewer) getSession().get(Reviewer.class, 1);
		assertThat(r.getReviews()).hasSize(3);
		
		List<Reviewer> reviewers = getSession().createQuery("select distinct r from Reviewer r join fetch r.reviews as review where review.comments > 10 order by r.name").list();
		
		assertThat(reviewers).hasSize(2);
		
		assertThat(reviewers.get(0).getReviews()).hasSize(3); //Seules les reviews fetchées sont prises (plus de lazy fetched)
		
		assertThat(reviewers.get(0).getReviews()).extracting("comments").contains(12,15,7);
	}
	
	/**
	 * Comportement dangereux, @see be.fabrice.fetch.eager.JoinFetchRiskyBehaviourTest.testModifyIncorrectCollection()
	 */
	@Test(description="eager fetch may be dangerous")
	public void testEagerFetchDanger(){
		List<Reviewer> reviewers = getSession().createQuery("select distinct r from Reviewer r join fetch r.reviews as review where review.comments > 10 order by r.name").list();
		
		Review newReview = new Review();
		newReview.setComments(0);
		newReview.setProject("new project");
		reviewers.get(0).getReviews().add(newReview);
		
		getSession().flush();
		
		Reviewer r = (Reviewer) getSession().get(Reviewer.class, 1);
		assertThat(r.getReviews()).hasSize(3);
		assertThat(r.getReviews()).extracting("comments").contains(12,15,0); //One review was deleted
	}
	
	/**
	 * Fonctionne, mais reste dangereux si on ajoute des jointure: tous les objets seront repris. La taille de Object[]
	 * est difficile à fixer.
	 * 
	 * Même chose pour l'ordre des résultats retournés qui dépend de l'ordre des jointures.
	 * 
	 * Implémentation fragile.
	 */
	@Test(description="use the returned rows - first")
	public void testUseTheForceLuke1(){
		List<Object[]> reviewers = getSession().createQuery("from Reviewer r join r.reviews as review where review.comments > 10 order by r.name").list();
		assertThat(reviewers).hasSize(4);
		
		assertThat(reviewers.get(0)).hasSize(2);
		
		assertThat(reviewers.get(0)[0]).isInstanceOf(Reviewer.class);
		assertThat(reviewers.get(0)[1]).isInstanceOf(Review.class);
	}
	
	@Test(description="use the returned rows - second")
	public void testUseTheForceLuke2(){
		List<Object[]> reviewers = getSession().createQuery("select r, review from Reviewer r join r.reviews as review where review.comments > 10 order by r.name").list();
		assertThat(reviewers).hasSize(4);
		
		assertThat(reviewers.get(0)[0]).isInstanceOf(Reviewer.class);
	}
	
	@Test(description="use the returned rows - second")
	public void testUseTheForceLuke3(){
		List<Result> results = getSession().createQuery("select new be.fabrice.fetch.specific.Result(r, review) from Reviewer r join r.reviews as review where review.comments > 10 order by r.name").list();
		assertThat(results).hasSize(4);
		
		assertThat(results).extracting("reviewer.name").contains("rev1","rev2");
		
		assertThat(results).extracting("review.comments").contains(12,15,11,13);
	}
}
