package be.fabrice.bidirectionnel.dao;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import be.fabrice.bidirectionnel.entities.Employeur;

@Repository
@Transactional(readOnly=true)
public class EmployeurHibernateDao extends HibernateDaoSupport implements
		EmployeurDao {

	@Transactional(readOnly=false)
	public void save(Employeur employeur) {
		getSession().saveOrUpdate(employeur);
	}

}
