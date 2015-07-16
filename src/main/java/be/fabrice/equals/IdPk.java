package be.fabrice.equals;

import java.io.Serializable;

public class IdPk implements Serializable{
	private int key;
	private String value;

	@SuppressWarnings("unused")
	private IdPk() {}
	
	public IdPk(int key, String value) {
		this.key=key;
		this.value = value;
	}
	
	@Override
	public int hashCode() {
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
		IdPk other = (IdPk) obj;
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
