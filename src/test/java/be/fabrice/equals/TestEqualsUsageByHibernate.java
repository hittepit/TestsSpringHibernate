package be.fabrice.equals;

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

@ContextConfiguration(locations="classpath:equals/test-spring.xml")
public class TestEqualsUsageByHibernate extends TransactionalTestBase{

	@BeforeMethod
	public void initData(){
		Operation operations = sequenceOf(deleteAllFrom("MASTEREAGER","MASTERLAZY","SIMPLEENTITY"),
				insertInto("SIMPLEENTITY")
					.columns("ID","NAME","VALUE")
					.values(1000,"Entity1000", 1)
					.values(1001,"Entity1001", 2)
					.build(),
				insertInto("MASTEREAGER")
					.columns("ID","NAME","S_IK")
					.values(100,"ME1",1000)
					.build(),
				insertInto("MASTERLAZY")
					.columns("ID","NAME","S_IK")
					.values(100,"ME1",1000)
					.build()
				);
		
		DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), operations);
		dbSetup.launch();
		EqualsCounter.reinit();
	}
	
	//Pas utilisé pour le dirty checking
	@Test
	public void equalsNotUsedForDirtyChecking(){
		SimpleEntity s = (SimpleEntity) getSession().get(SimpleEntity.class, 1000);
		s.setName("test");
		getSession().flush(); // Flush, dirty checking and save
		
		assertThat(EqualsCounter.get(SimpleEntity.class)).isEqualTo(0); //Pas d'utilisation de equals pour le dirty checking
		
		SimpleEntity s2 = (SimpleEntity) getSession()
				.createQuery("from SimpleEntity s where s.name=:name")
				.setParameter("name", "test")
				.uniqueResult();
		
		assertThat(s2).isNotNull(); //La preuve qu'elle a bien été sauvée malgré tout
	}
	
	//Heureusement d'ailleurs
	@Test
	public void gladThatEqualsIsNotUsedForDirtyChecking(){
		SimpleEntity s = (SimpleEntity) getSession().get(SimpleEntity.class, 1000);
		s.setValue(17);
		getSession().flush(); // Flush, dirty checking and save
		
		assertThat(EqualsCounter.get(SimpleEntity.class)).isEqualTo(0); //Pas d'utilisation de equals pour le dirty checking
		
		SimpleEntity s2 = (SimpleEntity) getSession()
				.createQuery("from SimpleEntity s where s.value=:value")
				.setParameter("value", 17)
				.uniqueResult();
		
		//Si le equals avait utilisé par le dirty checking, elle n'aurait pas été persistée
		assertThat(s2).isNotNull(); //La preuve qu'elle a bien été sauvée malgré tout
	}
	
	//Pas utilisé par le get (pour voir si déjà en session par exemple)
	@Test
	public void equalsIsNotUsedWithTheGet(){
		SimpleEntity s1 = (SimpleEntity) getSession().get(SimpleEntity.class, 1000);
		SimpleEntity s2 = (SimpleEntity) getSession().get(SimpleEntity.class, 1000);

		assertThat(EqualsCounter.get(SimpleEntity.class)).isEqualTo(0); //Pas d'utilisation de equals lors d'un get
		assertThat(s1).isSameAs(s2);
	}
	
	
	//pas utilisé par les query (pour voir si déjà en session)
	@Test
	public void equalsIsNotUsedWithAQuery(){
		SimpleEntity s1 = (SimpleEntity) getSession().get(SimpleEntity.class, 1000);
		SimpleEntity s2 = (SimpleEntity) getSession()
				.createQuery("from SimpleEntity s where s.name=:name")
				.setParameter("name", "Entity1000")
				.uniqueResult();

		assertThat(EqualsCounter.get(SimpleEntity.class)).isEqualTo(0); //Pas d'utilisation de equals lors d'un get
		assertThat(s1).isSameAs(s2);
	}
	
	//pas utilisé pour le dirty checking d'une relation eager
	@Test
	public void notUsedForDirtyCheckedEagerManyToOne(){
		MasterEager me = (MasterEager) getSession().get(MasterEager.class, 100);
		
		assertThat(Hibernate.isInitialized(me.getSimpleEntity())).isTrue(); //Eager
		assertThat(EqualsCounter.get(SimpleEntity.class)).isEqualTo(0); //Pas d'utilisation de equals lors d'un get
		assertThat(EqualsCounter.get(MasterEager.class)).isEqualTo(0); //Pas d'utilisation de equals lors d'un get
		
		me.setName("other");
		getSession().flush();
		assertThat(EqualsCounter.get(SimpleEntity.class)).isEqualTo(0); //Pas d'utilisation de equals lors d'un dirty checking
		assertThat(EqualsCounter.get(MasterEager.class)).isEqualTo(0); //Pas d'utilisation de equals lors d'un dirty checking
		
		SimpleEntity simpleEntity = (SimpleEntity) getSession().get(SimpleEntity.class,1001);
		me.setSimpleEntity(simpleEntity);
		getSession().flush();
		assertThat(EqualsCounter.get(SimpleEntity.class)).isEqualTo(0); //Pas d'utilisation de equals lors d'un dirty checking
		assertThat(EqualsCounter.get(MasterEager.class)).isEqualTo(0); //Pas d'utilisation de equals lors d'un dirty checking
	}
	
	//pas utilisé pour le dirty checking d'une relation lazy
	@Test
	public void notUsedForDirtyCheckedLazyManyToOne(){
		MasterLazy me = (MasterLazy) getSession().get(MasterLazy.class, 100);
		
		assertThat(Hibernate.isInitialized(me.getSimpleEntity())).isFalse(); //Lazy
		assertThat(EqualsCounter.get(SimpleEntity.class)).isEqualTo(0); //Pas d'utilisation de equals lors d'un get
		assertThat(EqualsCounter.get(MasterEager.class)).isEqualTo(0); //Pas d'utilisation de equals lors d'un get

		Hibernate.initialize(me.getSimpleEntity());
		assertThat(EqualsCounter.get(SimpleEntity.class)).isEqualTo(0); //Pas d'utilisation de equals lors de l'initialisation
		assertThat(EqualsCounter.get(MasterEager.class)).isEqualTo(0); //Pas d'utilisation de equals lors de l'initialisation
	}
	
	//pas utilisé pour le chargement une relation de type liste
	//Utilisé pour le chargement d'une relation de type set, mais pas à cause d'Hibernate
	//Utilisé par hibernate sur une clé multiple pour un get
	//Utilisé par hibernate sur une clé multiple pour un find
	//Eventuellement utilisé indirectement par HIbernate dans un usertype au travers de la méthode equals pour le dirtycheking
	//démo d'un mauvais equals portant sur un id et mis dans un set
}
