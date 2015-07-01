package be.fabrice.fetch.eager;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.assertj.core.api.Assertions.assertThat;

import org.hibernate.Hibernate;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.utils.TransactionalTestBase;
import be.fabrice.utils.logging.SimpleSql;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;

@ContextConfiguration(locations="classpath:fetch/eager/test-spring.xml")
public class ProjectionAndFetchingTest extends TransactionalTestBase {
	
	@BeforeMethod
	public void initData(){
		Operation operations = sequenceOf(
				deleteAllFrom("line","ligne","invoice","facture"),
				insertInto("invoice").columns("id","title")
					.values(1,"invoice1")
					.build(),
				insertInto("line").columns("id","name","invoice_fk")
					.values(1,"line1",1)
					.values(2,"line2",1)
					.build(),
				insertInto("facture").columns("id","title")
					.values(1,"facture1")
					.build(),
				insertInto("ligne").columns("id","name","facture_fk")
					.values(1,"ligne1",1)
					.values(2,"ligne2",1)
					.build());
		
		DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), operations);
		dbSetup.launch();
		
		SimpleSql.reinitSqlList();
	}

	@Test
	public void methodGetFetchedEagerWithJoin(){
		Facture facture = (Facture) getSession().get(Facture.class, 1);
		
		assertThat(Hibernate.isInitialized(facture.getLignes())).isTrue();
		
		assertThat(SimpleSql.contains("select .* from facture .* join ligne .*")).isTrue(); //Fetch fait par join
		assertThat(SimpleSql.contains("select .* from LIgNE .*")).isFalse();
	}

	@Test
	public void methodGetDoesNotFetchLazyAtAll(){
		Invoice invoice = (Invoice) getSession().get(Invoice.class, 1);
		
		assertThat(Hibernate.isInitialized(invoice.getLines())).isFalse();
		
		assertThat(SimpleSql.contains(".* line .*")).isFalse(); //Aucune requête ne porte sur line
	}
	
	//Un createQuery fait le fetch si eager, mais avec des select
	@Test
	public void createQueryFetchesEagerWithSelect(){
		Facture facture = (Facture) getSession().createQuery("from Facture f where f.title=:name").setParameter("name", "facture1").uniqueResult();
		
		assertThat(Hibernate.isInitialized(facture.getLignes())).isTrue();
		
		assertThat(SimpleSql.contains("select .* from facture .* join ligne .*")).isFalse(); //Pas de join
		assertThat(SimpleSql.contains("select .* from LIgNE .*")).isTrue(); //Fetch by select
	}
	
	//Un createQuery fait le fetch si eager, mais avec des select, même si le join est fait dans la requête 
	@Test
	public void createQueryFetchesEagerWithSelectEvenIfJoinIsMade(){
		Facture facture = (Facture) getSession().createQuery("select f from Facture f join f.lignes as l where l.name=:name")
				.setParameter("name", "ligne1").uniqueResult();
		
		assertThat(Hibernate.isInitialized(facture.getLignes())).isTrue();
		
		assertThat(SimpleSql.contains("select .* from facture .* join ligne .*")).isTrue(); //Join
		assertThat(SimpleSql.contains("select .* from LIgNE .*")).isTrue(); //Fetch by select
	}
	
	//Un createQuery ne fait pas le fetch si lazy, même si le join est fait dans la requête 
	@Test
	public void createQueryDoesNotFetchLazyEvenIfJoinIsMade(){
		Invoice invoice = (Invoice) getSession().createQuery("select i from Invoice i join i.lines as l where l.name=:name")
				.setParameter("name", "line1").uniqueResult();
		
		assertThat(Hibernate.isInitialized(invoice.getLines())).isFalse();
		
		assertThat(SimpleSql.contains("select .* from invoice .* join line .*")).isTrue(); //Join
	}
	
	//join fetch récupère en eager avec un join
	@Test
	public void createQueryFetchesEagerWithJoinWhenJoinFetched(){
		Facture facture = (Facture) getSession().createQuery("select f from Facture f join fetch f.lignes as l where l.name=:name")
				.setParameter("name", "ligne1").uniqueResult();
		
		assertThat(Hibernate.isInitialized(facture.getLignes())).isTrue();
		
		assertThat(SimpleSql.contains("select .* from facture .* join ligne .*")).isTrue(); //Join
		assertThat(SimpleSql.contains("select .* from LIgNE .*")).isFalse(); //Pas de Fetch by select
	}
	
	//join fetch récupère les lazy avec un join
	@Test
	public void createQueryFetchesLazyWithJoinWhenJoinFetched(){
		Invoice invoice = (Invoice) getSession().createQuery("select i from Invoice i join fetch i.lines as l where l.name=:name")
				.setParameter("name", "line1").uniqueResult();
		
		assertThat(Hibernate.isInitialized(invoice.getLines())).isTrue();
		
		assertThat(SimpleSql.contains("select .* from Invoice .* join line .*")).isTrue(); //Join
		assertThat(SimpleSql.contains("select .* from LINE .*")).isFalse(); //Pas de Fetch by select
	}
}
