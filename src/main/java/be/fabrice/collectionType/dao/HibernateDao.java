package be.fabrice.collectionType.dao;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import be.fabrice.collectionType.entity.Facture;

@Repository
@Transactional(readOnly=true)
public class HibernateDao extends HibernateDaoSupport implements Dao {

	public Facture find(Long id) {
		return (Facture) getSession().get(Facture.class, id);
	}

}
