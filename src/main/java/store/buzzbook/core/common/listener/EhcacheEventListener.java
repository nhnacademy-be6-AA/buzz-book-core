package store.buzzbook.core.common.listener;

import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;
import org.ehcache.event.EventType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EhcacheEventListener implements CacheEventListener<Object, Object> {
	@Override
	public void onEvent(CacheEvent<?, ?> cacheEvent) {
		if (cacheEvent.getType() == EventType.CREATED) {
			log.info("Created event: {}", cacheEvent.getKey());
		} else if (cacheEvent.getType() == EventType.UPDATED) {
			log.info("Updated event: {}", cacheEvent.getKey());
		} else if (cacheEvent.getType() == EventType.REMOVED) {
			log.info("Removed event: {}", cacheEvent.getKey());
		} else if (cacheEvent.getType() == EventType.EXPIRED) {
			log.info("Expired event: {}", cacheEvent.getKey());
		}
	}
}
