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
package org.magni.collect.tree;

import java.util.Arrays;
import java.util.List;

/**
 * @author Peter G. Horvath
 * 
 */
class TestTreeNode {

	private final List<TestTreeNode> children;
	private final String name;

	TestTreeNode(String name) {
		this(name, new TestTreeNode[0]);
	}
	
	TestTreeNode(String name, TestTreeNode... children) {
		this.name = name;
		this.children = Arrays.asList(children);
	}

	public Iterable<TestTreeNode> getChildren() {
		return children;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name;
	}

}
