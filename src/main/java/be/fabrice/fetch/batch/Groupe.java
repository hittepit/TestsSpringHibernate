package be.fabrice.fetch.batch;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;

@Entity
@Table(name="GROUPE")
public class Groupe {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	private String name;
	
	@OneToMany
	@JoinColumn(name="GROUP_ID")
	private List<NoBatch> noBatchs;
	
	@OneToMany
	@JoinColumn(name="GROUP_ID")
	@BatchSize(size=3)
	private List<Batch> batchs;
	
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
	public List<NoBatch> getNoBatchs() {
		return noBatchs;
	}
	public void setNoBatchs(List<NoBatch> noBatchs) {
		this.noBatchs = noBatchs;
	}
	public List<Batch> getBatchs() {
		return batchs;
	}
	public void setBatchs(List<Batch> batchs) {
		this.batchs = batchs;
	}
}
