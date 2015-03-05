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
@Table(name="CONTAINER2")
public class Container2 {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	private String name;
	@OneToMany(mappedBy="container",orphanRemoval=true, fetch=FetchType.EAGER)
	@Cascade({CascadeType.ALL})
	private List<Item2> items;
	
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
	public List<Item2> getItems() {
		return items;
	}
	public void setItems(List<Item2> items) {
		this.items = items;
	}
	
	public void addItem(Item2 item){
		if(this.items == null) this.items = new ArrayList<Item2>();
		
		if(item.getContainer() == null){
			this.items.add(item);
			item.setContainer(this);
		}
	}
	
	public void removeItem(Item2 item){
		if(this.items != null && this.items.remove(item)){
			item.setContainer(null);
		}
	}
	
	public void clearItems(){
		if(items != null){
			for(Item2 item:items){
				item.setContainer(null);
			}
		}
		
		items.clear();
	}
	
	public void removeAllItems(){
		if(items != null){
			List<Item2> its = new ArrayList<Item2>(items);
			for(Item2 it:its) removeItem(it);
		}
	}
}
