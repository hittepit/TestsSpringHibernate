package be.fabrice.model.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="RECT4")
public class RectangleV4 {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private double dimension1;
	private double dimension2;
	
	private RectangleV4(){}
	
	public RectangleV4(double dimension1, double dimension2){
		if(dimension1<=0.0 || dimension2<=0.0){
			throw new IllegalArgumentException("Les dimensions doiventt être strictement positive");
		}
		this.dimension1 = dimension1;
		this.dimension2 = dimension2;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public double getLongueur() {
		return dimension1>dimension2?dimension1:dimension2;
	}
	
	public double getLargeur() {
		return dimension1<dimension2?dimension1:dimension2;
	}
	public double getSurface(){
		return dimension1*dimension2;
	}
}
