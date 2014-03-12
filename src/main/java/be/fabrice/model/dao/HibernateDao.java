package be.fabrice.model.dao;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import be.fabrice.model.entity.Book;
import be.fabrice.model.entity.RectangleV1;

@Repository
public class HibernateDao extends HibernateDaoSupport implements Dao {

	@Override
	public void save(Object entity) {
		getSession().save(entity);
	}

	@Override
	public RectangleV1 findRectangle(Long id) {
		return (RectangleV1) getSession().get(RectangleV1.class,id);
	}

	@Override
	public Book findBook(Long id) {
		return (Book) getSession().get(Book.class, id);
	}
}
