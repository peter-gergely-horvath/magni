#Magni collection utility APIs

# Collection utilities #

Magni collection utilities provide features I miss from other collection libraries. Some of them are are relatively simple but would have been very helpful in my past work.

## "Sealable" collections ##

`org.magni.collect.sealable.SealableCollections` provides static methods for creating collection wrappers that can be made read-only ("sealed") by in invoking `seal()` method on them. The collections are editable until sealed, after which they behave exactly like the collections returned by `java.util.Collections.unmodifiable*()` methods:

```
org.magni.collect.sealable.SealableCollections.sealableCollection(Collection<E>)
org.magni.collect.sealable.SealableCollections.sealableList(List<E>)
org.magni.collect.sealable.SealableCollections.sealableMap(Map<K, V>)
org.magni.collect.sealable.SealableCollections.sealableSet(Set<E>)
org.magni.collect.sealable.SealableCollections.sealableSortedMap(SortedMap<K, V>)
org.magni.collect.sealable.SealableCollections.sealableSortedSet(SortedSet<E>)

```

## Tree traversal ##

`org.magni.collect.tree.TreeTraverser` allows iterating over the nodes of a tree. All you have to do is subclassing it and implementing the `getChildrenOf(E parent)` method properly:

```
new TreeTraverser<FooBarTreeNode>() {
	@Override
	protected Iterable<FooBarTreeNode> getChildrenOf(FooBarTreeNode parent) {
		
		return parent.getChildren();
	}
};
```


**Full example for tree traversal**

```

public class BreadthFirstTreeTraverserExample {

	/**
	 * Demonstration of what needs to be implemented 
	 * to be able to traverse a custom tree class
	 */
	private static final TreeTraverser<TestTreeNode> TESTTREENODE_TRAVERSER = 
			new TreeTraverser<TestTreeNode>() {

		@Override
		protected Iterable<TestTreeNode> getChildrenOf(TestTreeNode parent) {
			return parent.getChildren();
		}
	};
	
	
	/**
	 * Test Tree Node class for the sake of the example
	 */
	static class TestTreeNode {

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

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return name;
		}

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
	 */
	public static void main(String[] args) {

		TestTreeNode d = new TestTreeNode("d");
		TestTreeNode e = new TestTreeNode("e");

		TestTreeNode f = new TestTreeNode("f");
		TestTreeNode g = new TestTreeNode("g");

		TestTreeNode b = new TestTreeNode("b", d, e);
		TestTreeNode c = new TestTreeNode("c", f, g);

		TestTreeNode a = new TestTreeNode("a", b, c);

		for (TestTreeNode node : TESTTREENODE_TRAVERSER.breadthFirst(a)) {

			System.out.println(node);
		}
	}
}

```