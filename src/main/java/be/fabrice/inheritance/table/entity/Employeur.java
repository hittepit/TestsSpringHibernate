package be.fabrice.inheritance.table.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public abstract class Employeur {
	/**
	 * Attention, dans ce type d'héritage, la stratégie de génération de clé ne peut jamais être
	 * auto ou identity.
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name="ID")
	private Integer id;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
}
