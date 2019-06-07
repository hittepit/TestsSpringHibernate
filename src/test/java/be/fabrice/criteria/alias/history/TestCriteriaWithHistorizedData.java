package be.fabrice.criteria.alias.history;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.assertj.core.api.Assertions.assertThat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.JoinType;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;

import be.fabrice.utils.TransactionalTestBase;

@ContextConfiguration(locations="classpath:criteria/history/test-application-context.xml")
public class TestCriteriaWithHistorizedData extends TransactionalTestBase{
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	@BeforeMethod
	public void initData() throws ParseException{
		deleteAllFrom("contact","worker","boss");
		Operation operation = sequenceOf(
			insertInto("boss")
				.columns("id","name")
				.values(100L,"boss1")
				.values(101L,"boss2")
				.build(),
			insertInto("worker")
				.columns("id","name","boss_fk")
				.values(1000L,"worker1",100L)
				.values(1001L,"worker2",100L)
				.values(1002L,"worker3",101L)
				.build(),
			insertInto("contact")
				.columns("id","email","validityDate","worker_fk")
				.values(10L,"worker1a@work.com",sdf.parse("1/01/2017"),1000L)
				.values(11L,"worker1b@work.com",sdf.parse("19/01/2017"),1000L)
				.values(12L,"worker1c@work.com",new Date(),1000L)
				.values(13L,"worker3@work.com",sdf.parse("01/01/1999"),1002L)
				.build()
				);
		DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), operation);
		dbSetup.launch();
	}
	
	@Test
	public void getLastHistorizedValue(){
		Criteria workerCriteria = getSession().createCriteria(Worker.class)
				.add(Property.forName("boss.id").eq(100L));
		Criteria contactCriteria = workerCriteria.createAlias("contactHistory", "c", CriteriaSpecification.LEFT_JOIN);
		
		//Retrouve la dernière valeur valide en tenant compte du fait qu'elle peut ne pas exister
		contactCriteria.add(Restrictions.or(
				Subqueries.notExists(DetachedCriteria.forClass(Contact.class)
						.add(Property.forName("worker").eqProperty("c.worker"))
						.setProjection(Projections.id())),
				Property.forName("c.validityDate").eq(
						DetachedCriteria.forClass(Contact.class)
							.add(Property.forName("worker").eqProperty("c.worker"))
							.setProjection(Projections.max("validityDate")))));
		
		workerCriteria.setProjection(Projections.projectionList()
					.add(Projections.property("name").as("name"))
					.add(Projections.property("c.email").as("email")))
				.setResultTransformer(Transformers.aliasToBean(WorkerDto.class));
		
		List<WorkerDto> results = workerCriteria.list();
		assertThat(results).hasSize(2);
	}

}
