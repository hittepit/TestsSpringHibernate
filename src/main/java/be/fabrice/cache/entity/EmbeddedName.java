package be.fabrice.cache.entity;

import javax.persistence.Embeddable;

@Embeddable
public class EmbeddedName {
	private String nom;

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}
}
