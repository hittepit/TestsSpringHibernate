package be.fabrice.proxy.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="EMPBIS")
public class EmployeurPresqueCorrect {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private Integer id;
	
	@Column(name="NOM")
	private String name;
//	
//	@OneToMany(mappedBy="employeurCorrect")
//	@Cascade({CascadeType.SAVE_UPDATE,CascadeType.DELETE_ORPHAN})
//	private List<Travailleur> travailleurs;

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
//	public List<Travailleur> getTravailleurs() {
//		return travailleurs;
//	}
//	public void setTravailleurs(List<Travailleur> travailleurs) {
//		this.travailleurs = travailleurs;
//	}
//	public void addTravailleur(Travailleur travailleur){
//		if(this.travailleurs==null){
//			this.travailleurs = new ArrayList<Travailleur>();
//		}
//		travailleurs.add(travailleur);
//		travailleur.setEmployeurPresqueCorrect(this);
//	}
//	public void removeTravailleur(Travailleur travailleur){
//		if(this.travailleurs != null){
//			this.travailleurs.remove(travailleur);
//			travailleur.setEmployeur(null);
//		}
//	}
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
		if (!(obj instanceof EmployeurCorrect))
			return false;
		EmployeurCorrect other = (EmployeurCorrect) obj;
		if (getName() == null) {
			if (other.getName() != null)
				return false;
		} else if (!getName().equals(other.getName()))
			return false;
		return true;
	}


}
