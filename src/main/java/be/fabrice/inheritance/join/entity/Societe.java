package be.fabrice.inheritance.join.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="SOCIETE")
public class Societe extends Employeur {
	@Column(name="NUM")
	private String numeroEntreprise;

	public String getNumeroEntreprise() {
		return numeroEntreprise;
	}

	public void setNumeroEntreprise(String bce) {
		this.numeroEntreprise = bce;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getNumeroEntreprise() == null) ? 0 : getNumeroEntreprise().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Societe))
			return false;
		Societe other = (Societe) obj;
		if (getNumeroEntreprise() == null) {
			if (other.getNumeroEntreprise() != null)
				return false;
		} else if (!getNumeroEntreprise().equals(other.getNumeroEntreprise()))
			return false;
		return true;
	}
}
