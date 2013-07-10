package be.fabrice.simple.dao;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import be.fabrice.simple.entity.Person;

@Repository
@Transactional(readOnly=true)
public class PersonHibernateDao extends HibernateDaoSupport implements
		PersonDao {

	public Person find(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Person> findByName(String lastname) {
		// TODO Auto-generated method stub
		return null;
	}

	@Transactional(readOnly=false)
	public void save(Person person) {
		// TODO Auto-generated method stub

	}

}
