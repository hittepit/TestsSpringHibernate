package be.fabrice.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;

@Entity
public class Book {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	@Type(type="be.fabrice.model.entity.IsbnUserType")
	private Isbn isbn;
	@Column(name="TITLE",nullable=false)
	private String title;
	@Column(name="AUTHOR",nullable=true)
	private String author;
	
	private Book(){}
	
	public Book(Isbn isbn, String title, String author){
		if(StringUtils.isBlank(title)){
			throw new IllegalArgumentException("title cannot be empty");
		}
		this.isbn = isbn;
		this.title = StringUtils.trim(title);
		this.author = StringUtils.trimToNull(author);
	}

	public Long getId() {
		return id;
	}
	public Isbn getIsbn() {
		return isbn;
	}
	public String getTitle() {
		return title;
	}
	public String getAuthor() {
		return author;
	}
}
