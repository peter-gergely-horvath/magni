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
package org.magni.collect.tree;

import java.util.Iterator;

import org.junit.Test;

/**
 * @author Peter G. Horvath
 * 
 */
public class DepthFirstTreeTraverserTest extends TreeTraverserTestBase {

	@Test(expected = NullPointerException.class)
	public void testNullRootThrowsException() {
		TEST_TREENODE_TRAVERSER.depthFirst(null);
	}
	
	/** 
	 * Test traversal logic on the following tree:
	 *  
	 * <tt><pre>
	 * 
	 *         a
	 *       /   \
	 *      /     \
	 *     b       c
	 *   /  \     / \
	 *  d    e   f   g
	 *  </pre></tt>
	 *  
	 * Expected order is: a, b, d, e, c, f, g
	 *  
	 */
	@Test
	public void testTraversal() {
		
		TestTreeNode d = new TestTreeNode("d");
		TestTreeNode e = new TestTreeNode("e");
		
		TestTreeNode f = new TestTreeNode("f");
		TestTreeNode g = new TestTreeNode("g");
		
		TestTreeNode b = new TestTreeNode("b", d, e);
		TestTreeNode c = new TestTreeNode("c", f, g);
		
		TestTreeNode a = new TestTreeNode("a", b, c);
		
		Iterator<TestTreeNode> iterator = TEST_TREENODE_TRAVERSER.depthFirst(a).iterator();
		
		assertIteratorOrderMatches(iterator, a, b, d, e, c, f, g);
		
	}
	
	@Test
	public void testTraversalMultipleTimes() {
		
		TestTreeNode d = new TestTreeNode("d");
		TestTreeNode e = new TestTreeNode("e");
		
		TestTreeNode f = new TestTreeNode("f");
		TestTreeNode g = new TestTreeNode("g");
		
		TestTreeNode b = new TestTreeNode("b", d, e);
		TestTreeNode c = new TestTreeNode("c", f, g);
		
		TestTreeNode a = new TestTreeNode("a", b, c);
		
		Iterable<TestTreeNode> depthFirstIterable = TEST_TREENODE_TRAVERSER.depthFirst(a);
		
		assertIteratorOrderMatches(depthFirstIterable.iterator(), a, b, d, e, c, f, g);
		assertIteratorOrderMatches(depthFirstIterable.iterator(), a, b, d, e, c, f, g);
		assertIteratorOrderMatches(depthFirstIterable.iterator(), a, b, d, e, c, f, g);
		
	}

}
