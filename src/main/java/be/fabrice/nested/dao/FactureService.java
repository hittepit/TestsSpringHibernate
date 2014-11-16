package be.fabrice.nested.dao;

import java.util.List;

import be.fabrice.nested.entity.Facture;
import be.fabrice.nested.entity.Ligne;

public interface FactureService {
	void save(Facture facture, List<Ligne> lignes);
}
