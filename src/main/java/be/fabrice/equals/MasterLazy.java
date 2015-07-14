package be.fabrice.equals;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class MasterLazy {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	private String name;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="s_ik")
	private SimpleEntity simpleEntity;

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

	public SimpleEntity getSimpleEntity() {
		return simpleEntity;
	}

	public void setSimpleEntity(SimpleEntity simpleEntity) {
		this.simpleEntity = simpleEntity;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((simpleEntity == null) ? 0 : simpleEntity.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		EqualsCounter.tic(this.getClass());
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof MasterLazy))
			return false;
		MasterLazy other = (MasterLazy) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (simpleEntity == null) {
			if (other.simpleEntity != null)
				return false;
		} else if (!simpleEntity.equals(other.simpleEntity))
			return false;
		return true;
	}

}
