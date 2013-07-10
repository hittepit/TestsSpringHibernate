package be.fabrice.bidirectionnel.dao;

import be.fabrice.bidirectionnel.entities.Employeur;
import be.fabrice.bidirectionnel.entities.Travailleur;

public interface TravailleurDao {
	Travailleur find(Integer id);
	void save(Travailleur travailleur);
	void delete(Travailleur travailleur);
}
