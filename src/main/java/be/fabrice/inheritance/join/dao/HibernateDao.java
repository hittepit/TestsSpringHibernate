package be.fabrice.inheritance.join.dao;

import java.util.List;

import org.hibernate.transform.AliasToBeanResultTransformer;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import be.fabrice.inheritance.join.entity.Boss;
import be.fabrice.inheritance.join.entity.Employeur;
import be.fabrice.inheritance.join.entity.EmployeurVo;
import be.fabrice.inheritance.join.entity.Travailleur;

@Repository
@Transactional(readOnly=true)
public class HibernateDao extends HibernateDaoSupport implements Dao{

	public Travailleur findTravailleur(Integer id) {
		return (Travailleur) getSession().get(Travailleur.class, id);
	}

	public Employeur findEmployeur(Integer id) {
		return (Employeur) getSession().get(Employeur.class,id);
	}
	
	public Employeur findSimpleEmployeur(Integer id){
		return (Employeur) getSession().load(Employeur.class,id);
	}


	public List<Employeur> findAll() {
		return getSession().createQuery("from Employeur").list();
	}

	public Boss findBoss(Integer id) {
		return (Boss) getSession().get(Boss.class,id);
	}

	public EmployeurVo findEmployeurVo(Integer id){
		return (EmployeurVo) getSession()
			.createQuery("select e.id as id,e.country as country from Employeur e where e.id=:id")
			.setInteger("id", id).setResultTransformer(new AliasToBeanResultTransformer(EmployeurVo.class))
			.uniqueResult();
	}
}
