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

/**
 * A variable container that defers initialization until the first time
 * {@link #get()} is invoked.
 * 
 */
public abstract class LazyInitializer<T> {

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
