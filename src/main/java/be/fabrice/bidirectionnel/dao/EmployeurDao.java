package be.fabrice.bidirectionnel.dao;

import be.fabrice.bidirectionnel.entity.Employeur;


public interface EmployeurDao {
	Employeur find(Integer id);
	void save(Employeur employeur);
}
