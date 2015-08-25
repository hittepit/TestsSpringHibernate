package be.fabrice.equals;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity
public class SlaveMany {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	private String name;
	@ManyToMany(mappedBy="slaves")
	private List<MasterMany> masters;
	
	
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
	public List<MasterMany> getMasters() {
		return masters;
	}
	public void setMasters(List<MasterMany> masters) {
		this.masters = masters;
	}
	
	@Override
	public int hashCode() {
		HashcodeCounter.tic(SlaveMany.class);
		final int prime = 31;
		int result = 1;
		result = prime * result + ((masters == null) ? 0 : masters.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		EqualsCounter.tic(SlaveMany.class);
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof SlaveMany))
			return false;
		SlaveMany other = (SlaveMany) obj;
		if (masters == null) {
			if (other.masters != null)
				return false;
		} else if (!masters.equals(other.masters))
			return false;
		return true;
	}
}
