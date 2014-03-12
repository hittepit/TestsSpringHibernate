package be.fabrice.model.dao;

import be.fabrice.model.entity.Book;
import be.fabrice.model.entity.RectangleV1;

public interface Dao {
	void save(Object entity);
	
	RectangleV1 findRectangle(Long id);
	
	Book findBook(Long id);
}
