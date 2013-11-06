package be.fabrice.cache.dao;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import be.fabrice.cache.entity.Civilite;
import be.fabrice.cache.entity.EtatCivil;
import be.fabrice.cache.entity.Personne;
import be.fabrice.cache.entity.Situation;
import be.fabrice.cache.entity.Statut;

@Repository
@Transactional(readOnly=true)
public class DaoHibernate extends HibernateDaoSupport implements Dao {

	public Personne find(Long id) {
		return (Personne)getSession().get(Personne.class,id);
	}

	public List<Statut> findAllStatut() {
		return getSession().createQuery("from Statut").list();
	}
	
	public Statut findStatut(Long id) {
		return (Statut)getSession().get(Statut.class,id);
	}

	public List<EtatCivil> findAllEtatCivil() {
		return getSession().createQuery("from EtatCivil").list();
	}
	
	public EtatCivil findEtatCivil(Long id){
		return (EtatCivil)getSession().get(EtatCivil.class,id);
	}

	public List<Situation> findAllSitutions() {
		return getSession().createQuery("from Situation").list();
	}

	public Situation findSituation(Long id) {
		return (Situation)getSession().get(Situation.class, id);
	}

	@Transactional(readOnly=false)
	public void save(Object entity) {
		getSession().saveOrUpdate(entity);
	}

	public List<Civilite> findAllCivilites() {
		return getSession().createQuery("from Civilite").list();
	}
}
