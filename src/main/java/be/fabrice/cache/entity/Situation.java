package be.fabrice.cache.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name="SIT")
@Cache(region="SIT",usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Situation {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private int enfants;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public int getEnfants() {
		return enfants;
	}
	public void setEnfants(int enfants) {
		this.enfants = enfants;
	}
}
