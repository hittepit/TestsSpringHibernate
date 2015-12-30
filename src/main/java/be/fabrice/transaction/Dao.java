package be.fabrice.transaction;

import java.util.List;

import org.hibernate.Session;

public interface Dao {
	Session propagateTransactionAndGetSession();
	Session startNewTransactionAndGetSession();
	Item find(Long id);
	List<Item> findAll();
	
	void nonTransactionException();
	
	void requiredWithException();
	
	void requiresNewWithException();
	
	void nestedWithException();
	
	void notSupportedWithException();
}
