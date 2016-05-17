package be.fabrice.refresh;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Entity1 {
	@Id
	private Integer id;
	
	private String s1;
	
	private String s2;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getS1() {
		return s1;
	}

	public void setS1(String s1) {
		this.s1 = s1;
	}

	public String getS2() {
		return s2;
	}

	public void setS2(String s2) {
		this.s2 = s2;
	}
}
