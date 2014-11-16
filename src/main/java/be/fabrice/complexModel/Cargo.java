package be.fabrice.complexModel;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Cargo {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	private String nom;
	private String pays;
//	private List<Container> containers;
	@OneToMany(mappedBy="cargo")
	private List<Route> routes;
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
	public String getPays() {
		return pays;
	}
	public void setPays(String pays) {
		this.pays = pays;
	}
//	public List<Container> getContainers() {
//		return containers;
//	}
//	public void setContainers(List<Container> containers) {
//		this.containers = containers;
//	}
	public List<Route> getRoutes() {
		return routes;
	}
	public void setRoutes(List<Route> routes) {
		this.routes = routes;
	}
}
