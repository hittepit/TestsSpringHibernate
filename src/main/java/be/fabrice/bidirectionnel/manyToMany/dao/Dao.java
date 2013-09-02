package be.fabrice.bidirectionnel.manyToMany.dao;

import be.fabrice.bidirectionnel.manyToMany.entity.Book;
import be.fabrice.bidirectionnel.manyToMany.entity.Category;

public interface Dao {
	Book findBook(Integer id);

	Category findCategory(Integer id);

	void save(Object o);
}
