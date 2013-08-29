package be.fabrice.manyToOne.dao;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import be.fabrice.manyToOne.entity.Employeur;

@Repository
@Transactional(readOnly=true)
public class EmployeurHibernateDao extends HibernateDaoSupport implements EmployeurDao {

	public Employeur find(Integer id) {
		return (Employeur) getSession().get(Employeur.class, id);
	}

}
