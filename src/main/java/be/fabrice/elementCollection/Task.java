package be.fabrice.elementCollection;

import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OrderColumn;

@Entity
public class Task {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private String name;
	@ElementCollection
	@CollectionTable(name="LABEL", joinColumns=@JoinColumn(name="task_id"))
	@Column(name="name", nullable=false, length=10)
	private List<String> labels;
	@ElementCollection
	@CollectionTable(name="O_LABEL", joinColumns=@JoinColumn(name="task_id"))
	@Column(name="name", nullable=false, length=10)
	@OrderColumn(name="label_index", nullable=false)
	private List<String> orderedLabels;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getLabels() {
		return labels;
	}
	public void setLabels(List<String> labels) {
		this.labels = labels;
	}
	
	public List<String> getOrderedLabels() {
		return orderedLabels;
	}
	public void setOrderedLabels(List<String> orderedLabels) {
		this.orderedLabels = orderedLabels;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Task other = (Task) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
