package be.fabrice.transaction;

import java.util.List;

import org.hibernate.Session;

public interface Dao {
	Session propagateTransactionAndGetSession();
	Session startNewTransactionAndGetSession();
	Item find(Long id);
	List<Item> findAll();
}
