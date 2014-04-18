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
package org.magni.concurrent;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import javassist.util.proxy.MethodHandler;

/**
 * @author Peter G. Horvath
 *
 */
public class ConcurrencyUtils {

	private ConcurrencyUtils() {
		// static utility class - no instances allowed
	}
	
	/**
	 * Creates a {@link LazyInitializer} that uses the supplied {@code Callable}
	 * to initialize its value.
	 * 
	 * @param initializerCallable the {@code Callable} to initialize the value from  
	 * @return a {@link LazyInitializer} that uses the supplied {@code Callable}
	 * to initialize its value.
	 * 
	 * @throws NullPointerException if initializerCallable is {@code null}
	 */
	public static <T> Lazy.Initializer<T> lazyInitializer(Callable<T> initializerCallable) {
		return new CallableLazyInitializer<T>(initializerCallable);
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
	
	public static <T> T threadConfinementGuardian(T object)
			throws ProxyCreationFailedException {
	
		try {
	
			javassist.util.proxy.ProxyFactory factory = new javassist.util.proxy.ProxyFactory();
			factory.setSuperclass(object.getClass());
	
			factory.setFilter(new JavassistMethodFilterAdapter(IGNORE_FINALIZE_PROXYMETHODFILTER));
	
			@SuppressWarnings("unchecked")
			Class<T> proxyObjectClass = factory.createClass();
			T proxyObj;
	
			proxyObj = proxyObjectClass.newInstance();
	
			MethodHandler p = new ThreadConfinementGuardianProxy(object);
			((javassist.util.proxy.ProxyObject) proxyObj).setHandler(p);
	
			return proxyObj;
	
		} catch (Exception e) {
			throw new ProxyCreationFailedException(
					"Failed to create the proxy object", e);
		}
	}
	
	static class JavassistMethodFilterAdapter implements javassist.util.proxy.MethodFilter {
		
		private final ProxyMethodFilter delegate;
		
		JavassistMethodFilterAdapter(ProxyMethodFilter delegate) {
			this.delegate = delegate;
		}
	
		/* (non-Javadoc)
		 * @see javassist.util.proxy.MethodFilter#isHandled(java.lang.reflect.Method)
		 */
		public boolean isHandled(Method method) {
			return delegate.isHandled(method);
		}
	
	}

	private static final ProxyMethodFilter IGNORE_FINALIZE_PROXYMETHODFILTER = 
			new ProxyMethodFilter() {
	
				public boolean isHandled(Method m) {
					// ignore java.lang.Object.finalize() method
					return ! (m.getName().equals("finalize") && 
							 m.getParameterTypes().length == 0);
				}
		
	};
	
	

}
