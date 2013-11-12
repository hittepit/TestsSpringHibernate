package be.fabrice.partial.dao;

import java.util.List;

import be.fabrice.partial.entity.Travailleur;

public interface Dao {
	Travailleur findJustTravailleur(String name);
	List<Travailleur> findJustTravailleurByCategory(String categoryCode);
	Travailleur findStarTravailleur(String name); 
}
