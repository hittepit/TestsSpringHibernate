package be.fabrice.fetch.eager;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;

@Entity
public class Parent {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	private String name;
	@OneToMany(cascade={CascadeType.PERSIST,CascadeType.REMOVE})
	@JoinColumn(name="parent_fk")
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	private List<Enfant> enfants;
	
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
	public List<Enfant> getEnfants() {
		return enfants;
	}
	public void setEnfants(List<Enfant> enfants) {
		this.enfants = enfants;
	}
}
