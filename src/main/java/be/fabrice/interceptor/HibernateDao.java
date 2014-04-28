package be.fabrice.interceptor;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly=false, propagation=Propagation.REQUIRES_NEW)
public class HibernateDao extends HibernateDaoSupport implements Dao {
	@Override
	public void save(Person p) {
		getSession().saveOrUpdate(p);
	}
	
	@Override
	public Person find(Integer id) {
		return (Person) getSession().get(Person.class,id);
	}
	
	@Override
	public void delete(Person p) {
		getSession().delete(p);
	}
}
