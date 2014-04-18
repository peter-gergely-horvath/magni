package org.magni.concurrent;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.Callable;

class LazySortedMap<K,V> extends CallableLazyInitializer<SortedMap<K,V>> implements SortedMap<K,V> {
	
	LazySortedMap(Callable<SortedMap<K, V>> initializer) {
		super(initializer);
	}

	public Comparator<? super K> comparator() {
		return get().comparator();
	}

	public SortedMap<K, V> subMap(K fromKey, K toKey) {
		return get().subMap(fromKey, toKey);
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

	public SortedMap<K, V> headMap(K toKey) {
		return get().headMap(toKey);
	}

	public V get(Object key) {
		return get().get(key);
	}

	public SortedMap<K, V> tailMap(K fromKey) {
		return get().tailMap(fromKey);
	}

	public V put(K key, V value) {
		return get().put(key, value);
	}

	public K firstKey() {
		return get().firstKey();
	}

	public K lastKey() {
		return get().lastKey();
	}

	public Set<K> keySet() {
		return get().keySet();
	}

	public V remove(Object key) {
		return get().remove(key);
	}

	public Collection<V> values() {
		return get().values();
	}

	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return get().entrySet();
	}

	public void putAll(Map<? extends K, ? extends V> m) {
		get().putAll(m);
	}

	public void clear() {
		get().clear();
	}

	public boolean equals(Object o) {
		return get().equals(o);
	}

	public int hashCode() {
		return get().hashCode();
	}

}
