package be.fabrice.cache.dao;

import java.util.List;

import be.fabrice.cache.entity.EtatCivil;
import be.fabrice.cache.entity.Personne;
import be.fabrice.cache.entity.Situation;
import be.fabrice.cache.entity.Statut;

public interface Dao {
	Personne find(Long id);
	List<Statut> findAllStatut();
	Statut findStatut(Long id);
	List<EtatCivil> findAllEtatCivil();
	EtatCivil findEtatCivil(Long id);
	List<Situation> findAllSitutions();
	Situation findSituation(Long id);
}
