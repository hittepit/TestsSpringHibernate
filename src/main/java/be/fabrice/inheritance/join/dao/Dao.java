package be.fabrice.inheritance.join.dao;

import java.util.List;

import be.fabrice.inheritance.join.entity.Boss;
import be.fabrice.inheritance.join.entity.Employeur;
import be.fabrice.inheritance.join.entity.EmployeurVo;
import be.fabrice.inheritance.join.entity.Independant;
import be.fabrice.inheritance.join.entity.Societe;
import be.fabrice.inheritance.join.entity.Travailleur;

public interface Dao {
	Travailleur findTravailleur(Integer id);
	Employeur findEmployeur(Integer id);
	Employeur findSimpleEmployeur(Integer id);
	EmployeurVo findEmployeurVo(Integer id);
	List<Employeur> findAll();
	Boss findBoss(Integer id);

	void save(Employeur employeur);

    List<Boss> findBosses();

	List<Societe> findSocietes();

	List<Independant> findIndependants();

	void clear();
}
