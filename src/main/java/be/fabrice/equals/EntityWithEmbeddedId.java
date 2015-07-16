package be.fabrice.equals;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="EID")
public class EntityWithEmbeddedId {
	@EmbeddedId
	private be.fabrice.equals.EmbeddedId id;
	
	private String name;

	public be.fabrice.equals.EmbeddedId getId() {
		return id;
	}

	public void setId(be.fabrice.equals.EmbeddedId id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		HashcodeCounter.tic(this.getClass());
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		EqualsCounter.tic(this.getClass());
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof EntityWithEmbeddedId))
			return false;
		EntityWithEmbeddedId other = (EntityWithEmbeddedId) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	
}
