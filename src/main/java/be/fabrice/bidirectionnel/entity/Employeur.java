package be.fabrice.bidirectionnel.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
	
	@OneToMany(mappedBy="employeur",orphanRemoval=true)
	@Cascade({CascadeType.SAVE_UPDATE})
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
	public void addTravailleur(Travailleur travailleur){
		if(this.travailleurs==null){
			this.travailleurs = new ArrayList<Travailleur>();
		}
		travailleurs.add(travailleur);
		travailleur.setEmployeur(this);
	}
	public void removeTravailleur(Travailleur travailleur){
		if(this.travailleurs != null){
			this.travailleurs.remove(travailleur);
			travailleur.setEmployeur(null);
		}
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	/**
	 * Dans le cadre du test, l'égalité est définie sur base du nom (ce n'est évidemment pas correct dans le monde réel).
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Employeur other = (Employeur) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
