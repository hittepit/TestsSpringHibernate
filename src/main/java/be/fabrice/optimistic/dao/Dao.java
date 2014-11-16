package be.fabrice.optimistic.dao;

import be.fabrice.optimistic.entity.Boss;
import be.fabrice.optimistic.entity.Employee;

public interface Dao {
	Employee findEmployee(Long id);
	Boss findBoss(Long id);
	void save(Object entity);
}
