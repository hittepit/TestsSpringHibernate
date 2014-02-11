package be.fabrice.model.dao;

import be.fabrice.model.entity.Book;
import be.fabrice.model.entity.Rectangle;

public interface Dao {
	void save(Object entity);
	
	Rectangle findRectangle(Long id);
	
	Book findBook(Long id);
}
