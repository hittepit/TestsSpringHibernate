package be.fabrice.bidirectionnel.dao;

import be.fabrice.bidirectionnel.entity.Travailleur;

public interface TravailleurDao {
	Travailleur find(Integer id);
	void save(Travailleur travailleur);
	void delete(Travailleur travailleur);
}
