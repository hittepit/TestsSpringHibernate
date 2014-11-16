package be.fabrice.cache.entity;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name="CIV")
@Cache(region="CIV",usage=CacheConcurrencyStrategy.READ_ONLY)
public class Civilite {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private String code;
	@Embedded
	private EmbeddedName nom;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public EmbeddedName getNom() {
		return nom;
	}
	public void setNom(EmbeddedName name) {
		this.nom = name;
	}
}
