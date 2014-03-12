package be.fabrice.model.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="RECT1")
public class RectangleV1 {
	private Long id;
	private double longueur;
	private double largeur;
	
	private RectangleV1(){}
	
	public RectangleV1(double longueur, double largeur){
		if(longueur<=0.0){
			throw new IllegalArgumentException("La longueur doit être strictement positive");
		}
		if(largeur<=0.0){
			throw new IllegalArgumentException("La largeur doit être strictement positive");
		}
		this.longueur = longueur;
		this.largeur = largeur;
	}

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public double getLongueur() {
		return longueur;
	}
	public void setLongueur(double longueur) {
		if(longueur<=0.0){
			throw new IllegalArgumentException("La longueur doit être strictement positive");
		}
		this.longueur = longueur;
	}
	
	public double getLargeur() {
		return largeur;
	}
	public void setLargeur(double largeur) {
		if(largeur<=0.0){
			throw new IllegalArgumentException("La largeur doit être strictement positive");
		}
		this.largeur = largeur;
	}
}
