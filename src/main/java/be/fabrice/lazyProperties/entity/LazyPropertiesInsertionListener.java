package be.fabrice.lazyProperties.entity;

import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PostInsertEventListener;

public class LazyPropertiesInsertionListener implements PostInsertEventListener{
	@Override
	public void onPostInsert(PostInsertEvent event) {
		if(event.getEntity() instanceof LazyInitializable<?>){
			LazyInitializable<?> entity = (LazyInitializable<?>) event.getEntity();
			LazyProperties lazy = entity.getLazyProperties();
			lazy.setPrimaryKey(event.getId());
			event.getSession().update(lazy);
		}
	}
}
