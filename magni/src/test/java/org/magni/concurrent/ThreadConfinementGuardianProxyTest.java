/*
 *   Copyright 2013 Peter G. Horvath
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

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.magni.concurrent.ConcurrencyUtils;

/**
 * @author Peter G. Horvath
 * 
 */
public class ThreadConfinementGuardianProxyTest {

	private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

	@Test
	public void testAccessFromSameThread() {

		HashSet<String> guardedHashSet = ConcurrencyUtils
				.threadConfinementGuardian(new HashSet<String>());

		guardedHashSet.add("Foobar");

		Assert.assertTrue(guardedHashSet.contains("Foobar"));
	}

	@Test(expected=org.magni.concurrent.ThreadConfinementViolationException.class)
	public void testAccessFromAnotherThread() {

		try {
			final HashSet<String> guardedHashSet = ConcurrencyUtils
					.threadConfinementGuardian(new HashSet<String>());

			guardedHashSet.add("Foobar");

			Future<Boolean> future = EXECUTOR.submit(new Callable<Boolean>() {

				public Boolean call() throws Exception {
					return guardedHashSet.contains("Foobar");
				}
			});

			future.get();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch (ExecutionException e) {
			throw launderThrowable(e.getCause());
		}
	}

	@AfterClass
	public static void afterClass() {

		List<Runnable> tasksNeverCommencedExecution = EXECUTOR.shutdownNow();
		if (!tasksNeverCommencedExecution.isEmpty()) {
			throw new IllegalStateException(
					String.format(
							"%s tasks not commenced yet retrieved from ExecutorService",
							tasksNeverCommencedExecution.size()));

		}
	}
	
	private static RuntimeException launderThrowable(Throwable t) {
		if (t instanceof java.lang.Error) {
			throw (java.lang.Error) t;
		} else if (t instanceof RuntimeException) {
			return (RuntimeException) t;
		} else {
			return new RuntimeException(t);
		}

	}

}
