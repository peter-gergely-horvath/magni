/*
 *   Copyright 2013-2016 Peter G. Horvath
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.magni.collect.sealable;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;



/**
 * @author Peter G. Horvath
 *
 */
public class SealableCollections {
	
	private SealableCollections() {
		// no instances allowed
	}
	
	public static <E> SealableCollection<E> sealableCollection(Collection<E> collection) {
		return new SealableCollectionImpl<E>(collection);
	}
	
	public static <E> SealableList<E> sealableList(List<E> list) {
		return new SealableListImpl<E>(list);
	}
	
	public static <K,V> SealableMap<K,V> sealableMap(Map<K,V> map) {
		return new SealableMapImpl<K, V>(map);
	}
	
	public static <E> SealableSet<E> sealableSet(Set<E> set) {
		return new SealableSetImpl<E>(set);
	}
	
	public static <K,V> SealableSortedMap<K, V> sealableSortedMap(SortedMap<K, V> sortedMap) {
		return new SealableSortedMapImpl<K, V>(sortedMap);
	}
	
	public static <E> SealableSortedSet<E> sealableSortedSet(SortedSet<E> sortedSet) {
		return new SealableSortedSetImpl<E>(sortedSet);
	}
	
	private static abstract class SealableContainer<T> implements Sealable {

		
		SealableContainer(T delegate) {
			this.delegate = delegate;
		}
		
		protected volatile T delegate;
		
		private final Object lockObject = new Object();
		protected boolean sealedAlready = false;
		

		/* (non-Javadoc)
		 * @see org.magni.concurrent.collect.sealable.Sealable#seal()
		 */
		public void seal() {
			synchronized (lockObject) {
				if(sealedAlready) {
					throw new IllegalStateException("container has already been sealed");
				}
				delegate = unmodifiableViewOf(delegate);
				sealedAlready = true;
			}
		}

		/**
		 * @param object the object to transform into an unmodifiable object
		 * @return a ready-only view of the passed argument
		 */
		protected abstract T unmodifiableViewOf(T object);
		
	}
	
	private static class SealableCollectionImpl<E> extends SealableContainer<java.util.Collection<E>> implements SealableCollection<E> {

		SealableCollectionImpl(Collection<E> collection) {
			super(collection);
		}
		
		/* (non-Javadoc)
		 * @see org.magni.collect.sealable.SealableCollections.SealableContainer#unmodifiableViewOf(java.lang.Object)
		 */
		@Override
		protected Collection<E> unmodifiableViewOf(Collection<E> collection) {
			return Collections.unmodifiableCollection(collection);
		}

		public int size() {
			return delegate.size();
		}

		public boolean isEmpty() {
			return delegate.isEmpty();
		}

		public boolean contains(Object o) {
			return delegate.contains(o);
		}

		public Iterator<E> iterator() {
			return delegate.iterator();
		}

		public Object[] toArray() {
			return delegate.toArray();
		}

		public <T> T[] toArray(T[] a) {
			return delegate.toArray(a);
		}

		public boolean add(E e) {
			return delegate.add(e);
		}

		public boolean remove(Object o) {
			return delegate.remove(o);
		}

		public boolean containsAll(Collection<?> c) {
			return delegate.containsAll(c);
		}

		public boolean addAll(Collection<? extends E> c) {
			return delegate.addAll(c);
		}

		public boolean removeAll(Collection<?> c) {
			return delegate.removeAll(c);
		}

		public boolean retainAll(Collection<?> c) {
			return delegate.retainAll(c);
		}

		public void clear() {
			delegate.clear();
		}

		public boolean equals(Object o) {
			return delegate.equals(o);
		}

		public int hashCode() {
			return delegate.hashCode();
		}
		
	}
	
	private static class SealableListImpl<E> extends SealableContainer<java.util.List<E>> implements SealableList<E> {

		SealableListImpl(List<E> list) {
			super(list);
		}
		
		/* (non-Javadoc)
		 * @see org.magni.collect.sealable.SealableCollections.SealableContainer#unmodifiableViewOf(java.lang.Object)
		 */
		@Override
		protected List<E> unmodifiableViewOf(List<E> list) {
			return Collections.unmodifiableList(list);
		}
		

		public int size() {
			return delegate.size();
		}

		public boolean isEmpty() {
			return delegate.isEmpty();
		}

		public boolean contains(Object o) {
			return delegate.contains(o);
		}

		public Iterator<E> iterator() {
			return delegate.iterator();
		}

		public Object[] toArray() {
			return delegate.toArray();
		}

		public <T> T[] toArray(T[] a) {
			return delegate.toArray(a);
		}

