package be.fabrice.criteria.alias.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import be.fabrice.criteria.alias.entity.Employe;

@Repository
@Transactional(readOnly=true)
public class HibernateDao extends HibernateDaoSupport implements Dao {

	public Long incorrectCountEmployes(Long societeId) {
		return (Long) createIncorrectCriteria(societeId).setProjection(Projections.rowCount()).uniqueResult();
	}

	public List<Employe> incorrectFindEmployes(Long societeId) {
		return createIncorrectCriteria(societeId).list();
	}

	private Criteria createIncorrectCriteria(Long societeId){
		Criteria criteria = getSession().createCriteria(Employe.class);
		criteria.createAlias("patron.societe", "soc");
		criteria.add(Restrictions.eq("soc.id", societeId));
		return criteria;
	}
	
	public Long countEmployes(Long societeId) {
		return (Long) createCorrectCriteria(societeId).setProjection(Projections.rowCount()).uniqueResult();
	}
	
	public List<Employe> findEmployes(Long societeId) {
		return createCorrectCriteria(societeId).list();
	}

	private Criteria createCorrectCriteria(Long societeId){
		Criteria criteria = getSession().createCriteria(Employe.class);
		criteria.createAlias("patron", "pat");
		criteria.createAlias("pat.societe","soc");
		criteria.add(Restrictions.eq("soc.id", societeId));
		return criteria;
	}
}
