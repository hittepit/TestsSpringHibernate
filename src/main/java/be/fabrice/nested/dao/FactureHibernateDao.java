package be.fabrice.nested.dao;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import be.fabrice.nested.entity.Facture;
import be.fabrice.nested.entity.Ligne;

@Repository
@Transactional(readOnly=true)
public class FactureHibernateDao extends HibernateDaoSupport implements FactureDao {

	public Facture findFacture(Long id) {
		return (Facture)getSession().get(Facture.class, id);
	}

	public List<Ligne> findLignes(Facture facture) {
		return getSession().createQuery("from Ligne l where l.facture=:facture").setParameter("facture", facture).list();
	}

	@Transactional(readOnly=false,propagation=Propagation.REQUIRES_NEW)
	public void save(Ligne ligne) {
		if(ligne.getPrice()<0){
			throw new RuntimeException();
		}
		getSession().saveOrUpdate(ligne);
	}

	@Transactional(readOnly=false)
	public void save(Facture facture) {
		getSession().saveOrUpdate(facture);
	}

}
