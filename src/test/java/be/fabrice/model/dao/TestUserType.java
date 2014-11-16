package be.fabrice.model.dao;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fabrice.model.entity.Book;
import be.fabrice.model.entity.Isbn;

class BookVo{
	Long id;
	String isbn;
	String title;
	String author;
}


class BookVoMapper implements RowMapper<BookVo>{

	@Override
	public BookVo mapRow(ResultSet rs, int rowNum) throws SQLException {
		BookVo b = new BookVo();
		b.id = rs.getLong("ID");
		b.isbn = rs.getString("ISBN");
		b.title = rs.getString("TITLE");
		b.author = rs.getString("AUTHOR");
		return b;
	}
	
}

@ContextConfiguration(locations="classpath:model/test-model-spring.xml")
public class TestUserType extends AbstractTransactionalTestNGSpringContextTests{
	@Autowired
	private Dao dao;
	
	@BeforeMethod
	public void beforeMethod(){
		executeSqlScript("model/test-script.sql", false);
	}
	
	@Test
	public void testBookWithAuthorInsertion(){
		Book b = new Book(new Isbn("978-212-34-5680-3"),"Test","Toto");
		dao.save(b);
		
		assertNotNull(b.getId(),"Id doit avoir été généré");
		
		List<BookVo> books = jdbcTemplate.query("select * from BOOK where id=?",new BookVoMapper(),b.getId());
		assertEquals(books.size(), 1);
		
		BookVo book = books.get(0);
		
		assertEquals(book.id,b.getId());
		assertEquals(book.title,b.getTitle());
		assertEquals(book.author,b.getAuthor());
		assertEquals(book.isbn,b.getIsbn().getValue());
	}
	
	@Test
	public void testFindBook(){
		Book b = dao.findBook(1000L);
		assertNotNull(b);
		
		assertEquals(b.getTitle(),"Dune");
		assertEquals(b.getIsbn().getValue(),"9780441172719");
	}
}
