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


/**
 * @author Peter G. Horvath
 *
 */
public interface Sealable {
	
	/**
	 * <p>
	 * Seals this object allowing now further changes.</p>
	 * 
	 * <p>
	 * Implementors are mandated to provide thread-safe 
	 * behavior: after this method is invoked, any
	 * attempt to mutate the state of this object 
	 * (regardless of the thread from which 
	 * it was originated from) MUST fail with either
	 * {@code java.lang.UnsupportedOperationException}
	 * or {@code java.lang.IllegalStateException}
	 * being thrown.   
	 * </p>
	 * 
	 * <p>
	 * Calling this method more than one time is
	 * illegal. Implementors MUST throw 
	 * {@code IllegalStateException} in case client
	 * code attempts to call it multiple times.
	 * Implementations are mandated to be thread-safe:
	 * if more than one thread attempt to call
	 * this method in the same time, precisely one 
	 * should succeed and {@code IllegalStateException} 
	 * MUST be raised for all other invocations.
	 * </p>
	 * 
	 * @throws IllegalStateException if {@code seal()} has already been called before on this instance
	 */
	public void seal();

}
