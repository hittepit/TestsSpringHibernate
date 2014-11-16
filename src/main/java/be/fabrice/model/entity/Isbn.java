package be.fabrice.model.entity;

import org.apache.commons.validator.routines.ISBNValidator;

public class Isbn {
	private String code;
	
	public Isbn(String code){
		if(! validate(code)){
			throw new IllegalArgumentException(code + "is incorrect ISBN");
		}
		
		this.code = new ISBNValidator(true).validate(code);
	}
	
	public Isbn(Isbn isbn){
		if(isbn==null){
			throw new IllegalArgumentException("isbn argument cannot be null");
		}
		this.code =isbn.getValue();
	}
	
	public static boolean validate(String code){
		ISBNValidator validator = new ISBNValidator(true);
		return validator.isValid(code);
	}
	
	public String getValue(){
		return this.code;
	}
}
