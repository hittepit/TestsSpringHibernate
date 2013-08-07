package be.fabrice.inheritance.table.dao;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import be.fabrice.inheritance.table.entity.Boss;
import be.fabrice.inheritance.table.entity.Employeur;
import be.fabrice.inheritance.table.entity.Societe;
import be.fabrice.inheritance.table.entity.Travailleur;

@Repository
public class HibernateDao extends HibernateDaoSupport implements Dao {

	public Travailleur findTravailleur(Integer id) {
		return (Travailleur) getSession().get(Travailleur.class, id);
	}

	public Employeur findEmployeur(Integer id) {
		return (Employeur) getSession().get(Employeur.class,id);
	}

	/**
	 * Une première manière de récupérer tous les objets d'un type
	 */
	public List<Boss> findAllBosses() {
		return getSession().createCriteria(Boss.class).list();
	}

	/**
	 * Une autre...
	 */
	public List<Societe> findAllSocietes() {
		return getSession().createQuery("from Societe").list();
	}

	public List<Employeur> findAllEmployeurs() {
		return getSession().createQuery("from Employeur").list();
	}
}
