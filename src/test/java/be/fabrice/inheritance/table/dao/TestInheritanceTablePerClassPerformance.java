package be.fabrice.inheritance.table.dao;

import javax.persistence.criteria.CriteriaBuilder;

import java.util.List;
import java.util.Random;

import static org.testng.Assert.assertEquals;
import be.fabrice.inheritance.table.entity.Employeur;
import be.fabrice.inheritance.table.entity.Boss;
import be.fabrice.inheritance.table.entity.Independant;
import be.fabrice.inheritance.table.entity.Societe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(suiteName="Héritage", testName="Une table par classe")
@ContextConfiguration("classpath:inheritance/table/test-inheritance-spring.xml")
public class TestInheritanceTablePerClassPerformance extends AbstractTransactionalTestNGSpringContextTests {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestInheritanceTablePerClassPerformance.class);

    @Autowired
    private Dao dao;

    private int bossNumber;
    private int societeNumber;
    private int independantNumber;

    @BeforeClass
    public void beforeMethod(){
        final long start = System.nanoTime();
        for(int i=0; i<10000; i++) {
            final int type = new Random().nextInt(3);
            if(type == 0) {
                final Societe societe = new Societe();
                societe.setNumeroEntreprise(Integer.toString(i));
                dao.save(societe);
                societeNumber++;
            } else if(type == 1){
                final Boss boss = new Boss();
                boss.setName("Name"+Integer.toString(i));
                dao.save(boss);
                bossNumber++;
            } else {
                final Independant independant = new Independant();
                independant.setOnss(Integer.toString(i));
                dao.save(independant);
                independantNumber++;
            }
        }
        final long time = (System.nanoTime() - start)/1000000;
        LOGGER.info("Temps d'insertion avec one table per class = {} ms", time);
        dao.clearSession();
    }

    @Test
    public void timeToFindAll() {
        final long start = System.nanoTime();
        final List<Employeur> employeurs = dao.findAllEmployeurs();
        Assert.assertEquals(employeurs.size(), 10000);
        final long time = (System.nanoTime() - start)/1000000;
        LOGGER.info("Temps de findAll avec one table per class = {} ms", time);
        dao.clearSession();
    }

    @Test
    public void timeToFindBosses() {
        final long start = System.nanoTime();
        final List<Boss> bosses = dao.findBosses();
        assertEquals(bosses.size(), bossNumber);
        final long time = (System.nanoTime() - start)/1000000;
        LOGGER.info("Temps de findBosses avec one table per class = {} ms", time);
        dao.clearSession();
    }

    @Test
    public void timeToFindSocietes() {
        final long start = System.nanoTime();
        final List<Societe> societes = dao.findSocietes();
        assertEquals(societes.size(), societeNumber);
        final long time = (System.nanoTime() - start)/1000000;
        LOGGER.info("Temps de findSocietes avec one table per class = {} ms", time);
        dao.clearSession();
    }

    @Test
    public void timeToFindIndependants() {
        final long start = System.nanoTime();
        final List<Independant> independants = dao.findIndependants();
        assertEquals(independants.size(), independantNumber);
        final long time = (System.nanoTime() - start)/1000000;
        LOGGER.info("Temps de findIndependants avec one table per class = {} ms", time);
        dao.clearSession();
    }
}
