package be.fabrice.model.dao;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import be.fabrice.model.entity.Isbn;

public class TestIsbnRobustness {
	@Test
	public void testCorrectIsbn10(){
		Isbn isbn = new Isbn("2-1234-5680-2");
		assertEquals(isbn.getValue(), "9782123456803");
	}
	
	@Test
	public void testCorrectIsbn13(){
		Isbn isbn = new Isbn("978-212-34-5680-3");
		assertEquals(isbn.getValue(), "9782123456803");
	}

	@Test(expectedExceptions=IllegalArgumentException.class)
	public void testIncorrectIsbn10(){
		new Isbn("2-1234-5680-3");
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void testIncorrectIsbn13(){
		new Isbn("978-212-34-5680-2");
	}
}
