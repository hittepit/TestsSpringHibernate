package be.fabrice.oneToMany.dao;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import be.fabrice.oneToMany.entity.Employeur;
import be.fabrice.oneToMany.entity.Travailleur;

@Repository
@Transactional(readOnly=true)
public class EmployeurHibernateDao extends HibernateDaoSupport implements EmployeurDao {

	@Transactional(readOnly=false)
	public void save(Employeur employeur) {
		getSession().saveOrUpdate(employeur);
	}

	public Employeur find(Integer id) {
		return (Employeur)getSession().get(Employeur.class,id);
	}
	
	/**
	 * A noter le select e, sans quoi, à cause du join, la requête renvoie [Employeur,Travailleur]
	 */
	public Employeur find(Travailleur travailleur) {
		Object o = getSession().createQuery("select e from Employeur e left join e.travailleurs as t where t = :trav")
		.setParameter("trav", travailleur).uniqueResult();
		return (Employeur)o;
	}
	
	public Travailleur findTravailleur(Integer id) {
		return (Travailleur) getSession().get(Travailleur.class, id);
	}
}
