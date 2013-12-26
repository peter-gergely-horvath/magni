package org.magni.concurrent;

import java.util.concurrent.Callable;


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
		System.out.println(fooBar);
	}

}