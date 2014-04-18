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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.concurrent.Callable;

/**
 * @author Peter G. Horvath
 * 
 */
public class Lazy {

	public static <T> LazyInitializer<T> initializer(Callable<T> initializer) {
		return new CallableLazyInitializer<T>(initializer);
	}

	public static <E> List<E> list(Callable<List<E>> initializer) {

		return new LazyList<E>(initializer);
	}

	public static <E> List<E> listWithRandomAccessSupport(
			Callable<List<E>> initializer) {

		return new RandomAccessLazyList<E>(initializer);
	}

	public static <K, V> Map<K, V> map(Callable<Map<K, V>> initializer) {

		return new LazyMap<K, V>(initializer);
	}

	public static <E> Set<E> set(Callable<Set<E>> initializer) {

		return new LazySet<E>(initializer);
	}

	public static <K, V> SortedMap<K, V> sortedMap(
			Callable<SortedMap<K, V>> initializer) {
		return new LazySortedMap<K, V>(initializer);
	}

	public static <E> SortedSet<E> sortedSet(Callable<SortedSet<E>> initializer) {
		return new LazySortedSet<E>(initializer);
	}

}
