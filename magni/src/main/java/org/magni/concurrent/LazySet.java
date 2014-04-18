package org.magni.concurrent;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Callable;

class LazySet<E> extends CallableLazyInitializer<Set<E>> implements Set<E> {

	LazySet(Callable<Set<E>> initializer) {
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

	public Object[] toArray() {
		return get().toArray();
	}

	public <T> T[] toArray(T[] a) {
		return get().toArray(a);
	}

	public boolean add(E e) {
		return get().add(e);
	}

	public boolean remove(Object o) {
		return get().remove(o);
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
