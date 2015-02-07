package be.fabrice.transaction;

import org.hibernate.Session;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class HibernateDao extends HibernateDaoSupport implements Dao {

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public Session propagateTransactionAndGetSession() {
		return getSession();
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public Session startNewTransactionAndGetSession() {
		return getSession();
	}

}
