package org.magni.concurrent;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

class LazyMap<K,V> extends CallableLazyInitializer<Map<K,V>> implements Map<K,V> {
	
	LazyMap(Callable<Map<K, V>> initializer) {
		super(initializer);
	}

	public int size() {
		return get().size();
	}

	public boolean isEmpty() {
		return get().isEmpty();
	}

	public boolean containsKey(Object key) {
		return get().containsKey(key);
	}

	public boolean containsValue(Object value) {
		return get().containsValue(value);
	}

	public V get(Object key) {
		return get().get(key);
	}

	public V put(K key, V value) {
		return get().put(key, value);
	}

	public V remove(Object key) {
		return get().remove(key);
	}

	public void putAll(Map<? extends K, ? extends V> m) {
		get().putAll(m);
	}

	public void clear() {
		get().clear();
	}

	public Set<K> keySet() {
		return get().keySet();
	}

	public Collection<V> values() {
		return get().values();
	}

	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return get().entrySet();
	}

	public boolean equals(Object o) {
		return get().equals(o);
	}

	public int hashCode() {
		return get().hashCode();
	}
}
