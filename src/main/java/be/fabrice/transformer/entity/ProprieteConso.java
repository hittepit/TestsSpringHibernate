package be.fabrice.transformer.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="CONSO")
public class ProprieteConso {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	private int tour;
	private double valeur;
	@ManyToOne
	@JoinColumn(name="CLONE_FK")
	private Clone clone;
	@ManyToOne
	@JoinColumn(name="PROP_DEF_FK")
	private ProprieteDefinition proprieteDefinition;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public int getTour() {
		return tour;
	}
	public void setTour(int tour) {
		this.tour = tour;
	}
	public double getValeur() {
		return valeur;
	}
	public void setValeur(double valeur) {
		this.valeur = valeur;
	}
	public Clone getClone() {
		return clone;
	}
	public void setClone(Clone clone) {
		this.clone = clone;
	}
	public ProprieteDefinition getProprieteDefinition() {
		return proprieteDefinition;
	}
	public void setProprieteDefinition(ProprieteDefinition proprieteDefinition) {
		this.proprieteDefinition = proprieteDefinition;
	}
}
