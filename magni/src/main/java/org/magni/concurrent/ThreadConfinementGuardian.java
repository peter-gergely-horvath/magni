package org.magni.concurrent;

public class ThreadConfinementGuardian {
	
	private ThreadConfinementGuardian() {
		throw new AssertionError(ThreadConfinementGuardian.class + " is a static utility class, no instances allowed!");
	}
	
	public static <T> T create(T object)
			throws ProxyCreationFailedException {
	
		try {
	
			javassist.util.proxy.ProxyFactory factory = new javassist.util.proxy.ProxyFactory();
			factory.setSuperclass(object.getClass());
	
			factory.setFilter(new JavassistMethodFilterAdapter(Lazy.IGNORE_FINALIZE_PROXYMETHODFILTER));
			
	
			@SuppressWarnings("unchecked")
			Class<T> proxyObjectClass = factory.createClass();
			T proxyObj = proxyObjectClass.newInstance();
	
			ThreadConfinementGuardianProxy handler = new ThreadConfinementGuardianProxy(object);
			((javassist.util.proxy.ProxyObject) proxyObj).setHandler(handler);
	
			return proxyObj;
	
		} catch (Exception e) {
			throw new ProxyCreationFailedException(
					"Failed to create the proxy object", e);
		}
	}
	
	
	

}
