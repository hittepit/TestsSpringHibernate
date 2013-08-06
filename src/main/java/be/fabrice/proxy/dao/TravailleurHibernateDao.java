package be.fabrice.proxy.dao;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import be.fabrice.proxy.entity.Travailleur;

@Repository
@Transactional(readOnly=true)
public class TravailleurHibernateDao extends HibernateDaoSupport implements
		TravailleurDao {
	
	public Travailleur find(Integer id){
		return (Travailleur) getSession().get(Travailleur.class, id);
	}
}
