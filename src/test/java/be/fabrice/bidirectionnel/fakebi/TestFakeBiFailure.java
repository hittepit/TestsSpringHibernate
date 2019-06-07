package be.fabrice.bidirectionnel.fakebi;

import be.fabrice.utils.TransactionalTestBase;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

import javax.sql.DataSource;

@ContextConfiguration(locations= "classpath:bidirectionnel/fakebi/test-bidirectionnel-spring-fakeBi.xml")
public class TestFakeBiFailure extends TransactionalTestBase {
    @Test(expectedExceptions = RuntimeException.class)
    public void testItDoesNotWork() {
        Element el = new Element();
        el.add(25L);

        getSession().save(el);
    }
}
