package be.fabrice.inheritance.join.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="TRAV")
public class Travailleur {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private Integer id;
	
	@Column(name="NOM")
	private String name;
	
	@ManyToOne
	@JoinColumn(name="EMP_ID")
	private Employeur employeur;
	
	/**
	 * Uniquement à des fins de tests car n'a aucun sens
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="LAZY_EMP_ID")
	private Employeur lazyEmployeur;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Employeur getEmployeur() {
		return employeur;
	}

	public void setEmployeur(Employeur employeur) {
		this.employeur = employeur;
	}

	public Employeur getLazyEmployeur() {
		return lazyEmployeur;
	}

	public void setLazyEmployeur(Employeur lazyEmployeur) {
		this.lazyEmployeur = lazyEmployeur;
	}
}
