package be.fabrice.simple.dao;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import be.fabrice.simple.entity.Person;

@Repository
@Transactional(readOnly = true)
public class PersonHibernateDao extends HibernateDaoSupport implements PersonDao {

	public Person find(Integer id) {
		return (Person) getSession().get(Person.class, id);
	}

	public List<Person> findByLastname(String lastname) {
		return getSession().createQuery("from Person p where p.lastname = :l")
			.setString("l", lastname).list();
	}

	@Transactional(readOnly = false)
	public void save(Person person) {
		getSession().saveOrUpdate(person);
	}

}
