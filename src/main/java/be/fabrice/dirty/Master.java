package be.fabrice.dirty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Entity
public class Master {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	private String name;
	@OneToMany
	@JoinColumn(name="detail_fk")
	private List<Detail> details = new ArrayList<>();
	@OneToMany
	@JoinColumn(name="detailbis_fk")
	private Set<DetailBis> detailsBis = new HashSet<>();
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Detail> getDetails() {
		return details;
	}
	public void setDetails(List<Detail> details) {
		this.details = details;
	}
	public Set<DetailBis> getDetailsBis() {
		return detailsBis;
	}
	public void setDetailsBis(Set<DetailBis> detailsBis) {
		this.detailsBis = detailsBis;
	}
}
