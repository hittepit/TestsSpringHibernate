package be.fabrice.equals;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Type;

@Entity
public class Book1 {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	private String title;
	@Type(type="be.fabrice.equals.IsbnUserType1")
	private Isbn isbn;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Isbn getIsbn() {
		return isbn;
	}
	public void setIsbn(Isbn isbn1) {
		this.isbn = isbn1;
	}

	@Override
	public int hashCode() {
		HashcodeCounter.tic(this.getClass());
		final int prime = 31;
		int result = 1;
		result = prime * result + ((isbn == null) ? 0 : isbn.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		EqualsCounter.tic(this.getClass());
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Book1))
			return false;
		Book1 other = (Book1) obj;
		if (isbn == null) {
			if (other.isbn != null)
				return false;
		} else if (!isbn.equals(other.isbn))
			return false;
		return true;
	}
}
