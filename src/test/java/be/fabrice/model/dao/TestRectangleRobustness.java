package be.fabrice.model.dao;

import org.testng.annotations.Test;

import be.fabrice.model.entity.RectangleV1;

@Test(suiteName="Modèles robustes", testName="Robustesse d'une entité", description="Test sur l'utilisation de modèles robustes avec Hibernate")
public class TestRectangleRobustness {
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void testImpossibleToCreateRectangleWithNegativeLongueur(){
		new RectangleV1(-1.0, 1.0);
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void testImpossibleToCreateRectangleWithNegativeLargeur(){
		new RectangleV1(1.0, -1.0);
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void testImpossibleToCreateRectangleWithNullLongueur(){
		new RectangleV1(0.0, 1.0);
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void testImpossibleToCreateRectangleWithNullLargeur(){
		new RectangleV1(1.0, 0.0);
	}
}
