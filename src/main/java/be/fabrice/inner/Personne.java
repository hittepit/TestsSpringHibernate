package be.fabrice.inner;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Embeddable
class Situation{
	private int nbEnfants;
	private boolean marie;
	
	/**
	 * Construteur protected car on ne veut pas le construire depuis l'extérieur. L'idéal serait
	 * private, mais Hibernate ne l'accepte pas dans ce cas.
	 */
	protected Situation() {}
	
	protected void setNbEnfants(int nbEnfants) {
		this.nbEnfants = nbEnfants;
	}
	protected void setMarie(boolean marie) {
		this.marie = marie;
	}
	public boolean isMarie() {
		return marie;
	}
	public int getNbEnfants() {
		return nbEnfants;
	}
	public boolean isFamille(){
		return nbEnfants > 0 || marie;
	}
}

@Entity
public class Personne {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@Embedded
	private Situation situation;
	
	private String nom;
	
	private Personne() {
		// TODO Auto-generated constructor stub
	}
	
	public Personne(String nom) {
		// Vérifications sur nom
		this.nom = nom;
		this.situation = new Situation();
	}

	public Situation getSituation() {
		return situation;
	}

	public void setSituation(Situation situation) {
		this.situation = situation;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}
	
	public void setMarie(boolean marie) {
		situation.setMarie(marie);
	}
	public void setNbEnfants(int n) {
		situation.setNbEnfants(n);
	}
}

