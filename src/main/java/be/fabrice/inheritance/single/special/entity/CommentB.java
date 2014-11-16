package be.fabrice.inheritance.single.special.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("B")
public class CommentB extends AbstractComment{
	private int valeur;

	public int getValeur() {
		return valeur;
	}

	public void setValeur(int value) {
		this.valeur = value;
	}
	
	
}
