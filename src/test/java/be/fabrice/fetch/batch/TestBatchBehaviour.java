package be.fabrice.fetch.batch;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.utils.TransactionalTestBase;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Insert.Builder;
import com.ninja_squad.dbsetup.operation.Operation;

@ContextConfiguration(locations="classpath:fetch/batch/test-spring.xml")
public class TestBatchBehaviour extends TransactionalTestBase{
	@BeforeClass
	public void initDatabase(){
		Builder insertGroupsBuilder = insertInto("GROUPE").columns("ID","NAME");
		Builder insertNoBatchBuilder = insertInto("NB").columns("ID","NAME","GROUP_ID");
		Builder insertBatchBuilder = insertInto("B").columns("ID","NAME","GROUP_ID");
		for(int i = 1;i<=10;i++){
			insertGroupsBuilder = insertGroupsBuilder.values(i,"Group"+i);
			for(int j=1;j<=3;j++){
				insertNoBatchBuilder = insertNoBatchBuilder.values(i*10+j,"NoBa"+(i*10+j),i);
				insertBatchBuilder = insertBatchBuilder.values(i*100+j,"Ba"+(i*10+j),i);
			}
		}
		
		Operation operation = sequenceOf(insertGroupsBuilder.build(),
				insertBatchBuilder.build(),
				insertNoBatchBuilder.build());
		DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), operation);
        dbSetup.launch();
	}
	
	@BeforeMethod
	public void cleanSession(){
		getSession().clear();
	}
	
	@Test
	public void normalLazyLoadingMustLoadCollectionsOneByOne(){
		List<Groupe> groupes = getSession().createQuery("from Groupe").list();
		assertEquals(groupes.size(), 10, "All groups must be present");
		
		int numberOfInit = 0;
		for(Groupe g:groupes){
			if(Hibernate.isInitialized(g.getNoBatchs())) numberOfInit++;
		}
		assertEquals(numberOfInit, 0, "Pas de collection initialisée");
		
		groupes.get(0).getNoBatchs().isEmpty(); //Initialize
		
		assertTrue(Hibernate.isInitialized(groupes.get(0).getNoBatchs()),"That Lazy collections was initialized");
		numberOfInit = 0;
		for(Groupe g:groupes){
			if(Hibernate.isInitialized(g.getNoBatchs())) numberOfInit++;
		}
		assertEquals(numberOfInit, 1, "Une seule collection initialisée");
	}
	
	@Test
	public void batchLazyLoadingMustLoadCollectionsBySerie(){
		List<Groupe> groupes = getSession().createQuery("from Groupe").list();
		assertEquals(groupes.size(), 10, "All groups must be present");
		
		int numberOfInit = 0;
		for(Groupe g:groupes){
			if(Hibernate.isInitialized(g.getBatchs())) numberOfInit++;
		}
		assertEquals(numberOfInit, 0, "Pas de collection initialisée");
		
		groupes.get(0).getBatchs().isEmpty(); //Initialize
		
		numberOfInit = 0;
		for(Groupe g:groupes){
			if(Hibernate.isInitialized(g.getBatchs())) numberOfInit++;
		}
		assertEquals(numberOfInit, 3, "Batch size collections initialisées");
	}
	
	@AfterClass
	public void clearDatabase(){
		deleteAllFrom("GROUPE","NB","B");
	}
}
