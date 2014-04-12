package org.magni.concurrent;

public class LazyInitializerException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	LazyInitializerException() {
        super();
    }

    LazyInitializerException(String message) {
        super(message);
    }

    LazyInitializerException(String message, Throwable cause) {
        super(message, cause);
    }

    LazyInitializerException(Throwable cause) {
        super(cause);
    }

}
