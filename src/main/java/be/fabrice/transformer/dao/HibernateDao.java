package be.fabrice.transformer.dao;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import be.fabrice.transformer.entity.ProprieteVO;

@Repository
public class HibernateDao extends HibernateDaoSupport implements Dao {
	
	public List<ProprieteVO> find(Integer joueurId) {
		String hql ="select pc.proprieteDefinition.id, pc.clone.personnage.id, "
				+ "pc.proprieteDefinition.valeurInitModifiable, pi.valeurInitiale, "
				+ "pc.tour, pc.valeur "
				+ "from ProprieteConso pc left join pc.proprieteDefinition.proprietesInitiales as pi "
				+ "where "
				+ "pc.proprieteDefinition.joueur.id = :joueurId";
		
		return getSession().createQuery(hql).setInteger("joueurId",joueurId).setResultTransformer(new ProprieteVOResultTransformer()).list();
	}
}
