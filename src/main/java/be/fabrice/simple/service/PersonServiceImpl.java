package be.fabrice.simple.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import be.fabrice.simple.dao.PersonDao;
import be.fabrice.simple.entity.Person;

@Service
@Transactional(readOnly=true)
public class PersonServiceImpl implements PersonService {

	@Autowired
	private PersonDao personDao;
	
	public Map<Character, Integer> countCharactersOccurenceInFirstnames(String lastname) {
		List<Person> persons = personDao.findByLastname(lastname);
		Map<Character, Integer> result = new HashMap<Character, Integer>();

		for(Person p:persons){
			String fn = p.getFirstname();
			for(int i=0;i<fn.length();i++){
				Character c = fn.charAt(i);
				int count = result.containsKey(c)?result.get(c):0;
				count++;
				result.put(c,count);
			}
		}
		return result;
	}

}
