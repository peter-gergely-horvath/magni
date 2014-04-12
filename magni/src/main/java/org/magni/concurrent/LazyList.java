/*
 *   Copyright 2014 Peter G. Horvath
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
package org.magni.concurrent;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import java.util.ListIterator;
import java.util.concurrent.Callable;

/**
 * @author Peter G. Horvath
 *
 */
class LazyList<T> extends CallableLazyInitializer<List<T>> implements List<T> {
	
	LazyList(Callable<List<T>> initializer) {
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

	public Iterator<T> iterator() {
		return get().iterator();
	}

	public Object[] toArray() {
		return get().toArray();
	}

	public <A> A[] toArray(A[] a) {
		return get().toArray(a);
	}

	public boolean add(T e) {
		return get().add(e);
	}

	public boolean remove(Object o) {
		return get().remove(o);
	}

	public boolean containsAll(Collection<?> c) {
		return get().containsAll(c);
	}

	public boolean addAll(Collection<? extends T> c) {
		return get().addAll(c);
	}

	public boolean addAll(int index, Collection<? extends T> c) {
		return get().addAll(index, c);
	}

	public boolean removeAll(Collection<?> c) {
		return get().removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		return get().retainAll(c);
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

	public T get(int index) {
		return get().get(index);
	}

	public T set(int index, T element) {
		return get().set(index, element);
	}

	public void add(int index, T element) {
		get().add(index, element);
	}

	public T remove(int index) {
		return get().remove(index);
	}

	public int indexOf(Object o) {
		return get().indexOf(o);
	}

	public int lastIndexOf(Object o) {
		return get().lastIndexOf(o);
	}

	public ListIterator<T> listIterator() {
		return get().listIterator();
	}

	public ListIterator<T> listIterator(int index) {
		return get().listIterator(index);
	}

	public List<T> subList(int fromIndex, int toIndex) {
		return get().subList(fromIndex, toIndex);
	}
}
