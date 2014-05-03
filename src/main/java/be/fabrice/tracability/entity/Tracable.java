package be.fabrice.tracability.entity;

import java.lang.reflect.ParameterizedType;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

@MappedSuperclass
public abstract class Tracable<T extends Trace> {
	@Transient
	private Class<T> persistentClass;
	
	{
		persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id",insertable=false,updatable=false)
	private T trace;

	public Trace getTrace() {
		return trace;
	}
	
	public Trace createNewTrace(Integer id){
		try {
			trace = persistentClass.newInstance();
			trace.setId(id);
			return trace;
		} catch (InstantiationException e) {
			throw new RuntimeException("Bad config",e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Bad config",e);
		}
	}
}
