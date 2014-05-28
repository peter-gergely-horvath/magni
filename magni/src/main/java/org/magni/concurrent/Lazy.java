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

import java.lang.reflect.Method;
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
	
	/**
	 * A variable container that defers initialization until the first time
	 * {@link #get()} is invoked.
	 * 
	 */
	public static abstract class Initializer<T> {

		private final Object lockObject = new Object();

		private volatile T value;

		/**
		 * <p>
		 * Returns the value contained in this LazyInitializer.
		 * </p>
		 * <p>
		 * Initialization is performed on the first invocation of the method, the
		 * value established in {@link #initializeValue()} and is stored 
		 * internally; all subsequent invocations return the same value without
		 * {@link #initializeValue()} being called again.
		 * </p>
		 * 
		 * @see {@link #initializeValue()}
		 * 
		 * @return the value contained in this LazyInitializer.
		 */
		public final T get() {
			
	        T valueToReturn = value;
	        if (valueToReturn == null) {
	            synchronized(lockObject) {
	                valueToReturn = value;
	                if (valueToReturn == null) {
	                	value = valueToReturn = initializeValue();
	                }
	            }
	        }
	        return valueToReturn;
		}

		/**
		 * <p>
		 * Returns the initialized value for this LazyInitializer.
		 * </p>
		 * <p>
		 * This method will be invoked the first time a the variable is accessed
		 * with the {@link #get} method. <b>Normally, this method is invoked at most
		 * once</b>, but in case it throws an exception, it will be called again on
		 * subsequent invocations of {@link #get}. (In other words, all
		 * {@code Throwable} thrown from this method will be considered as
		 * recoverable, thus lazy-initialization will be attempted again the next
		 * time {@link #get()} is called)
		 * </p>
		 * 
		 * @return the initialized value for this LazyInitializer.
		 */
		protected abstract T initializeValue();

	}
	
	/**
	 * Private constructor to prevent instantiation: static utility class 
	 */
	private Lazy() {
		// static utility class - no instances allowed
	}

	/**
	 * Creates a {@link LazyInitializer} that uses the supplied {@code Callable}
	 * to initialize its value.
	 * 
	 * @param initializer the {@code Callable} to initialize the value from  
	 * @return a {@link Lazy.Initializer} that uses the supplied {@code Callable}
	 * to initialize its value.
	 * 
	 * @throws NullPointerException if initializer is {@code null}
	 */
	public static <T> Lazy.Initializer<T> initializer(Callable<T> initializer) {
		return new CallableLazyInitializer<T>(initializer);
	}
	
	/**
	 * <p>
	 * Creates a proxy object for the specified 
	 * {@code Class}, which lazy-initializes the 
	 * target object using the supplied 
	 * {@code Callable} on the first method
	 * invocation (except {@code java.lang.Object.finalize()})
	 * performed on the proxy.</p>
	 * 
	 * <p>
	 * All method invocations except 
	 * {@code java.lang.Object.finalize()} are
	 * delegated to the target object (and 
	 * thus cause the target object to be 
	 * initialized).</p>
	 * 
	 * @param targetClass the class to proxy
	 * @param initializerCallable the {@code Callable} that initializes the underlying object 
	 * 
	 * @return a proxy object that which lazy-initializes the target on the 
	 * 	first method invocation using the supplied {@code Callable}
	 * 
	 * @throws ProxyCreationFailedException in case the proxy object could not be created
	 */
	public static <T> T lazyInitializerProxy(Class<T> targetClass,
			Callable<T> initializerCallable) {
		return lazyInitializerProxy(targetClass, IGNORE_FINALIZE_PROXYMETHODFILTER, initializerCallable);
	}
	

	/**
	 * Creates a proxy object for the specified 
	 * {@code Class}, which lazy-initializes the 
	 * target object using the supplied 
	 * {@code Callable} on the first method
	 * invocation performed on the proxy. 
	 * 
	 * 
	 * @param targetClass the class to proxy
	 * @param methodFilter a filter which decides whether a method should be handled by the proxy or not
	 * @param initializerCallable the {@code Callable} that initializes the underlying object 
	 * 
	 * @return a proxy object that which lazy-initializes the target on the 
	 * 	first method invocation using the supplied {@code Callable}
	 * 
	 * @throws ProxyCreationFailedException in case the proxy object could not be created
	 */
	public static <T> T lazyInitializerProxy(Class<T> targetClass,
			ProxyMethodFilter methodFilter, Callable<T> initializerCallable)
			throws ProxyCreationFailedException {
	
		try {
	
			javassist.util.proxy.ProxyFactory factory = new javassist.util.proxy.ProxyFactory();
			factory.setSuperclass(targetClass);
	
			if (methodFilter != null) {
				factory.setFilter(new JavassistMethodFilterAdapter(methodFilter));
			}
	
			@SuppressWarnings("unchecked")
			Class<T> proxyObjectClass = factory.createClass();
			T proxyObj = proxyObjectClass.newInstance();
	
			LazyInitializerProxy<T> lp = new LazyInitializerProxy<T>(initializerCallable);
			((javassist.util.proxy.ProxyObject) proxyObj).setHandler(lp);
	
			return proxyObj;
	
		} catch (Exception e) {
			throw new ProxyCreationFailedException(
					"Failed to create the proxy object", e);
		}
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
	
	static final ProxyMethodFilter IGNORE_FINALIZE_PROXYMETHODFILTER = 
			new ProxyMethodFilter() {
	
				public boolean isHandled(Method m) {
					// ignore java.lang.Object.finalize() method
					return ! (m.getName().equals("finalize") && 
							 m.getParameterTypes().length == 0);
				}
		
	};

}
