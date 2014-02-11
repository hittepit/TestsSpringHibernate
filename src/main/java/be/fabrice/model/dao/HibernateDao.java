package be.fabrice.model.dao;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import be.fabrice.model.entity.Book;
import be.fabrice.model.entity.Rectangle;

@Repository
public class HibernateDao extends HibernateDaoSupport implements Dao {

	@Override
	public void save(Object entity) {
		getSession().save(entity);
	}

	@Override
	public Rectangle findRectangle(Long id) {
		return (Rectangle) getSession().get(Rectangle.class,id);
	}

	@Override
	public Book findBook(Long id) {
		return (Book) getSession().get(Book.class, id);
	}
}
