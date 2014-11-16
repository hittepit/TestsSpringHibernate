package be.fabrice.criteria.alias.dao;

import java.util.List;

import be.fabrice.criteria.alias.entity.Employe;

public interface Dao {
	Long incorrectCountEmployes(Long societeId);
	List<Employe> incorrectFindEmployes(Long societeId);
	Long countEmployes(Long societeId);
	List<Employe> findEmployes(Long societeId);
}
