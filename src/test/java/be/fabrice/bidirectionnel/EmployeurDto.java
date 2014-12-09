package be.fabrice.bidirectionnel;

import be.fabrice.bidirectionnel.Employeur;
import be.fabrice.bidirectionnel.Travailleur;

/**
 * <p>
 * Petite classe utilitaire pour contenir les données reçues en JDBC pour un
 * employeur.
 * </p>
 * <p>
 * L'entité {@link Employeur} aurait pu être utilisée dans ce cas-ci, mais ce
 * n'est pas propre. De plus, l'entité {@link Employeur} contient une liste de
 * {@link Travailleur}. Dans ce cas simple d'utilisation, cette liste ne serait
 * utilisée.
 * </p>
 * 
 * @author fabrice.claes
 * 
 */
public class EmployeurDto {
	private Integer id;
	private String name;

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
}
