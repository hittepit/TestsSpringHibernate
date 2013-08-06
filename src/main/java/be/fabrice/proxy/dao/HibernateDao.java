package be.fabrice.proxy.dao;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import be.fabrice.proxy.entity.Employeur;
import be.fabrice.proxy.entity.Travailleur;

@Repository
@Transactional(readOnly=true)
public class HibernateDao extends HibernateDaoSupport implements
		Dao {
	
	public Travailleur findTravailleur(Integer id){
		return (Travailleur) getSession().get(Travailleur.class, id);
	}
	public Employeur findEmployeur(Integer id){
		return (Employeur) getSession().get(Employeur.class,id);
	}
	public Employeur loadEmployeur(Integer id) {
		return (Employeur) getSession().load(Employeur.class,id);
	}
}
