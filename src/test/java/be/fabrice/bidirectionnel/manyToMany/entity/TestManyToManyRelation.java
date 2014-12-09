package be.fabrice.bidirectionnel.manyToMany.entity;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.bidirectionnel.manyToMany.dao.Dao;

@Test(testName="Tests sur les relations many-to-many",suiteName="Relations many to many")
@ContextConfiguration(locations="classpath:manyToMany/test-manyToMany-spring.xml")
public class TestManyToManyRelation extends AbstractTransactionalTestNGSpringContextTests {
	@Autowired
	private Dao dao;
	@Autowired
	private SessionFactory sessionFactory;
	
	@BeforeMethod
	public void beforeMethod(){
		executeSqlScript("manyToMany/test-script.sql", false);
	}
	
	@Test
	public void testSimpleFindBook(){
		Book b = dao.findBook(1001);
		assertEquals(b.getCategories().size(), 2);
	}
	
	@Test
	public void testSimpleFindCategory(){
		Category c = dao.findCategory(2001);
		assertEquals(c.getBooks().size(), 2);
	}
	
	@Test
	public void testBookCreationRefrencingExistingCatgeoriesWithUnneededCascading(){
		Book b = new Book();
		b.setTitle("test");
		
		List<Category> categories = new ArrayList<Category>();
		
		Category c = dao.findCategory(2001);
		c.getBooks().add(b);
		categories.add(c);
		c = dao.findCategory(2002);
		c.getBooks().add(b);
		categories.add(c);
		
		b.setCategories(categories);
		
		dao.save(b);
		sessionFactory.getCurrentSession().flush();
		
		assertEquals(countRowsInTable("book"),4);
		assertEquals(countRowsInTable("cat"),3);
		assertEquals(countRowsInTable("b_c"),5);
	}

	/*
	 * Cascading nécessaire, sans quoi Category n'est pas sauvé (et exception).
	 */
	@Test
	public void testBookAndCategoryCreationWithCascading(){
		final Book b = new Book();
		b.setTitle("test");
		List<Book> books = new ArrayList<Book>(){{add(b);}}; //Pour établir la bidirectionnalité
		
		List<Category> categories = new ArrayList<Category>();
		
		Category c = new Category();
		c.setName("new1");
		c.setBooks(books);
		categories.add(c);
		c = new Category();
		c.setName("new2");
		c.setBooks(books);
		categories.add(c);
		
		b.setCategories(categories);
		
		dao.save(b);
		sessionFactory.getCurrentSession().flush();
		
		assertEquals(countRowsInTable("book"),4);
		assertEquals(countRowsInTable("cat"),5);
		assertEquals(countRowsInTable("b_c"),5);
	}
	
	/**
	 * Malgré l'absence de cascading, parce que la bidirectionnalité est correcte, les relations book catégory
	 * sont persistée (à cause du dirty checking sur l'entité book, master)
	 */
	@Test
	public void testCategoryCreationReferencingExistingBookWithoutUnecessaryCascading(){
		Category c = new Category();
		c.setName("new1");
		
		List<Book> books = new ArrayList<Book>();
		Book b = dao.findBook(1001);
		b.getCategories().add(c);
		books.add(b);
		b = dao.findBook(1002);
		b.getCategories().add(c);
		books.add(b);
		
		c.setBooks(books);
		
		dao.save(c);
		sessionFactory.getCurrentSession().flush();
		
		assertEquals(countRowsInTable("book"),3);
		assertEquals(countRowsInTable("cat"),4);
		assertEquals(countRowsInTable("b_c"),5);
	}
	
	/**
	 * Lorsqu'une catégorie est créée avec des livres et category est sauvé, comme le maître
	 * de la relation est book et qu'il n'y a pas de cascading, ni book, ni la relation ne sont sauvées.
	 * C'est donc un point d'attention.
	 */
	@Test
	public void testBookAndCategoryCreationWithoutCascadingMustFail(){
		final Category c = new Category();
		c.setName("new1");
		List<Category> cats = new ArrayList<Category>(){{add(c);}};
		
		List<Book> books = new ArrayList<Book>();
		Book b = new Book();
		b.setTitle("test1");
		b.setCategories(cats);
		books.add(b);
		b = new Book();
		b.setTitle("test2");
		b.setCategories(cats);
		books.add(b);
		
		c.setBooks(books);
		
		dao.save(c);
		sessionFactory.getCurrentSession().flush();

		assertEquals(countRowsInTable("book"),3,"Nothing inserted");
		assertEquals(countRowsInTable("cat"),4);
		assertEquals(countRowsInTable("b_c"),3,"Nothing inserted");
	}
}
