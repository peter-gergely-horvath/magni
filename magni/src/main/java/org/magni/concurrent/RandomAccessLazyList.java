package org.magni.concurrent;

import java.util.List;
import java.util.concurrent.Callable;

public class RandomAccessLazyList<E> extends LazyList<E> implements java.util.RandomAccess {

	RandomAccessLazyList(Callable<List<E>> initializer) {
		super(initializer);
	}

}
