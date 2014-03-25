package be.fabrice.complexModel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Container {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	private String numero;
	@ManyToOne
	@JoinColumn(name="CARGO_ID")
	private Cargo cargo;
//	private List<Article> articles;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public Cargo getCargo() {
		return cargo;
	}
	public void setCargo(Cargo cargo) {
		this.cargo = cargo;
	}
//	public List<Article> getArticles() {
//		return articles;
//	}
//	public void setArticles(List<Article> articles) {
//		this.articles = articles;
//	}
}