		public boolean add(E e) {
			return delegate.add(e);
		}

		public boolean remove(Object o) {
			return delegate.remove(o);
		}

		public boolean containsAll(Collection<?> c) {
			return delegate.containsAll(c);
		}

		public boolean addAll(Collection<? extends E> c) {
			return delegate.addAll(c);
		}

		public boolean addAll(int index, Collection<? extends E> c) {
			return delegate.addAll(index, c);
		}

		public boolean removeAll(Collection<?> c) {
			return delegate.removeAll(c);
		}

		public boolean retainAll(Collection<?> c) {
			return delegate.retainAll(c);
		}

		public void clear() {
			delegate.clear();
		}

		public boolean equals(Object o) {
			return delegate.equals(o);
		}

		public int hashCode() {
			return delegate.hashCode();
		}

		public E get(int index) {
			return delegate.get(index);
		}

		public E set(int index, E element) {
			return delegate.set(index, element);
		}

		public void add(int index, E element) {
			delegate.add(index, element);
		}

		public E remove(int index) {
			return delegate.remove(index);
		}

		public int indexOf(Object o) {
			return delegate.indexOf(o);
		}

		public int lastIndexOf(Object o) {
			return delegate.lastIndexOf(o);
		}

		public ListIterator<E> listIterator() {
			return delegate.listIterator();
		}

		public ListIterator<E> listIterator(int index) {
			return delegate.listIterator(index);
		}

		public List<E> subList(int fromIndex, int toIndex) {
			return delegate.subList(fromIndex, toIndex);
		}
		
	}
	
	private static class SealableMapImpl<K,V> extends SealableContainer<java.util.Map<K,V>> implements SealableMap<K,V> {

		SealableMapImpl(Map<K, V> delegate) {
			super(delegate);
		}
		
		/* (non-Javadoc)
		 * @see org.magni.collect.sealable.SealableCollections.SealableContainer#unmodifiableViewOf(java.lang.Object)
		 */
		@Override
		protected Map<K, V> unmodifiableViewOf(Map<K, V> map) {
			return Collections.unmodifiableMap(map);
		}
		
		public int size() {
			return delegate.size();
		}

		public boolean isEmpty() {
			return delegate.isEmpty();
		}

		public boolean containsKey(Object key) {
			return delegate.containsKey(key);
		}

		public boolean containsValue(Object value) {
			return delegate.containsValue(value);
		}

		public V get(Object key) {
			return delegate.get(key);
		}

		public V put(K key, V value) {
			return delegate.put(key, value);
		}

		public V remove(Object key) {
			return delegate.remove(key);
		}

		public void putAll(Map<? extends K, ? extends V> m) {
			delegate.putAll(m);
		}

		public void clear() {
			delegate.clear();
		}

		public Set<K> keySet() {
			return delegate.keySet();
		}

		public Collection<V> values() {
			return delegate.values();
		}

		public Set<java.util.Map.Entry<K, V>> entrySet() {
			return delegate.entrySet();
		}

		public boolean equals(Object o) {
			return delegate.equals(o);
		}

		public int hashCode() {
			return delegate.hashCode();
		}
		
	}
	
	private static class SealableSetImpl<E> extends SealableContainer<java.util.Set<E>> implements SealableSet<E> {
		

		SealableSetImpl(Set<E> set) {
			super(set);
		}
		
		/* (non-Javadoc)
		 * @see org.magni.concurrent.collect.sealable.SealableContainer#unmodifiableViewOf(java.lang.Object)
		 */
		@Override
		protected Set<E> unmodifiableViewOf(Set<E> set) {
			return Collections.unmodifiableSet(set);
		}
		
		
		public int size() {
			return delegate.size();
		}

		public boolean isEmpty() {
			return delegate.isEmpty();
		}

		public boolean contains(Object o) {
			return delegate.contains(o);
		}

		public Iterator<E> iterator() {
			return delegate.iterator();
		}

		public Object[] toArray() {
			return delegate.toArray();
		}

		public <T> T[] toArray(T[] a) {
			return delegate.toArray(a);
		}

		public boolean add(E e) {
			return delegate.add(e);
		}

		public boolean remove(Object o) {
			return delegate.remove(o);
		}

		public boolean containsAll(Collection<?> c) {
			return delegate.containsAll(c);
		}

		public boolean addAll(Collection<? extends E> c) {
			return delegate.addAll(c);
		}

		public boolean retainAll(Collection<?> c) {
			return delegate.retainAll(c);
		}

		public boolean removeAll(Collection<?> c) {
			return delegate.removeAll(c);
		}

		public void clear() {
			delegate.clear();
		}

