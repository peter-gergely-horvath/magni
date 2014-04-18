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
import java.util.concurrent.Callable;

/**
 * @author Peter G. Horvath
 * 
 */
public class Lazy {

	public static <T> LazyInitializer<T> initializer(Callable<T> initializer) {
		return new CallableLazyInitializer<T>(initializer);
	}

	public static <E> List<E> list(Callable<List<E>> initializerCallable) {
		
		return new LazyList<E>(initializerCallable);
	}
	
	public static <E> List<E> listWithRandomAccessSupport(Callable<List<E>> initializerCallable) {
		
		return new RandomAccessLazyList<E>(initializerCallable);
	}
	
	

	public static <K, V> Map<K, V> map(Callable<Map<K, V>> initializerCallable) {
		
		return new LazyMap<K, V>(initializerCallable);
	}

}
