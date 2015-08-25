package be.fabrice.equals;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

@Entity
public class MasterMany {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	private String name;
	@ManyToMany
	@JoinTable(name="master_slave", joinColumns={@JoinColumn(name="master_fk")}, inverseJoinColumns=@JoinColumn(name="slave_fk"))
	private List<SlaveMany> slaves;

	
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
	public List<SlaveMany> getSlaves() {
		return slaves;
	}
	public void setSlaves(List<SlaveMany> slaves) {
		this.slaves = slaves;
	}
	
	@Override
	public int hashCode() {
		HashcodeCounter.tic(MasterMany.class);
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		EqualsCounter.tic(MasterMany.class);
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof MasterMany))
			return false;
		MasterMany other = (MasterMany) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
