package be.fabrice.equals;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Entity
public class MasterList {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	private String name;
	
	@OneToMany
	@JoinColumn(name="ml_fk")
	private List<SimpleEntity> simpleEntities;

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

	public List<SimpleEntity> getSimpleEntities() {
		return simpleEntities;
	}

	public void setSimpleEntities(List<SimpleEntity> simpleEntities) {
		this.simpleEntities = simpleEntities;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((simpleEntities == null) ? 0 : simpleEntities.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		EqualsCounter.tic(this.getClass());
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof MasterList))
			return false;
		MasterList other = (MasterList) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (simpleEntities == null) {
			if (other.simpleEntities != null)
				return false;
		} else if (!simpleEntities.equals(other.simpleEntities))
			return false;
		return true;
	}
}
