package be.fabrice.fetch.eager;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class BiParent {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	private String name;
	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true, mappedBy="parent")
	private List<BiEnfant> enfants;
	
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
	public List<BiEnfant> getEnfants() {
		return enfants;
	}
	public void setEnfants(List<BiEnfant> enfants) {
		this.enfants = enfants;
	}

}
