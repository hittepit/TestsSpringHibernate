package be.fabrice.simple.dao;

import java.util.List;

import be.fabrice.simple.entity.Person;

public interface PersonDao {
	Person find(Integer id);
	List<Person> findByLastname(String lastname);
	void save(Person person);
}
