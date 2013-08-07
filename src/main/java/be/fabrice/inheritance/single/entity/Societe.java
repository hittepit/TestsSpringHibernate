package be.fabrice.inheritance.single.entity;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("SOCIETE")
public class Societe extends Employeur {
	@Column(name="BCE")
	private String bce;

	public String getBce() {
		return bce;
	}

	public void setBce(String bce) {
		this.bce = bce;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getBce() == null) ? 0 : getBce().hashCode());
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
		if (getBce() == null) {
			if (other.getBce() != null)
				return false;
		} else if (!getBce().equals(other.getBce()))
			return false;
		return true;
	}
}