		public boolean equals(Object o) {
			return delegate.equals(o);
		}

		public int hashCode() {
			return delegate.hashCode();
		}
	}
	
	private static class SealableSortedMapImpl<K,V> extends SealableContainer<java.util.SortedMap<K, V>> implements SealableSortedMap<K, V> {

		SealableSortedMapImpl(SortedMap<K, V> delegate) {
			super(delegate);
		}
		
		/* (non-Javadoc)
		 * @see org.magni.collect.sealable.SealableCollections.SealableContainer#unmodifiableViewOf(java.lang.Object)
		 */
		@Override
		protected SortedMap<K, V> unmodifiableViewOf(SortedMap<K, V> sortedMap) {
			return Collections.unmodifiableSortedMap(sortedMap);
		}
		
		protected volatile java.util.SortedMap<K, V> delegate;

		public Comparator<? super K> comparator() {
			return delegate.comparator();
		}

		public SortedMap<K, V> subMap(K fromKey, K toKey) {
			return delegate.subMap(fromKey, toKey);
		}

		public int size() {
			return delegate.size();
		}

		public boolean isEmpty() {
			return delegate.isEmpty();
		}

		public boolean containsKey(Object key) {
			return delegate.containsKey(key);
		}

		public boolean containsValue(Object value) {
			return delegate.containsValue(value);
		}

		public SortedMap<K, V> headMap(K toKey) {
			return delegate.headMap(toKey);
		}

		public V get(Object key) {
			return delegate.get(key);
		}

		public SortedMap<K, V> tailMap(K fromKey) {
			return delegate.tailMap(fromKey);
		}

		public V put(K key, V value) {
			return delegate.put(key, value);
		}

		public K firstKey() {
			return delegate.firstKey();
		}

		public K lastKey() {
			return delegate.lastKey();
		}

		public Set<K> keySet() {
			return delegate.keySet();
		}

		public V remove(Object key) {
			return delegate.remove(key);
		}

		public Collection<V> values() {
			return delegate.values();
		}

		public Set<java.util.Map.Entry<K, V>> entrySet() {
			return delegate.entrySet();
		}

		public void putAll(Map<? extends K, ? extends V> m) {
			delegate.putAll(m);
		}

		public void clear() {
			delegate.clear();
		}

		public boolean equals(Object o) {
			return delegate.equals(o);
		}

		public int hashCode() {
			return delegate.hashCode();
		}
		
	}
	
	private static class SealableSortedSetImpl<E> extends SealableContainer<java.util.SortedSet<E>> implements SealableSortedSet<E> {

		SealableSortedSetImpl(SortedSet<E> sortedSet) {
			super(sortedSet);
		}
		
		/* (non-Javadoc)
		 * @see org.magni.collect.sealable.SealableCollections.SealableContainer#unmodifiableViewOf(java.lang.Object)
		 */
		@Override
		protected SortedSet<E> unmodifiableViewOf(SortedSet<E> sortedSet) {
			return Collections.unmodifiableSortedSet(sortedSet);
		}

		public int size() {
			return delegate.size();
		}

		public boolean isEmpty() {
			return delegate.isEmpty();
		}

		public boolean contains(Object o) {
			return delegate.contains(o);
		}

		public Iterator<E> iterator() {
			return delegate.iterator();
		}

		public Comparator<? super E> comparator() {
			return delegate.comparator();
		}

		public Object[] toArray() {
			return delegate.toArray();
		}

		public SortedSet<E> subSet(E fromElement, E toElement) {
			return delegate.subSet(fromElement, toElement);
		}

		public <T> T[] toArray(T[] a) {
			return delegate.toArray(a);
		}

		public SortedSet<E> headSet(E toElement) {
			return delegate.headSet(toElement);
		}

		public boolean add(E e) {
			return delegate.add(e);
		}

		public SortedSet<E> tailSet(E fromElement) {
			return delegate.tailSet(fromElement);
		}

		public boolean remove(Object o) {
			return delegate.remove(o);
		}

		public E first() {
			return delegate.first();
		}

		public E last() {
			return delegate.last();
		}

		public boolean containsAll(Collection<?> c) {
			return delegate.containsAll(c);
		}

		public boolean addAll(Collection<? extends E> c) {
			return delegate.addAll(c);
		}

		public boolean retainAll(Collection<?> c) {
			return delegate.retainAll(c);
		}

		public boolean removeAll(Collection<?> c) {
			return delegate.removeAll(c);
		}

		public void clear() {
			delegate.clear();
		}

		public boolean equals(Object o) {
			return delegate.equals(o);
		}

		public int hashCode() {
			return delegate.hashCode();
		}
	}


}
