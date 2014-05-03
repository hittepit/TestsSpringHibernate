package be.fabrice.lazyProperties.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="PERS1")
public class PersonneLazyPorperties implements LazyProperties {
	@Id
	private Integer id;
	private String lazyName;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getLazyName() {
		return lazyName;
	}
	public void setLazyName(String lazyName) {
		this.lazyName = lazyName;
	}
	
	@Override
	public void setPrimaryKey(Serializable pk) {
		this.id = (Integer) pk;
	}
}
