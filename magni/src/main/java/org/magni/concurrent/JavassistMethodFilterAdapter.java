package org.magni.concurrent;

import java.lang.reflect.Method;

class JavassistMethodFilterAdapter implements javassist.util.proxy.MethodFilter {

	private final ProxyMethodFilter delegate;

	JavassistMethodFilterAdapter(ProxyMethodFilter delegate) {
		if(delegate == null) throw new NullPointerException("Argument 'delegate' cannot be null");
		this.delegate = delegate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javassist.util.proxy.MethodFilter#isHandled(java.lang.reflect.Method)
	 */
	public boolean isHandled(Method method) {
		return delegate.isHandled(method);
	}

}
