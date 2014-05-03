package be.fabrice.optimistic.dao;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;

import be.fabrice.optimistic.entity.Boss;
import be.fabrice.optimistic.entity.Employee;

@Repository
@Transactional(readOnly=true)
public class HibernateDao extends HibernateDaoSupport implements Dao {

	@Override
	public Employee findEmployee(Long id) {
		return (Employee) getSession().get(Employee.class, id);
	}
	
	@Override
	public Boss findBoss(Long id) {
		return (Boss) getSession().get(Boss.class,id);
	}

	@Override
	@Transactional(readOnly=false)
	public void save(Object entity) {
		getSession().saveOrUpdate(entity);
	}
}
