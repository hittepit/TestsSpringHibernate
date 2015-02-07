package be.fabrice.transaction;

import org.hibernate.Session;

public interface Service {
	Session[] propagateTransactionTwice();
}
