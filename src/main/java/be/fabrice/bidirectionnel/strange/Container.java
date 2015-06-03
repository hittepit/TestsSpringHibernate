package be.fabrice.bidirectionnel.strange;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="CONTAINER")
public class Container {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	private String name;
	@OneToMany(cascade=CascadeType.ALL, mappedBy="container",orphanRemoval=true, fetch=FetchType.EAGER)
	private List<Item> items;
	
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
	public List<Item> getItems() {
		return items;
	}
	public void setItems(List<Item> items) {
		this.items = items;
	}
	
	public void addItem(Item item){
		if(this.items == null) this.items = new ArrayList<Item>();
		
		if(item.getContainer() == null){
			this.items.add(item);
			item.setContainer(this);
		}
	}
	
	/**
	 * Retrait bidirectionnel de l'item.
	 * 
	 * @param item
	 */
	public void removeItem(Item item){
		if(this.items != null && this.items.remove(item)){
			item.setContainer(null);
		}
	}
	
	/**
	 * Efface proprement tous les items du container en faisant un clear de la liste
	 */
	public void clearItems(){
		if(items != null){
			for(Item item:items){
				item.setContainer(null);
			}
		}
		
		items.clear();
	}
	
	/**
	 * Efface proprement tous les items du container en les retirant de la liste
	 */
	public void removeAllItems(){
		if(items != null){
			List<Item> its = new ArrayList<Item>(items);
			for(Item it:its) removeItem(it);
		}
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
		if (!(obj instanceof Container))
			return false;
		Container other = (Container) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
