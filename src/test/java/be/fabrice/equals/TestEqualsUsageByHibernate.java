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
		Operation operations = sequenceOf(deleteAllFrom("MASTEREAGER","MASTERLAZY","SIMPLEENTITY","MASTERLIST","MASTERSET","IDC"),
				insertInto("IDC")
					.columns("key","value","name")
					.values(1,"TEST","Test1")
					.values(2,"TEST","Test2")
					.build(),
				insertInto("MASTERLIST")
					.columns("ID","NAME")
					.values(200,"ML1")
					.build(),
				insertInto("MASTERSET")
					.columns("ID","NAME")
					.values(200,"MS1")
					.build(),
				insertInto("SIMPLEENTITY")
					.columns("ID","NAME","VALUE","ML_FK","MS_FK")
					.values(1000,"Entity1000", 1, 200, 200)
					.values(1001,"Entity1001", 2, 200, 200)
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
		HashcodeCounter.reinit();
	}
	
	//Pas utilisé pour le dirty checking
	@Test
	public void equalsNotUsedForDirtyChecking(){
		SimpleEntity s = (SimpleEntity) getSession().get(SimpleEntity.class, 1000);
		s.setName("test");
		getSession().flush(); // Flush, dirty checking and save
		
		assertThat(HashcodeCounter.get(SimpleEntity.class)).isEqualTo(0); //Pas d'utilisation de equals pour le dirty checking
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
		
		assertThat(HashcodeCounter.get(SimpleEntity.class)).isEqualTo(0); //Pas d'utilisation de equals pour le dirty checking
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

		assertThat(HashcodeCounter.get(SimpleEntity.class)).isEqualTo(0); //Pas d'utilisation de equals lors d'un get
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

		assertThat(HashcodeCounter.get(SimpleEntity.class)).isEqualTo(0); //Pas d'utilisation de equals lors d'un get
		assertThat(EqualsCounter.get(SimpleEntity.class)).isEqualTo(0); //Pas d'utilisation de equals lors d'un get
		assertThat(s1).isSameAs(s2);
	}
	
	//pas utilisé pour le dirty checking d'une relation eager
	@Test
	public void notUsedForDirtyCheckedEagerManyToOne(){
		MasterEager me = (MasterEager) getSession().get(MasterEager.class, 100);
		
		assertThat(Hibernate.isInitialized(me.getSimpleEntity())).isTrue(); //Eager
		assertThat(HashcodeCounter.get(SimpleEntity.class)).isEqualTo(0); //Pas d'utilisation de equals lors d'un get
		assertThat(HashcodeCounter.get(MasterEager.class)).isEqualTo(0); //Pas d'utilisation de equals lors d'un get
		assertThat(EqualsCounter.get(SimpleEntity.class)).isEqualTo(0); //Pas d'utilisation de equals lors d'un get
		assertThat(EqualsCounter.get(MasterEager.class)).isEqualTo(0); //Pas d'utilisation de equals lors d'un get
		
		me.setName("other");
		getSession().flush();
		assertThat(HashcodeCounter.get(SimpleEntity.class)).isEqualTo(0); //Pas d'utilisation de equals lors d'un dirty checking
		assertThat(HashcodeCounter.get(MasterEager.class)).isEqualTo(0); //Pas d'utilisation de equals lors d'un dirty checking
		assertThat(EqualsCounter.get(SimpleEntity.class)).isEqualTo(0); //Pas d'utilisation de equals lors d'un dirty checking
		assertThat(EqualsCounter.get(MasterEager.class)).isEqualTo(0); //Pas d'utilisation de equals lors d'un dirty checking
		
		SimpleEntity simpleEntity = (SimpleEntity) getSession().get(SimpleEntity.class,1001);
		me.setSimpleEntity(simpleEntity);
		getSession().flush();
		assertThat(HashcodeCounter.get(SimpleEntity.class)).isEqualTo(0); //Pas d'utilisation de equals lors d'un dirty checking
		assertThat(HashcodeCounter.get(MasterEager.class)).isEqualTo(0); //Pas d'utilisation de equals lors d'un dirty checking
		assertThat(EqualsCounter.get(SimpleEntity.class)).isEqualTo(0); //Pas d'utilisation de equals lors d'un dirty checking
		assertThat(EqualsCounter.get(MasterEager.class)).isEqualTo(0); //Pas d'utilisation de equals lors d'un dirty checking
	}
	
	//pas utilisé pour le dirty checking d'une relation lazy
	@Test
	public void notUsedForDirtyCheckedLazyManyToOne(){
		MasterLazy me = (MasterLazy) getSession().get(MasterLazy.class, 100);
		
		assertThat(Hibernate.isInitialized(me.getSimpleEntity())).isFalse(); //Lazy
		assertThat(HashcodeCounter.get(SimpleEntity.class)).isEqualTo(0); //Pas d'utilisation de equals lors d'un get
		assertThat(HashcodeCounter.get(MasterLazy.class)).isEqualTo(0); //Pas d'utilisation de equals lors d'un get
		assertThat(EqualsCounter.get(SimpleEntity.class)).isEqualTo(0); //Pas d'utilisation de equals lors d'un get
		assertThat(EqualsCounter.get(MasterLazy.class)).isEqualTo(0); //Pas d'utilisation de equals lors d'un get

		Hibernate.initialize(me.getSimpleEntity());
		assertThat(HashcodeCounter.get(SimpleEntity.class)).isEqualTo(0); //Pas d'utilisation de equals lors de l'initialisation
		assertThat(HashcodeCounter.get(MasterLazy.class)).isEqualTo(0); //Pas d'utilisation de equals lors de l'initialisation
		assertThat(EqualsCounter.get(SimpleEntity.class)).isEqualTo(0); //Pas d'utilisation de equals lors de l'initialisation
		assertThat(EqualsCounter.get(MasterLazy.class)).isEqualTo(0); //Pas d'utilisation de equals lors de l'initialisation
	}
	
	//pas utilisé pour le chargement une relation de type liste
	@Test
	public void notUsedByGetOnList(){
		MasterList ml = (MasterList) getSession().get(MasterList.class,200);
		assertThat(Hibernate.isInitialized(ml.getSimpleEntities())).isFalse(); //Lazy
		
		assertThat(HashcodeCounter.get(SimpleEntity.class)).isEqualTo(0); //Pas d'utilisation de equals lors d'un get
		assertThat(HashcodeCounter.get(MasterList.class)).isEqualTo(0); //Pas d'utilisation de equals lors d'un get
		assertThat(EqualsCounter.get(SimpleEntity.class)).isEqualTo(0); //Pas d'utilisation de equals lors d'un get
		assertThat(EqualsCounter.get(MasterList.class)).isEqualTo(0); //Pas d'utilisation de equals lors d'un get

		Hibernate.initialize(ml.getSimpleEntities());
		assertThat(HashcodeCounter.get(SimpleEntity.class)).isEqualTo(0); //Pas d'utilisation de equals lors de l'initialisation
		assertThat(HashcodeCounter.get(MasterList.class)).isEqualTo(0); //Pas d'utilisation de equals lors de l'initialisation
		assertThat(EqualsCounter.get(SimpleEntity.class)).isEqualTo(0); //Pas d'utilisation de equals lors de l'initialisation
		assertThat(EqualsCounter.get(MasterList.class)).isEqualTo(0); //Pas d'utilisation de equals lors de l'initialisation
	}
	
	//Hashcode Utilisé pour le chargement d'une relation de type set, mais pas à cause d'Hibernate
	@Test
	public void usedBySetButNotBecauseOfHibernate(){
		MasterSet ms = (MasterSet) getSession().get(MasterSet.class,200);
		assertThat(Hibernate.isInitialized(ms.getSimpleEntities())).isFalse(); //Lazy
		
		assertThat(HashcodeCounter.get(SimpleEntity.class)).isEqualTo(0); //Pas d'utilisation de equals lors d'un get
		assertThat(HashcodeCounter.get(MasterSet.class)).isEqualTo(0); //Pas d'utilisation de equals lors d'un get
		assertThat(EqualsCounter.get(SimpleEntity.class)).isEqualTo(0); //Pas d'utilisation de equals lors d'un get
		assertThat(EqualsCounter.get(MasterSet.class)).isEqualTo(0); //Pas d'utilisation de equals lors d'un get

		Hibernate.initialize(ms.getSimpleEntities());
		assertThat(ms.getSimpleEntities()).hasSize(2);
		assertThat(HashcodeCounter.get(SimpleEntity.class)).isGreaterThan(0); //Utilisation de hashcode lors de l'initialisation, par le set
		assertThat(HashcodeCounter.get(MasterSet.class)).isEqualTo(0); //Pas d'utilisation de equals lors de l'initialisation
		assertThat(EqualsCounter.get(SimpleEntity.class)).isEqualTo(0); //Pas d'utilisation de equals lors de l'initialisation
		assertThat(EqualsCounter.get(MasterSet.class)).isEqualTo(0); //Pas d'utilisation de equals lors de l'initialisation
	}
	
	//Pas Utilisé par hibernate pour un get avec clé composite
	@Test
	public void notUsedByGetWithIdClass(){
		EntityWithIdClass entity1 = (EntityWithIdClass) getSession().get(EntityWithIdClass.class, new IdPk(1, "TEST"));
		assertThat(entity1).isNotNull();
		EntityWithIdClass entity1Bis = (EntityWithIdClass) getSession().get(EntityWithIdClass.class, new IdPk(1, "TEST"));
		assertThat(entity1Bis).isSameAs(entity1);
		EntityWithIdClass entity2 = (EntityWithIdClass) getSession().get(EntityWithIdClass.class, new IdPk(2, "TEST"));
		
		assertThat(HashcodeCounter.get(EntityWithIdClass.class)).isEqualTo(0);
		assertThat(HashcodeCounter.get(IdPk.class)).isEqualTo(0);
		assertThat(EqualsCounter.get(EntityWithIdClass.class)).isEqualTo(0);
		assertThat(EqualsCounter.get(IdPk.class)).isEqualTo(0);
	}
	
	//Pas utilisé par hibernate sur une clé multiple pour un find
	@Test
	public void notUsedByFindWithIdClass(){
		EntityWithIdClass entity1 = (EntityWithIdClass) getSession().createQuery("from EntityWithIdClass e where e.key=:key and e.value=:value")
				.setParameter("key",1)
				.setParameter("value","TEST")
				.uniqueResult();
		assertThat(entity1).isNotNull();
		EntityWithIdClass entity1Bis = (EntityWithIdClass) getSession().createQuery("from EntityWithIdClass e where e.key=:key and e.value=:value")
				.setParameter("key",1)
				.setParameter("value","TEST")
				.uniqueResult();
		assertThat(entity1Bis).isSameAs(entity1);
		EntityWithIdClass entity2 = (EntityWithIdClass) getSession().createQuery("from EntityWithIdClass e where e.key=:key and e.value=:value")
				.setParameter("key",2)
				.setParameter("value","TEST")
				.uniqueResult();
		
		assertThat(HashcodeCounter.get(EntityWithIdClass.class)).isEqualTo(0);
		assertThat(HashcodeCounter.get(IdPk.class)).isEqualTo(0);
		assertThat(EqualsCounter.get(EntityWithIdClass.class)).isEqualTo(0);
		assertThat(EqualsCounter.get(IdPk.class)).isEqualTo(0);
	}
	//Embedded id
	//Eventuellement utilisé indirectement par HIbernate dans un usertype au travers de la méthode equals pour le dirtycheking
	//démo d'un mauvais equals portant sur un id et mis dans un set
}
