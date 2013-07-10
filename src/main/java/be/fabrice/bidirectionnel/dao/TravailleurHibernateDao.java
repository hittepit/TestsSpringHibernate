package be.fabrice.bidirectionnel.dao;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import be.fabrice.bidirectionnel.entities.Travailleur;

@Repository
@Transactional(readOnly=true)
public class TravailleurHibernateDao extends HibernateDaoSupport implements
		TravailleurDao {
	
	public Travailleur find(Integer id){
		return (Travailleur) getSession().get(Travailleur.class, id);
	}
	
	@Transactional(readOnly=false)
	public void save(Travailleur travailleur) {
		getSession().saveOrUpdate(travailleur);
	}

	public void delete(Travailleur travailleur) {
		getSession().delete(travailleur);
	}

}
