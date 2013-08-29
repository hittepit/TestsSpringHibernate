package be.fabrice.manyToOne.dao;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import be.fabrice.manyToOne.entity.Employeur;
import be.fabrice.manyToOne.entity.Travailleur;

@Repository
@Transactional(readOnly=true)
public class TravailleurHibernateDao extends HibernateDaoSupport implements TravailleurDao {

	public List<Travailleur> findTravailleurs(Employeur employeur) {
		return getSession().createQuery("from Travailleur t where t.employeur=:emp")
			.setParameter("emp", employeur).list();
	}

}
