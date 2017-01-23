package be.fabrice.criteria.alias.history;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Worker {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long Id;
	private String name;
	@ManyToOne
	@JoinColumn(name="BOSS_FK")
	private Boss boss;
	@OneToMany(mappedBy="worker")
	private List<Contact> contactHistory;
	public Long getId() {
		return Id;
	}
	public void setId(Long id) {
		Id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Boss getBoss() {
		return boss;
	}
	public void setBoss(Boss boss) {
		this.boss = boss;
	}
	public List<Contact> getContactHistory() {
		return contactHistory;
	}
	public void setContactHistory(List<Contact> contactHistory) {
		this.contactHistory = contactHistory;
	}
}
