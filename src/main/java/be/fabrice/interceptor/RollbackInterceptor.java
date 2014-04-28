package be.fabrice.interceptor;

import org.hibernate.EmptyInterceptor;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

/**
 * Cette intercepteur marque systématiquement la trasaction pour rollback. Un de ses effets est qu'aucune modification
 * de la base de données n'est effectuée via Hibernate.
 * @author fabrice.claes
 *
 */
@Component("rollbackInterceptor")
public class RollbackInterceptor extends EmptyInterceptor{
	@Override
	public void beforeTransactionCompletion(Transaction tx) {
		tx.rollback();
		super.beforeTransactionCompletion(tx);
	}
}
