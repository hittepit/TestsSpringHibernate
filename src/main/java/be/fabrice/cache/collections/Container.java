package be.fabrice.cache.collections;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name="CONT")
public class Container {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	private String name;
	@OneToMany
	@JoinColumn(name="CONT_FK")
	@Cache(region="elements",usage=CacheConcurrencyStrategy.READ_ONLY)
	private List<Element> elements;
	@OneToMany
	@JoinColumn(name="CONT_FK")
	@Cache(region="cachedElements",usage=CacheConcurrencyStrategy.READ_ONLY)
	private List<CachedElement> cachedElements;
	@OneToMany(fetch=FetchType.EAGER)
	@JoinColumn(name="CONT_FK")
	@Cache(region="eagerElements",usage=CacheConcurrencyStrategy.READ_ONLY)
	private List<EagerElement> eagerElements;
	
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
	public List<Element> getElements() {
		return elements;
	}
	public void setElements(List<Element> elements) {
		this.elements = elements;
	}
	public List<CachedElement> getCachedElements() {
		return cachedElements;
	}
	public void setCachedElements(List<CachedElement> cachedElements) {
		this.cachedElements = cachedElements;
	}
}
