package be.fabrice.bidirectionnel.strange;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Table(name="CONTAINER")
public class Container {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	private String name;
	@OneToMany(mappedBy="container",orphanRemoval=true, fetch=FetchType.EAGER)
	@Cascade({CascadeType.ALL})
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
	
	public void removeItem(Item item){
		if(this.items != null && this.items.remove(item)){
			item.setContainer(null);
		}
	}
	
	public void clearItems(){
		if(items != null){
			for(Item item:items){
				item.setContainer(null);
			}
		}
		
		items.clear();
	}
	
	public void removeAllItems(){
		if(items != null){
			List<Item> its = new ArrayList<Item>(items);
			for(Item it:its) removeItem(it);
		}
	}
}
