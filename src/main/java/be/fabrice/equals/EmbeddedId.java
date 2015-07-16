package be.fabrice.equals;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class EmbeddedId implements Serializable{
	private int key;
	private String value;

	@SuppressWarnings("unused")
	private EmbeddedId() {}
	
	public EmbeddedId(int key, String value) {
		this.key=key;
		this.value = value;
	}
	
	public int getKey() {
		return key;
	}
	public String getValue() {
		return value;
	}
	@Override
	public int hashCode() {
		HashcodeCounter.tic(this.getClass());
		final int prime = 31;
		int result = 1;
		result = prime * result + key;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		EqualsCounter.tic(this.getClass());
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof IdPk))
			return false;
		EmbeddedId other = (EmbeddedId) obj;
		if (key != other.key)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

}
