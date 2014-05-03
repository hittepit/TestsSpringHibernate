package be.fabrice.join.dao;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import be.fabrice.join.entity.Personne;

@Repository
@Transactional(readOnly=true)
public class HibernateDao extends HibernateDaoSupport implements Dao {

	public List<Personne> findPersonneByTravailleurCategory(String Category) {
		String hql = "select p from Personne p, Travailleur t where p.nna=t.nna and t.category = :cat";
		return getSession().createQuery(hql).setParameter("cat", Category).list();
	}
}
