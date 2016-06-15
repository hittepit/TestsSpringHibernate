package be.fabrice.distinct;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;

import be.fabrice.utils.TransactionalTestBase;

@ContextConfiguration(locations="classpath:distinct/test-distinct-spring.xml")
public class TestDistinctRequests extends TransactionalTestBase {

	@BeforeMethod
	public void initData(){
		Operation operations = sequenceOf(
				deleteAllFrom("LINE","INVOICE"),
				insertInto("INVOICE").columns("ID","CLIENT")
					.values(1001,"test1")
					.values(1002,"test2")
					.values(1003,"test3")
					.build(),
				insertInto("LINE").columns("ID","AMOUNT","INVOICE_FK")
					.values(101,10.0,1001)
					.values(102,15.0,1001)
					.values(103,20.0,1001)
					.values(104,5.0,1002)
					.values(105,9.0,1002)
					.values(106,1.0,1003)
					.values(107,2.0,1003)
					.build()
				);
		
		DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), operations);
		dbSetup.launch();
	}
	
	@Test
	public void theProblem(){
		List<Invoice> invoices = getSession().createQuery("select i from Invoice i left join i.lines as l where l.amount > :a")
				.setParameter("a", 10.0)
				.list();
		
		assertThat(invoices).hasSize(2); // has 2 invoices!
		
		assertThat(invoices).extracting("id").containsExactly(1001,1001); //but twice the same invoice... because of the join
	}
	
	@Test(description="distinct in hql")
	public void testDistinctInHql(){
		List<Invoice> invoices = getSession().createQuery("select distinct i from Invoice i left join i.lines as l where l.amount > :a")
				.setParameter("a", 10.0)
				.list();
		
		assertThat(invoices).hasSize(1);
		
		assertThat(invoices).extracting("id").contains(1001);
	}

	@Test(description="same problem with criteria")
	public void testProblemWithCriteria(){
		Criteria invoiceCriteria = getSession().createCriteria(Invoice.class);
		invoiceCriteria.createAlias("lines", "line");
		invoiceCriteria.add(Restrictions.gt("line.amount", 10.0));
		
		List<Invoice> invoices = invoiceCriteria.list();
		
		assertThat(invoices).hasSize(2); // has 2 invoices!
		
		assertThat(invoices).extracting("id").containsExactly(1001,1001); //but twice the same invoice... because of the join
	}

	@Test(description="distinct with criteria, easy version")
	public void testEasyDistinctWithCriteria(){
		Criteria invoiceCriteria = getSession().createCriteria(Invoice.class);
		invoiceCriteria.createAlias("lines", "line");
		invoiceCriteria.add(Restrictions.gt("line.amount", 10.0));

		invoiceCriteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		List<Invoice> invoices = invoiceCriteria.list();
		
		assertThat(invoices).hasSize(1);
		
		assertThat(invoices).extracting("id").contains(1001);
	}

	@Test(description="easy distinct with criteria does not work with pagination")
	public void testEasyDistinctWithCriteriaIncorrectPagination(){
		Criteria invoiceCriteria = getSession().createCriteria(Invoice.class);
		invoiceCriteria.createAlias("lines", "line");
		invoiceCriteria.add(Restrictions.gt("line.amount", 4.0));

		invoiceCriteria.addOrder(Order.asc("id")); //Garantee the order for pagination
		
		invoiceCriteria.setFirstResult(0);
		
		invoiceCriteria.setMaxResults(2);
		
		invoiceCriteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY); //Is applied on the resultSet, AFTER the request!
		
		List<Invoice> invoices = invoiceCriteria.list();
		
		assertThat(invoices).hasSize(1); //Should have 2!!
		
		assertThat(invoices).extracting("id").contains(1001);
	}

	@Test(description="distinct with criteria and pagination must pass by distinct ids, two requests")
	public void testDistinctWithCriteriaPaginationV1(){
		Criteria invoiceCriteria = getSession().createCriteria(Invoice.class);
		invoiceCriteria.createAlias("lines", "line");
		invoiceCriteria.add(Restrictions.gt("line.amount", 4.0));

		invoiceCriteria.addOrder(Order.asc("id")); //Garantee the order for pagination
		
		invoiceCriteria.setFirstResult(0);
		
		invoiceCriteria.setMaxResults(2);
		
		invoiceCriteria.setProjection(Projections.distinct(Projections.id()));
		
		List<Integer> invoiceIds = invoiceCriteria.list();
		
		List<Invoice> invoices = getSession().createQuery("from Invoice i where i.id in (:ids)")
				.setParameterList("ids", invoiceIds)
				.list();
		
		assertThat(invoices).hasSize(2); //Now it's correct
		
		assertThat(invoices).extracting("id").contains(1001, 1002);
	}

	@Test(description="distinct with criteria and pagination must pass by distinct ids, inner request")
	public void testDistinctWithCriteriaPaginationV2(){
		DetachedCriteria invoiceIdsCriteria = DetachedCriteria.forClass(Invoice.class);
		invoiceIdsCriteria.createAlias("lines", "line");
		invoiceIdsCriteria.add(Restrictions.gt("line.amount", 4.0));

		invoiceIdsCriteria.setProjection(Projections.distinct(Projections.id()));
		
		Criteria invoiceCriteria = getSession().createCriteria(Invoice.class);
		invoiceCriteria.add(Subqueries.propertyIn("id", invoiceIdsCriteria));
		invoiceCriteria.setFirstResult(0);
		invoiceCriteria.setMaxResults(2);
		invoiceCriteria.addOrder(Order.asc("id")); //Garantee order for pagination
		
		List<Invoice> invoices = invoiceCriteria.list();
		
		assertThat(invoices).hasSize(2); //Now it's correct
		
		assertThat(invoices).extracting("id").contains(1001, 1002);
	}
}
