package be.fabrice.validation;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class SimpleEntity {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	@Column(name="NNV",nullable=false)
	private Integer notNullValue;
	@Column(name="UV",unique=true)
	private Integer uniqueValue;
	@Column(name="NUV",unique=false)
	private Integer nonUniqueValue;
	@Column(name="SLLV",length=6)
	private String smallLengthLimitedValue;
	@Column(name="LLLV",length=25)
	private String longLengthLimitedValue;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getNotNullValue() {
		return notNullValue;
	}
	public void setNotNullValue(Integer notNullValue) {
		this.notNullValue = notNullValue;
	}
	public Integer getUniqueValue() {
		return uniqueValue;
	}
	public void setUniqueValue(Integer uniqueValue) {
		this.uniqueValue = uniqueValue;
	}
	public Integer getNonUniqueValue() {
		return nonUniqueValue;
	}
	public void setNonUniqueValue(Integer nonUniqueValue) {
		this.nonUniqueValue = nonUniqueValue;
	}
	public String getSmallLengthLimitedValue() {
		return smallLengthLimitedValue;
	}
	public void setSmallLengthLimitedValue(String smallLengthLimitedValue) {
		this.smallLengthLimitedValue = smallLengthLimitedValue;
	}
	public String getLongLengthLimitedValue() {
		return longLengthLimitedValue;
	}
	public void setLongLengthLimitedValue(String longLengthLimitedValue) {
		this.longLengthLimitedValue = longLengthLimitedValue;
	}
}
