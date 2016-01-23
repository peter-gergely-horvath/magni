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

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.util.concurrent.Callable;

import org.easymock.EasyMock;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;

/**
 * @author Peter G. Horvath
 * 
 */
public class LazyInitializerTest {

	private Lazy.Initializer<Long> lazyInitializer;
	private Callable<Long> mockCallable;

	
	
	@BeforeMethod @SuppressWarnings("unchecked")
	public void beforeTests() {
		mockCallable = EasyMock.createMock(Callable.class);
		lazyInitializer = Lazy.<Long>initializer(mockCallable);
	}

	
	@Test(expectedExceptions=NullPointerException.class)
	public void testNullInitializerThrowsNullPointerException() {
		Lazy.<Long>initializer(null);
	}

	
	/**
	 * Tests that the initializer is only called once
	 */
	@Test
	public void testInitializerValue() throws Exception {

		final Long expectedValue = 42L;

		expect(mockCallable.call()).andReturn(expectedValue).times(1);

		replay(mockCallable);

		assertEquals(expectedValue, lazyInitializer.get());
		assertEquals(expectedValue, lazyInitializer.get());
		assertEquals(expectedValue, lazyInitializer.get());

		verify(mockCallable);

	}

	/**
	 * Tests that if an exception is thrown from
	 * the initializer, subsequent invocations of
	 * get can recover
	 */
	@Test
	public void testInitializerThrowsException() throws Exception {

		final Long expectedValue = 42L;

		expect(mockCallable.call()).andThrow(
				new IllegalStateException(
						"Unable to initialize on first attempt")).times(1);

		expect(mockCallable.call()).andThrow(
				new IllegalStateException(
						"Unable to initialize on second attempt")).times(1);

		expect(mockCallable.call()).andReturn(expectedValue).times(1);

		replay(mockCallable);

		try {
			lazyInitializer.get();
			fail("Should have thrown an exception");
		} catch (RuntimeException e) {
			assertEquals("Unable to initialize on first attempt", e.getMessage());
		}

		try {
			lazyInitializer.get();
			fail("Should have thrown an exception");
		} catch (RuntimeException e) {
			assertEquals("Unable to initialize on second attempt", e.getMessage());
		}

		assertEquals(expectedValue, lazyInitializer.get());
		assertEquals(expectedValue, lazyInitializer.get());
		assertEquals(expectedValue, lazyInitializer.get());

		verify(mockCallable);

	}
	
	/**
	 * Tests that Error thrown from the initializer is
	 * not wrapped into an Exception but delegated to
	 * the caller 
	 */
	@Test
	public void testInitializerThrowsError() throws Exception {

		final Long expectedValue = 42L;

		expect(mockCallable.call()).andThrow(
				new java.lang.Error(
						"Unable to initialize on first attempt")).times(1);

		expect(mockCallable.call()).andThrow(
				new java.lang.Error(
						"Unable to initialize on second attempt")).times(1);

		expect(mockCallable.call()).andReturn(expectedValue).times(1);

		replay(mockCallable);

		try {
			lazyInitializer.get();
			fail("Should have thrown an Error");
		} catch (java.lang.Error e) {
			assertEquals("Unable to initialize on first attempt", 
					e.getMessage());
		}

		try {
			lazyInitializer.get();
			fail("Should have thrown an Error");
		} catch (java.lang.Error e) {
			assertEquals("Unable to initialize on second attempt", 
					e.getMessage());
		}

		assertEquals(expectedValue, lazyInitializer.get());
		assertEquals(expectedValue, lazyInitializer.get());
		assertEquals(expectedValue, lazyInitializer.get());

		verify(mockCallable);

	}

}
