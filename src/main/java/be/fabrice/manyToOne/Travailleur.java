package be.fabrice.manyToOne;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name="TRAV")
public class Travailleur {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private Integer id;
	
	@Column(name="NOM")
	private String nom;
	
	@ManyToOne
	@JoinColumn(name="EMP_ID")
	private Employeur employeur;
	
	@ManyToOne
	@JoinColumn(name="EMP_2_ID")
	@Fetch(FetchMode.SELECT)
	private Employeur employeurWithSelect;

	public Integer getId() {
		return id;
	}

	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public Employeur getEmployeur() {
		return employeur;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getNom() == null) ? 0 : getNom().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Travailleur))
			return false;
		Travailleur other = (Travailleur) obj;
		if (getNom() == null) {
			if (other.getNom() != null)
				return false;
		} else if (!getNom().equals(other.getNom()))
			return false;
		return true;
	}
	
	
}
