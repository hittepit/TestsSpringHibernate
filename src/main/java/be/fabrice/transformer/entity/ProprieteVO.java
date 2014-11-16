package be.fabrice.transformer.entity;

import java.util.HashMap;
import java.util.Map;

public class ProprieteVO {
	private Integer proprieteDefinitionId;
	private Integer personnageId;
	private boolean initModifiable;
	private Double valeurInit;
	private Map<Integer, Double> valeursParTour = new HashMap<Integer, Double>();
	public Integer getProprieteDefinitionId() {
		return proprieteDefinitionId;
	}
	public void setProprieteDefinitionId(Integer proprieteDefinitionId) {
		this.proprieteDefinitionId = proprieteDefinitionId;
	}
	public Integer getPersonnageId() {
		return personnageId;
	}
	public void setPersonnageId(Integer personnageId) {
		this.personnageId = personnageId;
	}
	public boolean isInitModifiable() {
		return initModifiable;
	}
	public void setInitModifiable(boolean initModifiable) {
		this.initModifiable = initModifiable;
	}
	public Double getValeurInit() {
		return valeurInit;
	}
	public void setValeurInit(Double valeurInit) {
		this.valeurInit = valeurInit;
	}
	public Map<Integer, Double> getValeursParTour() {
		return valeursParTour;
	}
	public void setValeursParTour(Map<Integer, Double> valeursParTour) {
		this.valeursParTour = valeursParTour;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((personnageId == null) ? 0 : personnageId.hashCode());
		result = prime * result + ((proprieteDefinitionId == null) ? 0 : proprieteDefinitionId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ProprieteVO))
			return false;
		ProprieteVO other = (ProprieteVO) obj;
		if (personnageId == null) {
			if (other.personnageId != null)
				return false;
		} else if (!personnageId.equals(other.personnageId))
			return false;
		if (proprieteDefinitionId == null) {
			if (other.proprieteDefinitionId != null)
				return false;
		} else if (!proprieteDefinitionId.equals(other.proprieteDefinitionId))
			return false;
		return true;
	}
	
}
