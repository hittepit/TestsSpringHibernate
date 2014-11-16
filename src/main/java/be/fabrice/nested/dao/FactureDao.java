package be.fabrice.nested.dao;

import java.util.List;

import be.fabrice.nested.entity.Facture;
import be.fabrice.nested.entity.Ligne;

public interface FactureDao {
	Facture findFacture(Long id);
	List<Ligne> findLignes(Facture facture);
	void save(Ligne ligne);
	void save(Facture facture);
}
