package be.fabrice.model.dao;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import be.fabrice.model.entity.Rectangle;

public class TestRectangleRobustness {
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void testImpossibleToCreateRectangleWithNegativeLongueur(){
		Rectangle r = new Rectangle(-1.0, 1.0);
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void testImpossibleToCreateRectangleWithNegativeLargeur(){
		Rectangle r = new Rectangle(1.0, -1.0);
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void testImpossibleToCreateRectangleWithNullLongueur(){
		Rectangle r = new Rectangle(0.0, 1.0);
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void testImpossibleToCreateRectangleWithNullLargeur(){
		Rectangle r = new Rectangle(1.0, 0.0);
	}
}
