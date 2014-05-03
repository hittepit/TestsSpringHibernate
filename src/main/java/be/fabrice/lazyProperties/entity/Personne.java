package be.fabrice.lazyProperties.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="PERS1")
public class Personne implements LazyInitializable<PersonneLazyPorperties>{
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	private String name;

	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id",insertable=false,updatable=false)
	private PersonneLazyPorperties personneLazyPorperties;
	
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

	public PersonneLazyPorperties getPersonneLazyPorperties() {
		return personneLazyPorperties;
	}

	@Override
	public PersonneLazyPorperties getLazyProperties() {
		return getPersonneLazyPorperties(); //Lazy init
	}
	
	public void setLazyName(String lazyName){
		PersonneLazyPorperties lazy = getPersonneLazyPorperties(); //Lazy init!
		if(lazy == null){
			lazy = new PersonneLazyPorperties();
			personneLazyPorperties = lazy;
		}
		lazy.setLazyName(lazyName);
	}
}
