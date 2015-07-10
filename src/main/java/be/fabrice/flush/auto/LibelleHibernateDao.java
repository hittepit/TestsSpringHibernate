package be.fabrice.flush.auto;

import org.hibernate.FlushMode;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly=true)
public class LibelleHibernateDao extends HibernateDaoSupport implements LibelleDao {

	@Override
	public Libelle find(Integer id) {
		return (Libelle) getSession().get(Libelle.class, id);
	}

	@Override
	public Libelle findByLabelStandard(String label) {
		return (Libelle) getSession().createQuery("from Libelle l where l.label = :label").setParameter("label", label).uniqueResult();
	}

	@Override
	public ImmutableLibelle findImmutableByLabelStandard(String label) {
		return (ImmutableLibelle) getSession().createQuery("from ImmutableLibelle l where l.label = :label").setParameter("label", label).uniqueResult();
	}
	
	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public Libelle findByLabelNewTransaction(String label) {
		return (Libelle) getSession().createQuery("from Libelle l where l.label = :label").setParameter("label", label).uniqueResult();
	}

	@Override
	public Libelle findByLabelFlushModeManual(String label) {
		return (Libelle) getSession().createQuery("from Libelle l where l.label = :label").setParameter("label", label)
				.setFlushMode(FlushMode.MANUAL).uniqueResult();
	}

}
