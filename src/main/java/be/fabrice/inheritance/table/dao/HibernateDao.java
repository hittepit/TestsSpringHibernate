package be.fabrice.inheritance.table.dao;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import be.fabrice.inheritance.table.entity.Boss;
import be.fabrice.inheritance.table.entity.Employeur;
import be.fabrice.inheritance.table.entity.Societe;
import be.fabrice.inheritance.table.entity.Travailleur;
import org.springframework.transaction.annotation.Transactional;

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

	@Transactional(readOnly = false)
	@Override
	public void save(Employeur employeur) {
		getSession().persist(employeur);
	}

	@Override
	public List<Boss> findBosses() {
		return getSession().createQuery("from Boss b").list();
	}

	@Override
	public List<Societe> findSocietes() {
		return getSession().createQuery("from Societe s").list();
	}

	@Override
	public void clearSession() {
		getSession().clear();
	}
}
