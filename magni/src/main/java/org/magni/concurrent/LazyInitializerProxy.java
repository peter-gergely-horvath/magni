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
import java.util.concurrent.Callable;

import javassist.util.proxy.MethodHandler;

class LazyInitializerProxy<T> extends CallableLazyInitializer<T>
		implements MethodHandler {

	LazyInitializerProxy(Callable<T> initializerCallable) {
		super(initializerCallable);
	}

	public Object invoke(Object self, Method overridden, Method forwarder,
			Object[] args) throws Throwable {
		
		return overridden.invoke(get(), args);
	}
}