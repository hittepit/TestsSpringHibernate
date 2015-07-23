package be.fabrice.flush.auto;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Dossier {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	private String nom;
	private char statut;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String name) {
		this.nom = name;
	}
	public char getStatut() {
		return statut;
	}
	public void setStatut(char statut) {
		this.statut = statut;
	}
}
