package be.fabrice.flush.dao;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import be.fabrice.flush.entity.Dummy;
import be.fabrice.flush.entity.Person;

@Repository
@Transactional(readOnly=true)
public class HibernateDao extends HibernateDaoSupport implements Dao{

	@Transactional(readOnly=false)
	public void save(Person p) {
		getSession().saveOrUpdate(p);
	}

	public List<Person> findByName(String name) {
		return getSession().createQuery("from Person p where p.name=:name").setString("name", name).list();
	}

	public Person find(Integer id) {
		return (Person)getSession().get(Person.class,id);
	}
	public Dummy findDummy(Integer id){
		return (Dummy)getSession().get(Dummy.class,id);
	}
	public Person findById(Integer id){
		return (Person) getSession().createQuery("from Person p where p.id=:id").setInteger("id", id).uniqueResult();
	}
}
