package be.fabrice.transaction;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Service
public class ServiceImpl implements Service {
	@Autowired
	private Dao dao;
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public Session[] propagateTransactionTwice() {
		return new Session[]{dao.propagateTransactionAndGetSession(),dao.propagateTransactionAndGetSession()};
	}

}
