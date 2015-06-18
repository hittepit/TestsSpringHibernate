package be.fabrice.comparehql.appi;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Contrat {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	@Column(name="NUm")
	private String numeroContrat;
	@ManyToOne
	@JoinColumn(name="CAT_FK")
	private Categorie categorie;
	@ManyToOne
	@JoinColumn(name="TRAV_FK")
	private Travailleur travailleur;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getNumeroContrat() {
		return numeroContrat;
	}
	public void setNumeroContrat(String numeroContrat) {
		this.numeroContrat = numeroContrat;
	}
	public Categorie getCategorie() {
		return categorie;
	}
	public void setCategorie(Categorie categorie) {
		this.categorie = categorie;
	}
	public Travailleur getTravailleur() {
		return travailleur;
	}
	public void setTravailleur(Travailleur travailleur) {
		this.travailleur = travailleur;
	}
}
