package be.fabrice.evict.dao;

import java.util.List;

import be.fabrice.evict.entity.Chien;

public interface Dao {
	Chien findChien(Integer id);
	void save(Chien chien);
	List<Chien> findChiens(Chien chien);
}
