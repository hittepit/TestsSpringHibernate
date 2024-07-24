package be.fabrice.inheritance.single.dao;

import java.util.List;

import be.fabrice.inheritance.single.entity.Boss;
import be.fabrice.inheritance.single.entity.Employeur;
import be.fabrice.inheritance.single.entity.Independant;
import be.fabrice.inheritance.single.entity.Societe;
import be.fabrice.inheritance.single.entity.Travailleur;

public interface Dao {
	Travailleur findTravailleur(Integer id);
	Employeur findEmployeur(Integer id);
	List<Boss> findAllBosses();
	List<Societe> findAllSocietes();
	List<Employeur> findAllEmployeurs();
	List<Employeur> findEmployeurByBossName(String name);

    void save(Employeur employeur);

	List<Employeur> findAll();

    List<Boss> findBosses();

	List<Societe> findSocietes();

	List<Independant> findIndependants();

	void clear();
}
