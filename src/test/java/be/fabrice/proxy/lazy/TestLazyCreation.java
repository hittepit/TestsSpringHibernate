package be.fabrice.proxy.lazy;

import java.util.List;

import javax.sql.DataSource;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;

import static org.assertj.core.api.Assertions.assertThat;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import be.fabrice.utils.TransactionalTestBase;

@Test(suiteName="Fonctionnement des proxies", testName="Tests sur proxy")
@ContextConfiguration(locations="classpath:proxy/test-proxy-spring.xml")
public class TestLazyCreation extends TransactionalTestBase  {
	@Autowired
	private DataSource dataSource;
	
	@BeforeMethod
	public void beforeTest(){
		Operation deletes = deleteAllFrom("BOOK","CATEGORY");
		Operation categories = insertInto("CATEGORY").columns("CODE","DESCRIPTION")
				.values("SF","Science-fiction")
				.values("HU","Humour")
				.build();
		
		Operation operation = sequenceOf(deletes,categories);
		
		DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), operation);
		dbSetup.launch();
	}

	@Test
	public void testInsertWithAProxy(){
		Category sfProxy = (Category) getSession().load(Category.class, "SF");
		
		assertThat(Hibernate.isInitialized(sfProxy)).isFalse(); //C'est un proxy non initialisé
		
		
		Book dune = new Book();
		dune.setTitle("Dune");
		dune.setCategory(sfProxy);
		
		getSession().saveOrUpdate(dune);
		
		assertThat(dune.getId()).isNotNull(); //A été persisté
		
		List<Book> sfBooks = getSession().createQuery("from Book b where b.category.code = :code").setParameter("code", "SF").list();
		
		assertThat(sfBooks).hasSize(1);
		assertThat(sfBooks).extracting("title").contains("Dune");
		
		assertThat(Hibernate.isInitialized(sfBooks.get(0).getCategory())).isFalse(); //Toujurs pas initialisé
	}
}
