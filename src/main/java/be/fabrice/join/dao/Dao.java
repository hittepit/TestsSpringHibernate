package be.fabrice.join.dao;

import java.util.List;

import be.fabrice.join.entity.Personne;

public interface Dao {
	List<Personne> findPersonneByTravailleurCategory(String Category);
}
