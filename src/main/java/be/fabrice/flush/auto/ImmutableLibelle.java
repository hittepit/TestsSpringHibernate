package be.fabrice.flush.auto;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Immutable;

@Entity
@Immutable
public class ImmutableLibelle {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	private String label;
	public Integer getId() {
		return id;
	}
	public String getLabel() {
		return label;
	}
}
