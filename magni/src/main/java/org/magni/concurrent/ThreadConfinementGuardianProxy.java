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
package org.magni.concurrent;

import java.lang.reflect.Method;

import javassist.util.proxy.MethodHandler;

/**
 * @author Peter G. Horvath
 *
 */
class ThreadConfinementGuardianProxy implements MethodHandler {

	private final Object target;
	private final Thread confinedToThread;

	ThreadConfinementGuardianProxy(Object target) {
		this.target = target;
		confinedToThread = Thread.currentThread();
	}
	
	
	/* (non-Javadoc)
	 * @see javassist.util.proxy.MethodHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.reflect.Method, java.lang.Object[])
	 */
	public Object invoke(Object self, Method overridden, Method forwarder,
			Object[] args) throws Throwable {
		
		if(Thread.currentThread() != confinedToThread) {
			throw new ThreadConfinementViolationException(
						"Illegal access from thread '" +
						Thread.currentThread() +
						"': object should only be accessed from '" + 
						confinedToThread +"'");
		}
		
		return overridden.invoke(target, args);
	}

}
