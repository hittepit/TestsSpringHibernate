package be.fabrice.comparehql;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.utils.TransactionalTestBase;
import be.fabrice.utils.logging.SimpleSql;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;

@ContextConfiguration(locations="classpath:comparehql/test-spring.xml")
public class TestHqlProjectionIssue extends TransactionalTestBase{

	private List<String> extractAliases(String sqlString, String aliasRoot){
		Set<String> aliases = new TreeSet<String>();
		Pattern p = Pattern.compile("("+aliasRoot+"\\d+_\\.)");
		Matcher matcher = p.matcher(sqlString);
		while(matcher.find()){
			aliases.add(matcher.group(1));
		}
		return new ArrayList<String>(aliases);
	}
	
	private List<String> extractSqlContaining(List<String> sqls, String match){
		List<String> s = new ArrayList<String>(SimpleSql.getSqlList());
		CollectionUtils.filter(
				s, new Predicate<String>() {
					public boolean evaluate(String sql) {
						return sql.contains("travailleu");
					};
				});
		return s;
	}
	
	@BeforeMethod
	public void initData(){
		Operation operations = sequenceOf(
				deleteAllFrom("tache","travailleur","bureau"),
				insertInto("bureau").columns("id","nom")
					.values(1,"bureau1")
					.values(2,"bureau2")
					.build(),
				insertInto("travailleur").columns("id","nom","bureau_ik")
					.values(1,"trav1 bureau1",1)
					.values(2,"trav2 bureau1",1)
					.values(3,"trav3 bureau2",2)
					.values(4,"trav4 bureau2",2)
					.build(),
				insertInto("tache").columns("id","nom","statut","trav_ik")
					.values(1,"tache1 trav 1","FINI",1)
					.values(2,"tache2 trav 1","ENCOURS",1)
					.values(3,"tache3 trav 3","ENCOURS",3)
					.values(4,"tache4 trav 4","ENCOURS",4)
					.build());
		
		DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), operations);
		dbSetup.launch();
		
		SimpleSql.reinitSqlList();
	}
	
	/**
	 * <p>Lorsque la projection est une entité obtenue par navigation dans le graphe d'objet depuis
	 * l'entité exprimée dans le From et que cette entité est utilisée directement ou indirectement
	 * dans le where, Hibernate génère des alias différents pour la projection et pour le where.</p>
	 * <p>A priori, les deux alias sont liés dans le sql généré, mais on a eu un cas (que je n'arrive pas
	 * à reproduire) où le sql générait une erreur sur DB2.</p>
	 */
	@Test
	public void twoAliasesWhenIndirectProjectionIsUseInWhere(){
		List<Travailleur> travs = getSession()
			.createQuery("select ta.travailleur from Tache ta "
					+ "where ta.statut = :statut and "
					+ "ta.travailleur.bureau.nom = :bName "
					+ "order by ta.travailleur.nom desc")
			.setParameter("statut", "ENCOURS")
			.setParameter("bName", "bureau2")
			.list();
		
		assertThat(travs).hasSize(2);
		assertThat(travs.get(0).getNom()).isEqualTo("trav4 bureau2");
		assertThat(travs.get(1).getNom()).isEqualTo("trav3 bureau2");
		
		//Cependant...
		List<String> sqlsWithTravailleur = extractSqlContaining(SimpleSql.getSqlList(), "travailleu");
		int nombreAliases = extractAliases(sqlsWithTravailleur.get(0), "travailleu").size();
		
		//La requête a généré deux alias différents pour travailleur
		assertThat(nombreAliases).isEqualTo(2);
	}
	
	@Test
	public void twoAliasesWhenIndirectProjectionIsUseInWhere2(){
		List<String> names = new ArrayList<String>();
		names.add("trav1");
		List<Travailleur> travs = getSession()
			.createQuery("select ta.travailleur from Tache ta "
					+ "where ta.statut = :statut and "
					+ "ta.travailleur.nom in (:names) "
					+ "order by ta.travailleur.nom desc")
			.setParameter("statut", "ENCOURS")
			.setParameterList("names", names)
			.list();
		
		assertThat(travs).isEmpty(); //La requête générée ne permet pas de retrouver le travailleur
		
		//Parce que...
		List<String> sqlsWithTravailleur = extractSqlContaining(SimpleSql.getSqlList(), "travailleu");
		int nombreAliases = extractAliases(sqlsWithTravailleur.get(0), "travailleu").size();
		
		//La requête a généré deux alias différents pour travailleur
		assertThat(nombreAliases).isEqualTo(2);
	}
	
	/**
	 * <p>Lorsque la projection est une entité obtenue par navigation dans le graphe d'objet depuis
	 * l'entité exprimée dans le From et que cette entité est utilisée directement ou indirectement
	 * dans le order by, Hibernate génère des alias différents pour la projection et pour le order by.</p>
	 * <p>A priori, les deux alias sont liés dans le sql généré, mais on a eu un cas (que je n'arrive pas
	 * à reproduire) où le sql générait une erreur sur DB2.</p>
	 */
	@Test
	public void twoAliasesWhenIndirectProjectionIsUseInOrder(){
		List<Travailleur> travs = getSession()
			.createQuery("select ta.travailleur from Tache ta "
					+ "where ta.statut = :statut "
					+ "order by ta.travailleur.nom desc")
			.setParameter("statut", "ENCOURS")
			.list();
		
		assertThat(travs).hasSize(3);
		assertThat(travs.get(0).getNom()).isEqualTo("trav4 bureau2");
		assertThat(travs.get(1).getNom()).isEqualTo("trav3 bureau2");
		assertThat(travs.get(2).getNom()).isEqualTo("trav1 bureau1");
		
		//Cependant...
		List<String> sqlsWithTravailleur = extractSqlContaining(SimpleSql.getSqlList(), "travailleu");
		int nombreAliases = extractAliases(sqlsWithTravailleur.get(0), "travailleu").size();
		
		//La requête a généré deux alias différents pour travailleur
		assertThat(nombreAliases).isEqualTo(2);
	}
	
	/**
	 * <p>Lorsque la projection est une entité obtenue par navigation dans le graphe d'objet depuis
	 * l'entité exprimée dans le From et que cette entité n'est pas utilisée, directement ou indirectement,
	 * dans le order by ou le where, Hibernate ne génère qu'un seul alias.</p>
	 */
	@Test
	public void oneAliasesWhenIndirectProjectionNotUsedAfter(){
		List<Travailleur> travs = getSession()
			.createQuery("select ta.travailleur from Tache ta "
					+ "where ta.statut = :statut ")
			.setParameter("statut", "ENCOURS")
			.list();
		
		assertThat(travs).hasSize(3);
		
		List<String> sqlsWithTravailleur = extractSqlContaining(SimpleSql.getSqlList(), "travailleu");
		int nombreAliases = extractAliases(sqlsWithTravailleur.get(0), "travailleu").size();
		
		//La requête a généré deux alias différents pour travailleur
		assertThat(nombreAliases).isEqualTo(1);
	}

	/**
	 * <p>Lorsque la projection est issue de join et donc qu'elle a un alias, cet alias est utilisé
	 * partout dans la requête si c'est lui qui est utilisé.</p>
	 */
	@Test
	public void oneAliasWhenProjectionIsAnAliasFromAJoin(){
		List<Travailleur> travs = getSession()
			.createQuery("select trav from Tache ta "
					+ "join ta.travailleur as trav "
					+ "where ta.statut = :statut "
					+ "and trav.bureau.nom = :bName order by trav.nom desc")
			.setParameter("statut", "ENCOURS")
			.setParameter("bName", "bureau2")
			.list();
		
		assertThat(travs).hasSize(2);
		assertThat(travs.get(0).getNom()).isEqualTo("trav4 bureau2");
		assertThat(travs.get(1).getNom()).isEqualTo("trav3 bureau2");
		
		List<String> sqlsWithTravailleur = extractSqlContaining(SimpleSql.getSqlList(), "travailleu");
		int nombreAliases = extractAliases(sqlsWithTravailleur.get(0), "travailleu").size();
		
		//La requête a généré un seul alias
		assertThat(nombreAliases).isEqualTo(1);
	}

	/**
	 * <p>Lorsque la projection est issue de join et donc qu'elle a un alias, mais que cet alias n'est 
	 * pas utilisé dans le reste de la requête, Hibernate génère deux alias.</p>
	 */
	@Test
	public void twoAliasesWhenProjectionIsAnAliasFromAJoinButObjectGraphIsUsedAfter(){
		List<Travailleur> travs = getSession()
			.createQuery("select trav from Tache ta "
					+ "join ta.travailleur as trav "
					+ "where ta.statut = :statut "
					+ "and ta.travailleur.bureau.nom = :bName order by ta.travailleur.nom desc")
			.setParameter("statut", "ENCOURS")
			.setParameter("bName", "bureau2")
			.list();
		
		assertThat(travs).hasSize(2);
		assertThat(travs.get(0).getNom()).isEqualTo("trav4 bureau2");
		assertThat(travs.get(1).getNom()).isEqualTo("trav3 bureau2");
		
		List<String> sqlsWithTravailleur = extractSqlContaining(SimpleSql.getSqlList(), "travailleu");
		int nombreAliases = extractAliases(sqlsWithTravailleur.get(0), "travailleu").size();
		
		//La requête a généré un seul alias
		assertThat(nombreAliases).isEqualTo(2);
	}
}
