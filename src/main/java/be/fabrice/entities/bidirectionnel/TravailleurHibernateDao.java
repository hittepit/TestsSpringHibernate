package be.fabrice.entities.bidirectionnel;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly=true)
public class TravailleurHibernateDao extends HibernateDaoSupport implements
		TravailleurDao {

	@Transactional(readOnly=false)
	public void save(Travailleur travailleur) {
		getSession().saveOrUpdate(travailleur);
	}

}
