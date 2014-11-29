package be.fabrice.bidirectionnel.dao;

import be.fabrice.bidirectionnel.Employeur;

/**
 * <p>
 * Petite classe utilitaire pour contenur les données JDBC d'un travailleur.
 * </p>
 * <p>
 * Cette classe est préférée à l'entité car cette dernière contient un lien vers
 * un {@link Employeur} alors que le JDBC renvoie juste la foreign key. Restons
 * simple.
 * </p>
 * 
 * @author fabrice.claes
 * 
 */
public class TravailleurDto {
	private Integer id;
	private String name;
	private Integer employeurId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getEmployeurId() {
		return employeurId;
	}

	public void setEmployeurId(Integer employeurId) {
		this.employeurId = employeurId;
	}
}
