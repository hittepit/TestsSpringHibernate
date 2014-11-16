package be.fabrice.tracability.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Trace {
	@Id
	private Integer id;
	@Column(name="UPDATE_USER_ID",nullable=true)
	private Integer updateUserId;
	@Column(name="UPDATE_TIME",nullable=true)
	private Timestamp modifyingTime;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getUpdateUserId() {
		return updateUserId;
	}
	public void setUpdateUserId(Integer updateUserId) {
		this.updateUserId = updateUserId;
	}
	public Timestamp getModifyingTime() {
		return modifyingTime;
	}
	public void setModifyingTime(Timestamp modifyingTime) {
		this.modifyingTime = modifyingTime;
	}
}
