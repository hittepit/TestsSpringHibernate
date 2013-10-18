package be.fabrice.manyToOne.dao;

import be.fabrice.manyToOne.entity.Employeur;
import be.fabrice.manyToOne.entity.Travailleur;

public interface EmployeurDao {
	Employeur find(Integer id);

	Travailleur findTravailleur(Integer id);
}
