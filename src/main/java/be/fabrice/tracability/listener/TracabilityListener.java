package be.fabrice.tracability.listener;

import java.sql.Timestamp;
import java.util.Date;

import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PostInsertEventListener;
import org.hibernate.event.PostUpdateEvent;
import org.hibernate.event.PostUpdateEventListener;

import be.fabrice.tracability.entity.Tracable;
import be.fabrice.tracability.entity.Trace;

public class TracabilityListener implements PostUpdateEventListener,PostInsertEventListener{

	@Override
	public void onPostUpdate(PostUpdateEvent event) {
		if(event.getEntity() instanceof Tracable<?>){
			Trace trace =((Tracable<?>) event.getEntity()).getTrace(); 
			trace.setUpdateUserId(1000);
			trace.setModifyingTime(new Timestamp(new Date().getTime()));
		}
	}

	@Override
	public void onPostInsert(PostInsertEvent event) {
		if(event.getEntity() instanceof Tracable<?>){
			Tracable<?> entity = (Tracable<?>) event.getEntity();
			Trace trace = entity.createNewTrace((Integer)event.getId());
			trace.setUpdateUserId(1000);
			trace.setModifyingTime(new Timestamp(new Date().getTime()));
			
			event.getSession().update(trace);
		}
	}
}
