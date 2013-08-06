package be.fabrice.proxy.dao;

import be.fabrice.proxy.entity.Employeur;
import be.fabrice.proxy.entity.Travailleur;

public interface Dao {
	Travailleur findTravailleur(Integer id);
	Employeur findEmployeur(Integer id);
	Employeur loadEmployeur(Integer id);
}
