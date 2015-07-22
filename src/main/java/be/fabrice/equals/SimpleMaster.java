package be.fabrice.equals;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class SimpleMaster {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	private String name;
	@ManyToOne
	@JoinColumn(name="s_fk")
	private SimpleEntity simpleEntity;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public SimpleEntity getSimpleEntity() {
		return simpleEntity;
	}
	public void setSimpleEntity(SimpleEntity simpleEntity) {
		this.simpleEntity = simpleEntity;
	}
}
