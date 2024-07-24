package be.fabrice.inheritance.join.dao;

import java.util.List;
import java.util.Random;

import static org.testng.Assert.assertEquals;
import be.fabrice.inheritance.join.entity.Boss;
import be.fabrice.inheritance.join.entity.Employeur;
import be.fabrice.inheritance.join.entity.Societe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(suiteName="Héritage", testName="JOIN-PERF")
@ContextConfiguration("classpath:inheritance/join/test-inheritance-spring.xml")
public class TestJoinInheritancePerformance extends AbstractTransactionalTestNGSpringContextTests {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestJoinInheritancePerformance.class);

    @Autowired
    private Dao dao;

    private int bossNumber;
    private int societeNumber;

    @BeforeClass
    public void beforeMethod(){
        final long start = System.nanoTime();
        for(int i=0; i<10000; i++) {
            final boolean isSociete = new Random().nextBoolean();
            if(isSociete) {
                final Societe societe = new Societe();
                societe.setNumeroEntreprise(Integer.toString(i));
                dao.save(societe);
                societeNumber++;
            } else {
                final Boss boss = new Boss();
                boss.setName("Name"+Integer.toString(i));
                dao.save(boss);
                bossNumber++;
            }
        }
        final long time = (System.nanoTime() - start)/1000000;
        LOGGER.info("Temps d'insertion avec join = {} ms", time);
        dao.clear();
    }

    @Test
    public void timeToFindAll() {
        final long start = System.nanoTime();
        final List<Employeur> employeurs = dao.findAll();
        assertEquals(employeurs.size(), 10000);
        final long time = (System.nanoTime() - start)/1000000;
        LOGGER.info("Temps de findAll avec join = {} ms", time);
        dao.clear();
    }

    @Test
    public void timeToFindBosses() {
        final long start = System.nanoTime();
        final List<Boss> bosses = dao.findBosses();
        assertEquals(bosses.size(), bossNumber);
        final long time = (System.nanoTime() - start)/1000000;
        LOGGER.info("Temps de findBosses avec join = {} ms", time);
        dao.clear();
    }

    @Test
    public void timeToFindSocietes() {
        final long start = System.nanoTime();
        final List<Societe> societes = dao.findSocietes();
        assertEquals(societes.size(), societeNumber);
        final long time = (System.nanoTime() - start)/1000000;
        LOGGER.info("Temps de findSocietes avec join = {} ms", time);
        dao.clear();
    }
}
