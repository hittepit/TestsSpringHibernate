package be.fabrice.bidirectionnel.manyToMany.dao;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import be.fabrice.bidirectionnel.manyToMany.entity.Book;
import be.fabrice.bidirectionnel.manyToMany.entity.Category;

@Repository
@Transactional(readOnly=true)
public class HibernateDao extends HibernateDaoSupport implements Dao {

	public Book findBook(Integer id) {
		return (Book)getSession().get(Book.class, id);
	}

	public Category findCategory(Integer id) {
		return (Category) getSession().get(Category.class,id);
	}

	public void save(Object o) {
		getSession().saveOrUpdate(o);
	}
}
