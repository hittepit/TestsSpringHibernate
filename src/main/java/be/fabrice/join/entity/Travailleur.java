package be.fabrice.join.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="TRAV")
public class Travailleur {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private String nna;
	private String category;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNna() {
		return nna;
	}
	public void setNna(String nna) {
		this.nna = nna;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
}
