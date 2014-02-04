package be.fabrice.optimistic.dao;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;

import be.fabrice.optimistic.entity.Employee;

@Repository
@Transactional(readOnly=true)
public class HibernateDao extends HibernateDaoSupport implements Dao {

	@Override
	public Employee findEmployee(Long id) {
		return (Employee) getSession().get(Employee.class, id);
	}

	@Override
	@Transactional(readOnly=false)
	public void save(Employee employee) {
		getSession().saveOrUpdate(employee);
	}
}
