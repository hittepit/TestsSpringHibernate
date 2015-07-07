package be.fabrice.fetch.lazy.oneToOne;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.assertj.core.api.Assertions.assertThat;

import org.hibernate.Hibernate;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;

import be.fabrice.utils.TransactionalTestBase;
import be.fabrice.utils.logging.SimpleSql;

@ContextConfiguration(locations="classpath:fetch/lazy/oneToOne/test-spring.xml")
public class TestLazyOneToOneBehaviour extends TransactionalTestBase{

	@BeforeMethod
	public void initData(){
		Operation operations = sequenceOf(
				deleteAllFrom("BMS","BSL", "MS", "SL"),
				insertInto("BSL").columns("id","name")
					.values(1,"slave1")
					.build(),
				insertInto("BMS").columns("id","name","sl_fk")
					.values(1,"master1",1)
					.build(),
				insertInto("SL").columns("id","name")
					.values(1,"slave1")
					.build(),
				insertInto("MS").columns("id","name","sl_fk")
					.values(1,"master1",1)
					.build());
		
		DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), operations);
		dbSetup.launch();
		
		SimpleSql.reinitSqlList();
	}
	
	@Test
	public void lazyOneToOneOnSlaveSideIsEagerlyFetched(){
		BadSlave slave= (BadSlave) getSession().get(BadSlave.class, 1);
		assertThat(Hibernate.isInitialized(slave.getMaster())).isTrue();
	}
	
	@Test
	public void lazyOneToOneOnSlaveResultsInInefficientSelectBecauseOtherSideIsEager(){
		BadSlave slave= (BadSlave) getSession().get(BadSlave.class, 1);
		assertThat(Hibernate.isInitialized(slave.getMaster())).isTrue();
		
		assertThat(SimpleSql.getSqlList()).hasSize(2);
		//Alors que la première requête récupère le BadSlave, la deuxième le récupère à nouveau
		assertThat(SimpleSql.contains("select .* from BMS .* join BSL .*")).isTrue();
	}
	
	@Test
	public void lazyOneToOneOnMasterIsReallyLazy(){
		Master master = (Master) getSession().get(Master.class, 1);
		
		assertThat(Hibernate.isInitialized(master.getSlave())).isFalse();
	}
	
	@Test 
	public void lazyOneToOneOnMasterSimplifiesRequest(){
		Slave slave= (Slave) getSession().get(Slave.class, 1);
		assertThat(Hibernate.isInitialized(slave.getMaster())).isTrue();
		
		//Même en lazy, le slave du master est initialisé
		assertThat(Hibernate.isInitialized(slave.getMaster().getSlave())).isTrue();
		
		//Heureusement
		assertThat(slave.getMaster().getSlave()).isSameAs(slave);
		
		assertThat(SimpleSql.getSqlList()).hasSize(2);
		//La première requête récupère le Slave, la deuxième récupère juste le Master
		assertThat(SimpleSql.contains("select .* from BMS .* join BSL .*")).isFalse();
	}
}
