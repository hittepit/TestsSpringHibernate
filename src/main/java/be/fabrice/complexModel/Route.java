package be.fabrice.complexModel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Route {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	private String de;
	private String a;
	@ManyToOne
	@JoinColumn(name="CARGO_ID")
	private Cargo cargo;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getDe() {
		return de;
	}
	public void setDe(String from) {
		this.de = from;
	}
	public String getA() {
		return a;
	}
	public void setA(String to) {
		this.a = to;
	}
	public Cargo getCargo() {
		return cargo;
	}
	public void setCargo(Cargo cargo) {
		this.cargo = cargo;
	}
}
