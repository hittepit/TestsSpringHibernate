package be.fabrice.evict.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Example;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import be.fabrice.evict.entity.Chien;

/**
 * Un Dao 'service' pour des besoins de démonstration.
 * @author fabrice.claes
 *
 */
@Repository
@Transactional(readOnly=true)
public class HibernateDao extends HibernateDaoSupport implements Dao {

	public Chien findChien(Integer id) {
		return (Chien) getSession().get(Chien.class, id);
	}

	public void save(Chien chien) {
		// TODO Auto-generated method stub
		
	}

	public List<Chien> findChiens(Chien chien) {
		Criteria c = getSession().createCriteria(Chien.class)
			.add(Example.create(chien));
		return c.list();
	}
	
}
