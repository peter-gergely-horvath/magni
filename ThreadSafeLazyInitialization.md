#Magni provides support for Thread-safe lazy initialization

# Introduction #

Implementing Thread-safe lazy initialization in Java can be tricky. Magni provides an easy to use API for this.

## Lazy initialization support ##

`org.magni.concurrent.LazyInitializer<T>` class is a variable container that defers initialization until the first time its `get()` method is invoked. The value is established in `initializeValue()` and is stored internally; all subsequent invocations return the same value.

`LazyInitializer.get()` encapsulates the logic for thread safe invocation of `LazyInitializer.initializeValue()` so the developer does not have to deal with additional external synchronization or locking.

**First example: subclassing**
```

public class LazyInitializedFooBarExample {

	private static LazyInitializer<FooBar> LAZY_FOOBAR_RESOURCE = new LazyInitializer<FooBar>() {

		@Override
		protected FooBar initializeValue() {
			return new FooBar();
		}
	};

	public static void main(String[] args) {
		
		// Thread-safe lazy initialized FooBar
		FooBar fooBar = LAZY_FOOBAR_RESOURCE.get();
		
		// use fooBar object ...
		
	}

}

```


**Second example: initializing the value with java.util.concurrent.Callable**

```
public class LazyInitializedFooBarExample {

	private static LazyInitializer<FooBar> LAZY_FOOBAR_RESOURCE = 
			ConcurrencyUtils.lazyInitializer(new Callable<FooBar>() {

		public FooBar call() throws Exception {
			return new FooBar();
		}
	});
	


	public static void main(String[] args) {
		
		// Thread-safe lazy initialized FooBar
		FooBar fooBar = LAZY_FOOBAR_RESOURCE.get();
		
		// use fooBar object ...
		
	}

}
```