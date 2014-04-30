package be.fabrice.transformer.dao;

import java.util.List;

import be.fabrice.transformer.entity.ProprieteVO;

public interface Dao {
	List<ProprieteVO> find(Integer joueurId);
}
