package be.fabrice.elementCollection;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.criterion.Restrictions;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;

import be.fabrice.utils.TransactionalTestBase;

@ContextConfiguration(locations="classpath:elementCollection/test-element-spring.xml")
public class TestElementCollectionBehaviour extends TransactionalTestBase{
	
	@BeforeMethod
	public void beforeTest(){
		Operation deletes = deleteAllFrom("O_LABEL", "LABEL", "TASK");
		Operation tasks = insertInto("TASK").columns("ID","NAME")
				.values(1000L,"task1")
				.values(1001L,"task2")
				.build();
		Operation labels = insertInto("LABEL").columns("task_id","name")
				.values(1000L,"label1")
				.values(1000L,"label2")
				.build();
		Operation orderedLabels = insertInto("O_LABEL").columns("task_id","name","label_index")
				.values(1000L,"label1",1)
				.values(1000L,"label2",0)
				.build();
		
		Operation operation = sequenceOf(deletes,tasks,labels,orderedLabels);
		
		DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), operation);
		dbSetup.launch();
	}

	@Test(description="labels must be lazyly loaded")
	public void testLabelsLoaded(){
		Task t = (Task) getSession().get(Task.class, 1000L);
		assertThat(Hibernate.isInitialized(t.getLabels())).isFalse();
		assertThat(t.getLabels()).hasSize(2);
		assertThat(t.getLabels()).contains("label1","label2");
	}
	
	/**
	 * As the list has no OrderColum, the while list is deleted and persisted again. A set would be
	 * more efficient.
	 */
	@Test(description="new labels are persisted without need of cascading")
	public void testAddNewLabels(){
		Task t = (Task) getSession().get(Task.class, 1000L);
		t.getLabels().add("new label");
		
		getSession().flush();
		
		getSession().clear();
		
		t = (Task) getSession().get(Task.class, 1000L);
		assertThat(t.getLabels()).hasSize(3);
	}
	
	@Test(description="removed labels are deleted")
	public void testRemoveLabels(){
		Task t = (Task) getSession().get(Task.class, 1000L);
		t.getLabels().remove(1);
		
		getSession().flush();
		
		getSession().clear();
		
		t = (Task) getSession().get(Task.class, 1000L);
		assertThat(t.getLabels()).hasSize(1);
	}

	@Test(description="labels must be lazyly loaded with order")
	public void testOrderedLabelsLoaded(){
		Task t = (Task) getSession().get(Task.class, 1000L);
		assertThat(Hibernate.isInitialized(t.getOrderedLabels())).isFalse();
		assertThat(t.getLabels()).hasSize(2);
		assertThat(t.getLabels()).containsExactly("label2","label1");
	}
	
	/**
	 * This list has a OrderColum, and this is more efficient when using a list.
	 */
	@Test(description="new labels are persisted without need of cascading")
	public void testAddNewOrderedLabels(){
		Task t = (Task) getSession().get(Task.class, 1000L);
		t.getOrderedLabels().add("new label");
		
		getSession().flush();
		
		getSession().clear();
		
		t = (Task) getSession().get(Task.class, 1000L);
		assertThat(t.getOrderedLabels()).hasSize(3);
	}
	
	@Test(description="removed ordered labels are deleted and resorted")
	public void testRemoveOrderedLabels(){
		Task t = (Task) getSession().get(Task.class, 1000L);
		t.getOrderedLabels().remove(0);
		
		getSession().flush();
		
		getSession().clear();
		
		t = (Task) getSession().get(Task.class, 1000L);
		assertThat(t.getOrderedLabels()).hasSize(1);
		assertThat(t.getOrderedLabels().get(0)).isEqualTo("label1");
	}
	
	@Test(description="find task containing one specific label")
	public void find_task_containing_one_label(){
		List<Task> tasks = 
//				getSession().createCriteria(Task.class)
//			.createAlias("orderedLabels", "l")
//			.add(Restrictions.eq("l.elements", "label1"))
//			.list();
//		
		getSession().createQuery("from Task t where :l in t.orderedLabels.elements").setParameter("l", "label1").list();
		
		assertThat(tasks).hasSize(1);
	}
}
