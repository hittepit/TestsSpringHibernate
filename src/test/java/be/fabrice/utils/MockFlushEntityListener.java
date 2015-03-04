package be.fabrice.utils;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.event.FlushEntityEvent;
import org.hibernate.event.def.DefaultFlushEntityEventListener;

public class MockFlushEntityListener extends DefaultFlushEntityEventListener {
	private int invocation = 0;
	private List<Class<?>> entityClassFlushed = new ArrayList<Class<?>>();
	
	public void onFlushEntity(FlushEntityEvent event)  throws HibernateException {
		invocation+=1;
		entityClassFlushed.add(event.getEntity().getClass());
		super.onFlushEntity(event);
	}
	
	public int getInvocation() {
		return invocation;
	}
	
	public List<Class<?>> getEntityClassFlushed() {
		return entityClassFlushed;
	}
	
	public void resetInvocation(){
		this.entityClassFlushed = new ArrayList<Class<?>>();
		this.invocation = 0;
	}
}
