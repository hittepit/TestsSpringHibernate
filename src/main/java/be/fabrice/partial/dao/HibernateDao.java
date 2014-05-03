package be.fabrice.partial.dao;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import be.fabrice.partial.entity.Travailleur;

@Repository
@Transactional(readOnly=true)
public class HibernateDao extends HibernateDaoSupport implements Dao {

	/**
	 * Très mauvaise requête pour juste un find sur id (session.get)
	 */
	public Travailleur findJustTravailleur(String name) {
		return (Travailleur) getSession().createQuery("select t from Travailleur t where t.nom=:nom").setString("nom", name).uniqueResult();
	}

	public List<Travailleur> findJustTravailleurByCategory(String categoryCode) {
		return getSession().createQuery("select t from Travailleur t where t.category.code=:code").setString("code", categoryCode).list();
	}

	public Travailleur findStarTravailleur(String name) {
		return (Travailleur) getSession().createQuery("from Travailleur t where t.nom=:nom").setString("nom", name).uniqueResult();
	}
}
