package com.madhav.pokedex.pokemon;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

public class InMemoryCache<K, V> {

    private final long ttlMillis;
    private final int maxSize;

    private final Map<K, CacheEntry<V>> store;

    public InMemoryCache(long ttlMillis, int maxSize) {
        this.ttlMillis = ttlMillis;
        this.maxSize = maxSize;

        this.store = new LinkedHashMap<>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, CacheEntry<V>> eldest) {
                return size() > InMemoryCache.this.maxSize;
            }
        };
    }

    public synchronized V get(K key) {
        CacheEntry<V> entry = store.get(key);
        if (entry == null) {
            return null;
        }
        if (isExpired(entry)) {
            store.remove(key);
            return null;
        }
        return entry.value();
    }

    public synchronized void put(K key, V value) {
        store.put(key, new CacheEntry<>(value, Instant.now().toEpochMilli()));
    }

    private boolean isExpired(CacheEntry<V> entry) {
        long now = Instant.now().toEpochMilli();
        return now - entry.createdAtMillis() > ttlMillis;
    }

    private record CacheEntry<V>(V value, long createdAtMillis) { }
}
