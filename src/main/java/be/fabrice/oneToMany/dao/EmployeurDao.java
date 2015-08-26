package be.fabrice.oneToMany.dao;

import be.fabrice.oneToMany.entity.Employeur;
import be.fabrice.oneToMany.entity.Travailleur;

public interface EmployeurDao {
	void save(Employeur employeur);
	void save(Travailleur travailleur);
	Employeur find(Integer id);
	Employeur find(Travailleur travailleur);
	Travailleur findTravailleur(Integer id);
}
