package be.fabrice.interceptor;

public interface Dao {
	void save(Person p);

	Person find(Integer  id);
	
	void delete(Person p);
}
