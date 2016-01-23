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

import static org.testng.Assert.assertEquals;

import java.util.Iterator;

/**
 * @author Peter G. Horvath
 * 
 */
public class TreeTraverserTestBase {

	protected static final TreeTraverser<TestTreeNode> TEST_TREENODE_TRAVERSER = new TreeTraverser<TestTreeNode>() {

		@Override
		protected Iterable<TestTreeNode> getChildrenOf(TestTreeNode parent) {
			return parent.getChildren();
		}
	};

	protected static void assertIteratorOrderMatches(
			Iterator<TestTreeNode> iterator, TestTreeNode... expected) {

		for (TestTreeNode expectedTreeNode : expected) {
			assertEquals(expectedTreeNode, iterator.next());
		}
	}

}
