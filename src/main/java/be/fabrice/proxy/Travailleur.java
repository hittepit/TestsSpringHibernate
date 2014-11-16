package be.fabrice.proxy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name="TRAV")
public class Travailleur {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private Integer id;
	
	@Column(name="NOM")
	private String nom;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="EMP_ID")
	private Employeur employeur;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="EMPBIS_ID")
	private EmployeurPresqueCorrect employeurPresqueCorrect;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="EMPTER_ID")
	private EmployeurCorrect employeurCorrect;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
	public void setEmployeur(Employeur employeur) {
		this.employeur = employeur;
	}
	public EmployeurCorrect getEmployeurCorrect() {
		return employeurCorrect;
	}
	public void setEmployeurCorrect(EmployeurCorrect employeurCorrect) {
		this.employeurCorrect = employeurCorrect;
	}
	public EmployeurPresqueCorrect getEmployeurPresqueCorrect() {
		return employeurPresqueCorrect;
	}
	public void setEmployeurPresqueCorrect(EmployeurPresqueCorrect employeurPresqueCorrect) {
		this.employeurPresqueCorrect = employeurPresqueCorrect;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nom == null) ? 0 : nom.hashCode());
		return result;
	}
	/**
	 * Dans le cadre du test, l'égalité est définie sur base du nom (ce n'est évidemment pas correct dans le monde réel).
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Travailleur other = (Travailleur) obj;
		if (nom == null) {
			if (other.nom != null)
				return false;
		} else if (!nom.equals(other.nom))
			return false;
		return true;
	}
}
