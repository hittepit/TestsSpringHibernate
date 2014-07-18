package be.fabrice.inheritance.single.special.entity;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Table(name="COMMENT")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="PARENT_TYPE")
public abstract class AbstractComment {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
//	private ClassA a;
	
//	private ClassB b;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
