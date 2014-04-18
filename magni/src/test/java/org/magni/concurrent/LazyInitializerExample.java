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



/**
 * @author Peter G. Horvath
 * 
 */
public class LazyInitializerExample {

	private static final Lazy.Initializer<String> LAZY_RESOURCE = new Lazy.Initializer<String>() {

		@Override
		protected String initializeValue() {
			try {
				System.out.print("((Expensive initialisation is running...))");
				Thread.sleep(8000);

				return "This is an expensive resource!";
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new RuntimeException(e);
			}
		}
	};

	public static void main(String[] args) {

		for (int i = 1; i < 11; i++) {
			System.out.format("Getting Lazy-init resource for the %s. time ", i);

			System.out.format(": %s %n", LAZY_RESOURCE.get());
		}

	}

}
