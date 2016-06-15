package be.fabrice.distinct;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Line {
	@Id
	private Integer id;
	private double amount;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
}
