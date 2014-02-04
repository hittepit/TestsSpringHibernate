package be.fabrice.optimistic.dao;

import be.fabrice.optimistic.entity.Employee;

public interface Dao {
	Employee findEmployee(Long id);
	void save(Employee employee);
}
