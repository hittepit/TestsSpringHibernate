package be.fabrice.inheritance.table.dao;

import java.util.List;

import be.fabrice.inheritance.table.entity.Boss;
import be.fabrice.inheritance.table.entity.Employeur;
import be.fabrice.inheritance.table.entity.Societe;
import be.fabrice.inheritance.table.entity.Travailleur;

public interface Dao {
	Travailleur findTravailleur(Integer id);
	Employeur findEmployeur(Integer id);
	List<Boss> findAllBosses();
	List<Societe> findAllSocietes();
	List<Employeur> findAllEmployeurs();
}
