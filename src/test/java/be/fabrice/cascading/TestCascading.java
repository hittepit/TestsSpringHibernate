package be.fabrice.cascading;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.assertj.core.api.Assertions.assertThat;
import be.fabrice.utils.TransactionalTestBase;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import org.assertj.core.api.Assertions;
import org.hibernate.SessionFactory;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = "classpath:cascading/test-cascading-spring.xml")
public class TestCascading extends TransactionalTestBase {
    @BeforeMethod
    public void initData() {
        Operation operations = sequenceOf(deleteAllFrom("DOG","cat", "MASTER"),
                insertInto("MASTER")
                        .columns("id", "name")
                        .values(100, "master1")
                        .build(),
                insertInto("DOG")
                        .columns("id", "name","master_fk")
                        .values(101, "dog1", 100)
                        .values(102, "dog2", 100)
                        .build(),
                insertInto("cat")
                        .columns("id","name","cat_master_fk")
                        .values(201, "cat1", "100")
                        .values(202, "cat2", 100)
                        .build()
        );
        DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), operations);
        dbSetup.launch();
    }
    
    @Test
    public void testAddDogNotSaved() {
        final Master master = ((Master) getSession().get(Master.class, 100));
        assertThat(master.getDogs()).hasSize(2);
        final Dog dog = new Dog();
        dog.setName("newDog");
        master.getDogs().add(dog);
        getSession().saveOrUpdate(master);
        assertThat(dog.getId()).isNull();
    }

    @Test
    public void testAddDogAndSaveIt() {
        final Master master = ((Master) getSession().get(Master.class, 100));
        assertThat(master.getDogs()).hasSize(2);
        final Dog dog = new Dog();
        dog.setName("newDog");
        master.getDogs().add(dog);
        getSession().saveOrUpdate(dog);
        assertThat(dog.getId()).isNotNull();
    }

    @Test
    public void testAddDogFlushAndSave() {
        final Master master = ((Master) getSession().get(Master.class, 100));
        assertThat(master.getDogs()).hasSize(2);
        final Dog dog = new Dog();
        dog.setName("newDog");
        master.getDogs().add(dog);
//        getSession().saveOrUpdate(master);
        getSession().flush();
        assertThat(dog.getId()).isNotNull();
    }

    @Test
    public void addCatNotSaved() {
        final Master master = ((Master) getSession().get(Master.class, 100));
        assertThat(master.getCats()).hasSize(2);
        final Cat cat = new Cat();
        cat.setName("newCat");
        cat.setMaster(master);
        master.getCats().add(cat);
        getSession().saveOrUpdate(master);
        assertThat(cat.getId()).isNull();
    }

    @Test
    public void addCatAndSaveit() {
        final Master master = ((Master) getSession().get(Master.class, 100));
        assertThat(master.getCats()).hasSize(2);
        final Cat cat = new Cat();
        cat.setName("newCat");
        cat.setMaster(master);
        master.getCats().add(cat);
        getSession().saveOrUpdate(cat);
        assertThat(cat.getId()).isNotNull();
    }
}
