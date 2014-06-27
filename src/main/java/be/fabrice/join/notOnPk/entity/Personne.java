package be.fabrice.join.notOnPk.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="PERS")
public class Personne {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private String name;
	private String code;
	@OneToOne(optional=true,fetch=FetchType.LAZY)
	@JoinColumn(name="CODE",referencedColumnName="CODE_P")
	private Parametres parametres;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Parametres getParametres() {
		return parametres;
	}
	public void setParametres(Parametres parametres) {
		this.parametres = parametres;
	}
}
