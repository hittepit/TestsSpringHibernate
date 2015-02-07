package be.fabrice.transaction;

import org.hibernate.Session;

public interface Dao {
	Session propagateTransactionAndGetSession();
	Session startNewTransactionAndGetSession();}
