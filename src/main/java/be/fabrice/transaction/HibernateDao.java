package be.fabrice.transaction;

import java.util.List;

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

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public Item find(Long id){
		return (Item) getSession().get(Item.class,id);
	}
	
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public List<Item> findAll(){
		return getSession().createCriteria(Item.class).list();
	}
}
