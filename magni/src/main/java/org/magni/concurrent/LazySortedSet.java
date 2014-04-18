package org.magni.concurrent;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.concurrent.Callable;

class LazySortedSet<E> extends CallableLazyInitializer<SortedSet<E>> implements SortedSet<E> {
	
	LazySortedSet(Callable<SortedSet<E>> initializer) {
		super(initializer);
	}

	public int size() {
		return get().size();
	}

	public boolean isEmpty() {
		return get().isEmpty();
	}

	public boolean contains(Object o) {
		return get().contains(o);
	}

	public Iterator<E> iterator() {
		return get().iterator();
	}

	public Comparator<? super E> comparator() {
		return get().comparator();
	}

	public Object[] toArray() {
		return get().toArray();
	}

	public SortedSet<E> subSet(E fromElement, E toElement) {
		return get().subSet(fromElement, toElement);
	}

	public <T> T[] toArray(T[] a) {
		return get().toArray(a);
	}

	public SortedSet<E> headSet(E toElement) {
		return get().headSet(toElement);
	}

	public boolean add(E e) {
		return get().add(e);
	}

	public SortedSet<E> tailSet(E fromElement) {
		return get().tailSet(fromElement);
	}

	public boolean remove(Object o) {
		return get().remove(o);
	}

	public E first() {
		return get().first();
	}

	public E last() {
		return get().last();
	}

	public boolean containsAll(Collection<?> c) {
		return get().containsAll(c);
	}

	public boolean addAll(Collection<? extends E> c) {
		return get().addAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		return get().retainAll(c);
	}

	public boolean removeAll(Collection<?> c) {
		return get().removeAll(c);
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
