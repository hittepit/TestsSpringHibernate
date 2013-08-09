package be.fabrice.flush.dao;

import org.hibernate.HibernateException;
import org.hibernate.event.FlushEvent;
import org.hibernate.event.def.DefaultFlushEventListener;
import org.springframework.stereotype.Component;

@Component("mockSessionFlushListener")
public class MockSessionFlushListener extends DefaultFlushEventListener {
	private int invocation = 0;

	@Override
	public void onFlush(FlushEvent event) throws HibernateException {
		invocation+=1;
		super.onFlush(event);
	}
	
	public int getInvocation() {
		return invocation;
	}
	
	public void resetInvocation(){
		this.invocation = 0;
	}
}
