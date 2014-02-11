package be.fabrice.model.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="RECT")
public class Rectangle {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private double longueur;
	private double largeur;
	
	private Rectangle(){}
	
	public Rectangle(double longueur, double largeur){
		if(longueur<=0.0){
			throw new IllegalArgumentException("La longueur doit être strictement positive");
		}
		if(largeur<=0.0){
			throw new IllegalArgumentException("La largeur doit être strictement positive");
		}
		this.longueur = longueur;
		this.largeur = largeur;
	}
	
	public Long getId() {
		return id;
	}
	public double getLongueur() {
		return longueur;
	}
	public double getLargeur() {
		return largeur;
	}
	public double getSurface() {
		return longueur * largeur;
	}
}
