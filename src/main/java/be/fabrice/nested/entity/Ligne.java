package be.fabrice.nested.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="LIGNE")
public class Ligne {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private int quantity;
	private double price;
	/**
	 * Je triche, c'est pour simplifier mes test, mais ça ne devrait pas exister dans mon entité
	 */
	@Transient
	private Long factureId;
//	@ManyToOne
//	@JoinColumn(name="FACT_ID")
//	private Facture facture;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
//	public Facture getFacture() {
//		return facture;
//	}
//	public void setFacture(Facture facture) {
//		this.facture = facture;
//	}
	public Long getFactureId() {
		return factureId;
	}
	public void setFactureId(Long factureId) {
		this.factureId = factureId;
	}
}
