package be.fabrice.transformer.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name="INIT")
public class ProprieteInitiale {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	@Column(name="VAL_INIT")
	private double valeurInitiale;
	@ManyToOne
	@JoinColumn(name="PROP_DEF_FK")
	private ProprieteDefinition proprieteDefinition;
	@ManyToOne
	@JoinColumn(name="PERSO_FK")
	private Personnage personnage;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public double getValeurInitiale() {
		return valeurInitiale;
	}
	public void setValeurInitiale(double valeurInitiale) {
		this.valeurInitiale = valeurInitiale;
	}
	public ProprieteDefinition getProprieteDefinition() {
		return proprieteDefinition;
	}
	public void setProprieteDefinition(ProprieteDefinition proprieteDefinition) {
		this.proprieteDefinition = proprieteDefinition;
	}
	public Personnage getPersonnage() {
		return personnage;
	}
	public void setPersonnage(Personnage personnage) {
		this.personnage = personnage;
	}
}
