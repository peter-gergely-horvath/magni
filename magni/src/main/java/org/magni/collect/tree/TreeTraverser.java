/*
 *   Copyright 2013 Peter G. Horvath
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
package org.magni.collect.tree;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author Peter G. Horvath
 *
 */
public abstract class TreeTraverser<E> {
	
	protected abstract Iterable<E> getChildrenOf(E parent);
	
	
	public Iterable<E> depthFirst(final E root) {
		checkRootIsNotNull(root);
		
		return new Iterable<E>() {

			public Iterator<E> iterator() {
				return new Iterator<E>() {
					
					@SuppressWarnings("unchecked")
					private LinkedList<E> nodes = new LinkedList<E>(Arrays.<E>asList(root));
					
					public boolean hasNext() {
						return !nodes.isEmpty();
					}
				
					public E next() {
						E nextNode = nodes.removeFirst();
						LinkedList<E> linkedList = new LinkedList<E>();
						
						for (E e : getChildrenOf(nextNode)) {
							linkedList.add(e);
						}
						
						linkedList.addAll(nodes);
						nodes = linkedList;
						return nextNode;
					}
				
					public void remove() {
						throw new UnsupportedOperationException();
					}
				
				};
			}
			
		};
		
	}
	
	public Iterable<E> breadthFirst(final E root) {
		checkRootIsNotNull(root);
		
		return new Iterable<E>() {

			public Iterator<E> iterator() {
				return new Iterator<E>() {
					
					@SuppressWarnings("unchecked")
					private LinkedList<E> nodes = new LinkedList<E>(Arrays.<E>asList(root));
					
					public boolean hasNext() {
						return !nodes.isEmpty();
					}
				
					public E next() {
						E nextNode = nodes.removeFirst();
						for (E e : getChildrenOf(nextNode)) {
							nodes.addLast(e);
						}
						return nextNode;
					}
				
					public void remove() {
						throw new UnsupportedOperationException();
					}
				
				};
			}
			
		};
	}


	private static void checkRootIsNotNull(Object root) {
		if(root == null) {
			throw new NullPointerException("root node cannot be null");
		}
	}

}
