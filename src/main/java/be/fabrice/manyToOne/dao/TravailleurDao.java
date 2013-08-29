package be.fabrice.manyToOne.dao;

import java.util.List;

import be.fabrice.manyToOne.entity.Employeur;
import be.fabrice.manyToOne.entity.Travailleur;

public interface TravailleurDao {
	List<Travailleur> findTravailleurs(Employeur employeur);
}
