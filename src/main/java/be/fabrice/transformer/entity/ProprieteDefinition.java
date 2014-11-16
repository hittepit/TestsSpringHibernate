package be.fabrice.transformer.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="PROP_DEF")
public class ProprieteDefinition {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	private String nom;
	
	@Column(name="VALEUR_INIT")
	private boolean valeurInitModifiable;
	
	@ManyToOne
	@JoinColumn(name="JOUEUR_FK")
	private Joueur joueur;
	
	@OneToMany(mappedBy="proprieteDefinition")
	private List<ProprieteInitiale> proprietesInitiales;

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
}
