package be.fabrice.transformer.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.transform.ResultTransformer;

import be.fabrice.transformer.entity.ProprieteVO;

public class ProprieteVOResultTransformer implements ResultTransformer {
	/*
	"select pc.proprieteDefinition.id, pc.clone.personnage.id, "
				+ "pc.proprieteDefinition.valeurInitModifiable, pi.valeurInitiale, "
				+ "pc.tour, pc.valeur "
	 */
	@Override
	public Object transformTuple(Object[] tuple, String[] aliases) {
		ProprieteVO vo = new ProprieteVO();
		vo.setProprieteDefinitionId((Integer) tuple[0]);
		vo.setPersonnageId((Integer) tuple[1]);
		vo.setInitModifiable((Boolean) tuple[2]);
		vo.setValeurInit((Double) tuple[3]);
		vo.getValeursParTour().put((Integer) tuple[4], (Double) tuple[5]);
		return vo;
	}

	@Override
	public List transformList(List collection) {
		List<ProprieteVO> vos = (List<ProprieteVO>) collection;
		List<ProprieteVO> fvos = new ArrayList<ProprieteVO>();

		for (ProprieteVO vo : vos) {
			ProprieteVO fvo;
			if (fvos.contains(vo)) {
				fvo = fvos.get(fvos.indexOf(vo));
			} else {
				fvo = new ProprieteVO();
				fvo.setInitModifiable(vo.isInitModifiable());
				fvo.setValeurInit(vo.getValeurInit());
				fvo.setPersonnageId(vo.getPersonnageId());
				fvo.setProprieteDefinitionId(vo.getProprieteDefinitionId());
				fvos.add(fvo);
			}
			fvo.getValeursParTour().putAll(vo.getValeursParTour());
		}
		return fvos;
	}

}
