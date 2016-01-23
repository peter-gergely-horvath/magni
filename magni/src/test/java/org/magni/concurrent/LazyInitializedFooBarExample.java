package org.magni.concurrent;

import java.util.concurrent.Callable;


public class LazyInitializedFooBarExample {

	private static final Lazy.Initializer<FooBar> LAZY_FOOBAR_RESOURCE = 
			Lazy.initializer(new Callable<FooBar>() {

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