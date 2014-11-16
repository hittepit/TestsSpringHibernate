package be.fabrice.simple.service;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.simple.dao.PersonDao;
import be.fabrice.simple.entity.Person;

/**
 * Un petit clin d'oeil aux développeurs qui pensent que si un bean est instancié par Spring
 * son test doit instancier un context de Spring. Ici, Spring n'est d'aucune aide.
 * @author fabrice.claes
 *
 */
public class TestPersonService {
	@Mock
	private PersonDao mockPersonDao;
	
	@InjectMocks private PersonService sut = new PersonServiceImpl();
	
	@BeforeMethod
	public void beforeMethod(){
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * <p>L'objet testé (sut, pour service under test) est instancié et ses dépendances injectées automatiquement
	 * avec l'annotation @InjectMocks. Aucun setter n'est nécessaire dans le service!</p>
	 * <p>Comme c'est du test unitaire, il ne doit pas dépendre de la qualité de sa dépendance (PersonDao). Il ne repose
	 * aucunement sur des données provenant de la DB. Le contrat du service est que, compte tenu d'une liste de personnes
	 * renvoyées par un Dao (peu importe où le Dao a été les chercher), il compte les occurences dans des caractères dans
	 * leurs prénoms</p>
	 * <p>Un mock du dao est donc créé et stubbé pour renvoyer une liste de personnes (il n'est même pas nécessaire qu'elles
	 * aient le même nom. Connaissant la liste, on vérifie que le résultat de la méthode est correct.</p>
	 * <p>Note: en général, la prépartion du test et des mock est faite dans un @BeforeMethod.</p>
	 */
	@Test
	public void testCount() {
		List<Person> persons = new ArrayList<Person>();
		Person p = new Person();
		p.setFirstname("azab");
		p.setLastname("aucune importance");
		persons.add(p);
		p = new Person();
		p.setFirstname("babar");
		p.setLastname("toujours aucune importance");
		persons.add(p);
		p = new Person();
		p.setFirstname("akab");
		p.setLastname("...");
		persons.add(p);
		
		when(mockPersonDao.findByLastname(anyString())).thenReturn(persons);
		
		
		//Le test proprement dit maintenant
		Map<Character,Integer> answer = sut.countCharactersOccurenceInFirstnames("pffff");

		assertEquals(answer.size(),5);
		assertEquals(answer.get('a'),Integer.valueOf(6));
		assertEquals(answer.get('z'),Integer.valueOf(1));
		assertEquals(answer.get('b'),Integer.valueOf(4));
		assertEquals(answer.get('r'),Integer.valueOf(1));
		assertEquals(answer.get('k'),Integer.valueOf(1));
		assertNull(answer.get('w'));
	}
}
