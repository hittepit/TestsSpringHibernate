package be.fabrice.flush.dao;

import java.util.List;

import be.fabrice.flush.entity.Dummy;
import be.fabrice.flush.entity.Person;

public interface Dao {
	void save(Person p);
	List<Person> findByName(String name);
	Person find(Integer id);
	Dummy findDummy(Integer id);
	Person findById(Integer id);
}
