package be.fabrice.fetch.eager;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Entity
public class Invoice {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	private String title;
	@OneToMany(fetch=FetchType.LAZY)
	@JoinColumn(name="invoice_fk")
	private List<Line> lines;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<Line> getLines() {
		return lines;
	}
	public void setLines(List<Line> lines) {
		this.lines = lines;
	}

}
