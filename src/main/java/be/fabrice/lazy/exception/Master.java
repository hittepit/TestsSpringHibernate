package be.fabrice.lazy.exception;

import java.util.Comparator;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import org.hibernate.annotations.OrderBy;

class DetailComparator implements Comparator<Detail>{

	@Override
	public int compare(Detail o1, Detail o2) {
		return - (o1.getName().compareTo(o2.getName()));
	}
	
}

@Entity
public class Master {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private String name;
	
	@OneToMany(fetch=FetchType.EAGER)
	@JoinColumn(name="master_id")
	//@Sort(comparator=DetailComparator.class)
	@OrderBy(clause="name desc")
	private List<Detail> details;
	
	public Long getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public List<Detail> getDetails() {
		return details;
	}
}
