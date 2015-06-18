package be.fabrice.comparehql.appi;

import java.util.ArrayList;
import java.util.List;

import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

import be.fabrice.utils.TransactionalTestBase;

@ContextConfiguration(locations="classpath:comparehql/appi/test-spring.xml")
public class TestHqProjectionIssueInAppi extends TransactionalTestBase {
	@Test
	public void orderby(){
		StringBuilder sb = new StringBuilder("SELECT distinct cont.categorie FROM Contrat as cont")
        .append(" WHERE cont.travailleur.relevePrestation.numeroDossier = :dossierId")
        .append(" AND cont.travailleur.relevePrestation.periode = :periode")
        .append(" AND cont.categorie.code IN (:listCategorieTravailleur)")
        .append(" ORDER BY cont.categorie.libelle");

		List<String> cats = new ArrayList<String>();
		cats.add("01");
		cats.add("02");
		
		getSession()
			.createQuery(sb.toString())
			.setParameter("dossierId", "123456")
			.setParameter("periode", "201501")
			.setParameter("listCategorieTravailleur", cats)
			.list();
	}
}
