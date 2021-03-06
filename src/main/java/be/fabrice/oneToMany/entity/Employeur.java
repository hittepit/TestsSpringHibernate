package be.fabrice.oneToMany.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Table(name="EMP")
public class Employeur {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private Integer id;
	
	@Column(name="NOM")
	private String name;

	@OneToMany
	@JoinColumn(name="EMP_ID")
	@Cascade(CascadeType.SAVE_UPDATE)
	private List<Travailleur> travailleurs;
	
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
	
	public List<Travailleur> getTravailleurs() {
		return travailleurs;
	}
	
	public void setTravailleurs(List<Travailleur> travailleurs) {
		this.travailleurs = travailleurs;
	}
	
	public void add(Travailleur t){
		if(this.travailleurs == null){
			this.travailleurs = new ArrayList<Travailleur>();
		}
		this.travailleurs.add(t);
	}
}
