package be.fabrice.equals;

public class Isbn {
	private String value;
	
	public Isbn(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		HashcodeCounter.tic(this.getClass());
		final int prime = 31;
		int result = 1;
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
		if (!(obj instanceof Isbn))
			return false;
		Isbn other = (Isbn) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
}
