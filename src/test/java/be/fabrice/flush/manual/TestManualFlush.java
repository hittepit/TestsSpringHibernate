package be.fabrice.flush.manual;

import javax.sql.DataSource;

import java.util.List;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.assertj.core.api.Assertions.assertThat;
import be.fabrice.flush.entity.Person;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import org.assertj.core.api.Assertions;
import org.hibernate.FlushMode;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(suiteName="Flush", testName="Flush in FlushMode.MANUAL or readonly")
@ContextConfiguration("classpath:flush/test-flush-spring.xml")
public class TestManualFlush extends AbstractTransactionalTestNGSpringContextTests {
    @Autowired
    private DataSource dataSource;
    @Autowired
    private SessionFactory sessionFactory;

    @BeforeMethod
    public void init() {
        Operation operations = sequenceOf(
                deleteAllFrom("PERSON"),
                insertInto("PERSON").columns("ID","NOM")
                        .values(1000,"toto")
                        .build()
        );

        DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), operations);
        dbSetup.launch();
    }

    @Test
    public void testReadOnlyObjectAreNotChanged() {
        Person p = (Person) sessionFactory.getCurrentSession().createQuery("from Person p where p.name=:name")
                .setParameter("name", "toto")
                .setReadOnly(true)
                .uniqueResult();

        p.setName("changed");

        sessionFactory.getCurrentSession().flush();

        Person p2 = (Person) sessionFactory.getCurrentSession().createQuery("from Person p where p.name=:name")
                .setParameter("name", "changed")
                .setReadOnly(true)
                .uniqueResult();

        assertThat(p2).describedAs("Object not updated").isNull();
    }

    @Test
    public void testFlushManualOnQueryDoesNotFlush() {
        Person p = (Person) sessionFactory.getCurrentSession().createQuery("from Person p where p.name=:name")
                .setParameter("name", "toto")
                .uniqueResult();

        p.setName("changed");

        Person p2 = (Person) sessionFactory.getCurrentSession().createQuery("from Person p where p.name=:name")
                .setParameter("name", "changed")
                .setFlushMode(FlushMode.MANUAL)
                .uniqueResult();

        Person p3 = (Person) sessionFactory.getCurrentSession().createQuery("from Person p where p.name=:name")
                .setParameter("name", "toto")
                .setFlushMode(FlushMode.MANUAL)
                .uniqueResult();

        assertThat(p2).describedAs("Data was not updated").isNull();
        assertThat(p3).describedAs("Data was not updated").isNotNull();
    }

    @Test
    public void testQueryDoesFlush() {
        Person p = (Person) sessionFactory.getCurrentSession().createQuery("from Person p where p.name=:name")
                .setParameter("name", "toto")
                .uniqueResult();

        p.setName("changed");

        Person p2 = (Person) sessionFactory.getCurrentSession().createQuery("from Person p where p.name=:name")
                .setParameter("name", "changed")
                .uniqueResult();

        Person p3 = (Person) sessionFactory.getCurrentSession().createQuery("from Person p where p.name=:name")
                .setParameter("name", "toto")
                .uniqueResult();

        assertThat(p2).describedAs("Data was updated").isNotNull();
        assertThat(p3).describedAs("Data was updated").isNull();
    }

    @Test
    public void testEntityWithReadonlyHintAreNotFlush() {
        Person p = (Person) sessionFactory.getCurrentSession().createQuery("from Person p where p.name=:name")
                .setReadOnly(true) //setHint(QueryHints.READ_ONLY, true) with entityManager
                .setParameter("name", "toto")
                .uniqueResult();

        p.setName("changed");

        Person p2 = (Person) sessionFactory.getCurrentSession().createQuery("from Person p where p.name=:name")
                .setParameter("name", "changed")
                .uniqueResult();

        Person p3 = (Person) sessionFactory.getCurrentSession().createQuery("from Person p where p.name=:name")
                .setParameter("name", "toto")
                .uniqueResult();

        assertThat(p2).describedAs("Data was not updated").isNull();
        assertThat(p3).describedAs("Data was not updated").isNotNull();
    }

}
