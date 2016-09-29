package be.fabrice.inner;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
class Situation2{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	private int nbEnfants;
	private boolean marie;
	@ManyToOne
	@JoinColumn(name="personne_fk")
	private Personne2 personne;
	/**
	 * Construteur protected car on ne veut pas le construire depuis l'extérieur. L'idéal serait
	 * private, mais Hibernate ne l'accepte pas dans ce cas.
	 */
	protected Situation2() {}
	
	protected Situation2(Personne2 p) {
		this.personne = p;
	}
	
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
public class Personne2 {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@OneToOne(mappedBy="personne")
	private Situation2 situation;
	
	private String nom;
	
	private Personne2() {
		// TODO Auto-generated constructor stub
	}
	
	public Personne2(String nom) {
		// Vérifications sur nom
		this.nom = nom;
		this.situation = new Situation2(this);
	}

	public Situation2 getSituation() {
		return situation;
	}

	public void setSituation(Situation2 situation) {
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

