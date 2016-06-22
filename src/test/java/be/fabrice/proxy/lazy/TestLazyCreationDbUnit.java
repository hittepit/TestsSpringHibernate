package be.fabrice.proxy.lazy;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.utils.TransactionalTestBase;

@Test(suiteName="Fonctionnement des proxies", testName="Tests sur proxy (version dbUnit)")
@ContextConfiguration(locations="classpath:proxy/test-proxy-spring.xml")
public class TestLazyCreationDbUnit extends TransactionalTestBase {
	@Autowired
	private DataSource dataSource;
	
	@BeforeMethod
	public void beforeTest() throws DatabaseUnitException, SQLException, FileNotFoundException{
		DatabaseConnection con = new DatabaseConnection(dataSource.getConnection());
		
		DatabaseOperation.CLEAN_INSERT.execute(con, 
				new XmlDataSet(
						ClassLoader.getSystemResourceAsStream("proxy/dbunit-setup.xml")));
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
