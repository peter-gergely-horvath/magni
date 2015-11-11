# Lazy initialization proxy support #

`org.magni.concurrent.ConcurrencyUtils.lazyInitializerProxy(Class<T>, Callable<T>)` creates a proxy object for the specified `Class` which lazy-initializes the target object using the supplied `Callable` on the first method invocation (except `java.lang.Object.finalize()`) performed on the proxy.

**Example**

The code below will output the following:

```
Before creating the proxy
After creating the proxy
Before calling toString() on the proxy
Creating FooBar...
After calling toString() on the proxy
```



```

public class LazyInitializerProxyExample {

	public static void main(String[] args) {
		
		System.out.println("Before creating the proxy");
		
		FooBar fooBarProxy = ConcurrencyUtils.lazyInitializerProxy(FooBar.class, 
			new Callable<FooBar>() {

			public FooBar call() throws Exception {
				System.out.println("Creating FooBar...");
				return new FooBar();
			}
		});
		
		System.out.println("After creating the proxy");
		
		
		System.out.println("Before calling toString() on the proxy");
		
		// instantiation of FooBar will happen when
		// a method is called on the proxy
		fooBarProxy.toString();
		
		System.out.println("After calling toString() on the proxy");
	}

}

```