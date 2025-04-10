import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

/**
 * An AVL tree-based implementation of a majority of the Java Collections api. A
 * TreeList is a sorted list, meaning it sorts its elements according to their
 * natural ordering, while allowing both duplicates and index access.
 * 
 * Adapted from my solution for EditorTrees, the term project in CSSE 230 at
 * Rose-Hulman Institute of Technology.
 *
 * @author Tal Belkind
 * 
 */
public class TreeList<T extends Comparable<T>> extends AbstractCollection<T> {
	private Node root; // the root node of the TreeList
	private int size; // the current size of the TreeList
	private final Node NULL_NODE = new Node(); // Node whose values are null to avoid checking for null errors

	/**
	 * Construct an empty TreeList
	 */
	public TreeList() {
		root = NULL_NODE;
	}

	/**
	 * Construct a single-node TreeList whose element is e
	 * 
	 * @param e the element for the root node to contain
	 */
	public TreeList(T e) {
		root = new Node(e);
		size++;
	}

	/**
	 * Make this TreeList be a copy of e, with all new nodes, but the same shape and
	 * contents. The new Tree will have the same structure, meaning it won't
	 * necessarily be a complete tree.
	 * 
	 * @param e the TreeList to copy
	 */
	public TreeList(TreeList<T> e) {
		this.root = treeListCopyHelper(e.root);
	}

	/**
	 * Copies a tree node-for-node
	 * 
	 * @param otherTreeNode the subtree to copy
	 * @return a new Node representing the copied subtree
	 */
	private Node treeListCopyHelper(Node otherTreeNode) {
		if (otherTreeNode == NULL_NODE) {
			return NULL_NODE;
		}
		Node node = new Node(otherTreeNode.data);
		size++;
		node.rank = otherTreeNode.rank;
		node.left = treeListCopyHelper(otherTreeNode.left);
		node.right = treeListCopyHelper(otherTreeNode.right);
		node.balance = otherTreeNode.balance;
		return node;
	}

	/**
	 * returns the string produced by an in-order traversal of this tree. The string
	 * is formatted like an array.
	 * 
	 * @return an array-formatted in-order traversal of the tree.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[");
		root.toString(sb);
		sb.delete(sb.length() - 2, sb.length());
		sb.append("]");
		return sb.toString();
	}

	/**
	 * Determines if the tree list is empty
	 * 
	 * @return true if the list is empty, otherwise false
	 */
	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Determines if the TreeList contains the specified object. Throws
	 * ClassCastException if o is not of the correct type.
	 * 
	 * @param o the object to check for existence in the list
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean contains(Object o) throws ClassCastException {
		if (!root.data.getClass().isInstance(o)) {
			throw new ClassCastException();
		}
		return root.contains((T) o);
	}

	/**
	 * @return a new Lazy In-order iterator of the TreeList
	 */
	@Override
	public Iterator<T> iterator() {
		return new LazyInOrderIterator();
	}

	/**
	 * Clears the TreeList, making the root simply a null node.
	 */
	@Override
	public void clear() {
		this.root = NULL_NODE;
	}

	/**
	 * Creates a new Object array containing the elements of the TreeList. The type
	 * of the array is not guaranteed by the method.
	 * 
	 * @return Object array containing all elements of the TreeList. The size of the
	 *         return array is equal to the size of the TreeList.
	 */
	@Override
	public Object[] toArray() {
		Object[] a = new Object[this.size];
		int i = 0;
		for (T element : this) {
			a[i] = element;
			i++;
		}
		return a;
	}

	/**
	 * Creates a new array containing the elements of the TreeList. The type of the
	 * array is based on the type of the input array.
	 * 
	 * @param array of the same type as the TreeList's data.
	 * @return Object array that can be cast to the inputted type
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object[] toArray(Object[] a) throws ClassCastException {
		if (!root.data.getClass().isInstance(a.getClass().getComponentType())) {
			throw new ClassCastException();
		}
		if (a.length < size) {
			a = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
		}
		if (a.length > size) {
			a[size] = null;
		}
		int i = 0;
		for (T element : this) {
			a[i] = element;
			i++;
		}
		return a;
	}

	/**
	 * Determines if every element from the input collection is contained within
	 * this TreeList. Elements in the collection do not have to be sorted.
	 * 
	 * @param c the collection to check
	 * @return true if every element from the input collection is contained in the
	 *         TreeList, otherwise false
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public boolean containsAll(Collection c) {
		for (Object o : c) {
			if (!this.contains(o)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Adds all elements from a given input collection to this TreeList. Elements in
	 * the collection do not have to be sorted. The order in which they were placed
	 * into the collection will be lost.
	 * 
	 * @param c the collection to add all elements from
	 * @return true if this TreeList was modified as a result of the call, otherwise
	 *         false
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean addAll(Collection c) throws ClassCastException {
		if (c.size() <= 0) {
			return false;
		}
		for (Object o : c) {
			if (!root.data.getClass().isInstance(o)) {
				throw new ClassCastException();
			}
			this.add((T) o);
		}
		return true;
	}

	/**
	 * Determines the size of the tree
	 * 
	 * @return the number of nodes in this tree, not including any instances of
	 *         NULL_NODE
	 */
	public int size() {
		return this.size;
	}

	/**
	 * Adds a new element to the TreeList. It's location in the tree will be based
	 * on the natural ordering of the element.
	 * 
	 * @param e the element to add to the tree
	 */
	public boolean add(T e) {
		NodeInfo info = new NodeInfo();
		root = root.add(e, info);
		if (info.succeeded)
			size++;
		return info.succeeded;
	}

	/**
	 * Retrieves an element at a specific position
	 * 
	 * @param pos position in the tree
	 * @return the element at that position
	 * @throws IndexOutOfBoundsException if the given position is outside the range
	 *                                   of the TreeList
	 */
	public T get(int pos) throws IndexOutOfBoundsException {
		if (pos < 0 || pos >= size) {
			throw new IndexOutOfBoundsException();
		}
		return root.get(pos).data;
	}

	/**
	 * Removes an object from the treelist. Throws ClassCastException if the type of
	 * the input is invalid.
	 * 
	 * @param o the object to remove from the TreeList
	 * @return the character that is removed
	 * @throws ClassCastException if the type of the input is not the same as the
	 *                            data type of the tree list.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(Object o) throws ClassCastException {
		if (!root.data.getClass().isInstance(o)) {
			throw new ClassCastException();
		}
		NodeInfo info = new NodeInfo();
		root = root.remove((T) o, info);
		if (info.succeeded)
			size--;
		return info.succeeded;
	}

	/**
	 * Determines if this TreeList is equivalent to another based on the In-order
	 * traversal of their elements
	 * 
	 * @param o the object to evaluate this TreeList against
	 * @return false if this TreeList and the inputed object are not equivalent,
	 *         otherwise True.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof TreeList))
			return false;
		if (this.size() != ((TreeList<T>) o).size())
			return false;
		Iterator<T> myIterator = iterator();
		Iterator<T> oIterator = ((TreeList<T>) o).iterator();
		while (myIterator.hasNext()) {
			if (!myIterator.next().equals(oIterator.next())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * A node in a height-balanced binary tree with rank. Except for the NULL_NODE,
	 * one node cannot belong to two different trees.
	 */
	class Node {

		private enum Code {
			SAME, LEFT, RIGHT;
		}

		private T data; // the data contained by this node
		private Node left, right; // the left and right subtrees of this node
		private int rank; // the in-order position of this node within its own subtree.
		private Code balance; // the balance of this node (either tipped left, equal, or tipped right)

		/**
		 * Creates a new null node, whose data, left, and right nodes are set to null.
		 */
		private Node() {
			this.left = null;
			this.right = null;
			this.data = null;
			this.balance = Code.SAME;
		}

		/**
		 * Creates a Node with the specified data and left and right null nodes
		 * 
		 * @param element representing the data for this node to contain
		 */
		private Node(T data) {
			this.left = NULL_NODE;
			this.right = NULL_NODE;
			this.data = data;
			this.balance = Code.SAME;
		}

		/**
		 * Adds the specified element based on the natural ordering of the element
		 * 
		 * @param element  the element to add
		 * @param NodeInfo object to track key information throughout the insertion
		 *                 process
		 * @return Node representing the new root of the subtree with the new addition
		 */
		private Node add(T ch, NodeInfo info) {
			if (this == NULL_NODE) {
				info.succeeded = true;
				return new Node(ch);
			}
			if (ch.compareTo(this.data) > 0) {
				right = right.add(ch, info);
				return handleRightInsertion(info);
			} else {
				rank++;
				left = left.add(ch, info);
				return handleLeftInsertion(info);
			}
		}

		/**
		 * Determines new balance codes for the right subtree based on the insertion
		 * 
		 * @param info NodeInfo object to store rotation/balance code information
		 * @return the modified root
		 */
		private Node handleRightInsertion(NodeInfo info) {
			if (balance == Code.SAME && !info.stopRotating) {
				balance = Code.RIGHT;
			} else if (balance == Code.LEFT && !info.stopRotating) {
				balance = Code.SAME;
				info.stopRotating = true;
			} else if (balance == Code.RIGHT && !info.stopRotating) {
				info.stopRotating = true;
				if (right.balance == Code.LEFT) {
					return doubleRotateLeft(this, this.right, this.right.left, info);
				}
				return singleRotateLeft(this, this.right, info, false);
			}
			return this;
		}

		/**
		 * Determines new balance codes for the left subtree based on the insertion
		 * 
		 * @param info NodeInfo object to store rotation/balance code information
		 * @return the modified root
		 */
		private Node handleLeftInsertion(NodeInfo info) {
			if (balance == Code.SAME && !info.stopRotating) {
				balance = Code.LEFT;
			} else if (balance == Code.RIGHT && !info.stopRotating) {
				balance = Code.SAME;
				info.stopRotating = true;
			} else if (balance == Code.LEFT && !info.stopRotating) {
				info.stopRotating = true;
				if (left.balance == Code.RIGHT) {
					return doubleRotateRight(this, this.left, this.left.right, info);
				}
				return singleRotateRight(this, this.left, info, false);
			}
			return this;
		}

		/**
		 * removes the given element, rotating as necessary. The TreeList is not
		 * modified if the given element does not exist.
		 * 
		 * @param element the element to remove
		 * @param info    wrapper storing rotation/balance code information
		 * @return the modified root
		 */
		private Node remove(T element, NodeInfo info) {

			// find position or traverse tree until found
			if (element.compareTo(this.data) == 0) {
				info.succeeded = true;

				// removes current node and replaces with
				// child, or in-order successor if 2 children
				if (this.left == NULL_NODE && this.right == NULL_NODE) {
					return NULL_NODE;
				} else if (this.left == NULL_NODE) {
					return this.right;
				} else if (this.right == NULL_NODE) {
					return this.left;
				} else {
					// swaps with the next in-order successor's
					// data, then removes that successor
					data = right.get(0).data;
					right = right.remove(data, info);
					return handleRightDeletion(info);
				}
			} else if (element.compareTo(this.data) < 0) {
				if (left == NULL_NODE) {
					return this;
				}
				rank--;
				left = left.remove(element, info);
				return handleLeftDeletion(info);
			} else {
				if (right == NULL_NODE) {
					return this;
				}
				right = right.remove(element, info);
				return handleRightDeletion(info);
			}
		}

		/**
		 * Determines balance codes post-deletion in left subtree <br>
		 * Determines changes to balance codes, and whether to keep monitoring changes
		 * up the tree
		 * 
		 * @param info NodeInfo to store deletion/balance code info
		 * @return the node with modified balance codes
		 */
		private Node handleLeftDeletion(NodeInfo info) {
			// only adjust if still considering balance changes
			if (!info.stopRotating) {
				if (this.balance == Code.LEFT) {
					this.balance = Code.SAME;
				} else if (this.balance == Code.RIGHT) {
					// trigger rotation stuff
					if (this.right.balance == Code.RIGHT) {
						return singleRotateLeft(this, this.right, info, false);
					} else if (this.right.balance == Code.SAME) {
						// special situation with balance codes after rotation
						info.stopRotating = true;
						return singleRotateLeft(this, this.right, info, true);
					} else {
						return doubleRotateLeft(this, this.right, this.right.left, info);
					}
				} else {
					this.balance = Code.RIGHT;
					info.stopRotating = true;
				}
			}
			return this;
		}

		/**
		 * Determines balance codes post-deletion in right subtree <br>
		 * Determines changes to balance codes, and whether to keep monitoring changes
		 * up the tree
		 * 
		 * @param info NodeInfo to store deletion/balance code info
		 * @return the node with modified balance codes
		 */
		private Node handleRightDeletion(NodeInfo info) {
			// only adjust if still considering balance changes
			if (!info.stopRotating) {
				if (this.balance == Code.LEFT) {
					// trigger rotation stuff
					if (this.left.balance == Code.LEFT) {
						return singleRotateRight(this, this.left, info, false);
					} else if (this.left.balance == Code.SAME) {
						// special situation with balance codes after rotation
						info.stopRotating = true;
						return singleRotateRight(this, this.left, info, true);
					} else {
						return doubleRotateRight(this, this.left, this.left.right, info);
					}
				} else if (this.balance == Code.RIGHT) {
					this.balance = Code.SAME;
				} else {
					this.balance = Code.LEFT;
					info.stopRotating = true;
				}
			}
			return this;
		}

		/**
		 * Performs a double right rotation
		 * 
		 * @param parent     the parent node to be rotated
		 * @param child      the child node to be rotated
		 * @param grandchild the Grandchild node to be rotated
		 * @param info       NodeInfo object storing rotation information
		 * @return the new root of the rotated tree
		 */
		private Node doubleRotateRight(Node parent, Node child, Node grandchild, NodeInfo info) {
			Code grandchildBalance = grandchild.balance;
			Node rotatedNode = singleRotateRight(parent, singleRotateLeft(child, grandchild, info, false), info, false);
			if (grandchildBalance == Code.RIGHT) {
				rotatedNode.left.balance = Code.LEFT;
			} else if (grandchildBalance == Code.LEFT) {
				rotatedNode.right.balance = Code.RIGHT;
			}
			return rotatedNode;
		}

		/**
		 * Performs a double left rotation
		 * 
		 * @param parent     the parent node to be rotated
		 * @param child      the child node to be rotated
		 * @param grandchild the grandchild node to be rotated
		 * @param info       NodeInfo object storing rotation information
		 * @return the new root of the rotated tree
		 */
		private Node doubleRotateLeft(Node parent, Node child, Node grandchild, NodeInfo info) {
			Code grandchildBalance = grandchild.balance;
			Node rotatedNode = singleRotateLeft(parent, singleRotateRight(child, child.left, info, false), info, false);
			if (grandchildBalance == Code.RIGHT) {
				rotatedNode.left.balance = Code.LEFT;
			} else if (grandchildBalance == Code.LEFT) {
				rotatedNode.right.balance = Code.RIGHT;
			}
			return rotatedNode;
		}

		/**
		 * Performs a single right rotation
		 * 
		 * @param parent            the parent node to be rotated
		 * @param child             the child node to be rotated
		 * @param info              NodeInfo object storing rotation information
		 * @param isSpecialDeletion true if the rotation does not yield = balance codes
		 * @return the new root of the rotated tree
		 */
		private Node singleRotateRight(Node parent, Node child, NodeInfo info, boolean isSpecialDeletion) {
			Node temp = child.right;
			child.right = parent;
			parent.left = temp;
			if (isSpecialDeletion) {
				parent.balance = Code.LEFT;
				child.balance = Code.RIGHT;
			} else {
				parent.balance = Code.SAME;
				child.balance = Code.SAME;
			}
			parent.rank = parent.rank - child.rank - 1;
			return child;
		}

		/**
		 * Performs a single left rotation
		 * 
		 * @param parent            the parent node to be rotated
		 * @param child             the child node to be rotated
		 * @param info              NodeInfo object storing rotation information
		 * @param isSpecialDeletion true if the rotation does not yield = balance codes
		 * @return the new root of the rotated tree
		 */
		private Node singleRotateLeft(Node parent, Node child, NodeInfo info, boolean isSpecialDeletion) {
			Node temp = child.left;
			child.left = parent;
			parent.right = temp;
			if (isSpecialDeletion) {
				parent.balance = Code.RIGHT;
				child.balance = Code.LEFT;
			} else {
				parent.balance = Code.SAME;
				child.balance = Code.SAME;
			}
			child.rank += parent.rank + 1;
			return child;

		}

		/**
		 * Returns a String representation of the TreeList using an in-order traversal
		 * 
		 * @param sb the StringBuilder object to add modifications to
		 */
		private void toString(StringBuilder sb) {
			if (this.equals(NULL_NODE))
				return;
			left.toString(sb);
			sb.append(String.valueOf(data));
			sb.append(", ");
			right.toString(sb);
		}

		/**
		 * Retrieves the Node at a given position
		 * 
		 * @param pos the integer position to locate the node at relative to the current
		 *            subtree
		 * @return the Node found at the specified position
		 */
		private Node get(int pos) {
			if (rank == pos) {
				return this;
			} else if (pos <= rank) {
				return left.get(pos);
			}
			return right.get(pos - rank - 1);
		}

		/**
		 * Determines if this Node or one of it's subtrees contains the specified
		 * element
		 * 
		 * @param element the element to determine containment for
		 * @return true if the element is contained within this node or one of it's
		 *         subtrees, otherwise false
		 */
		private boolean contains(T element) {
			if (this == NULL_NODE) {
				return false;
			}
			int comparison = element.compareTo(this.data);
			if (comparison == 0) {
				return true;
			} else if (comparison < 0) {
				return left.contains(element);
			} else {
				return right.contains(element);
			}
		}
	}

	/**
	 * Helper Class: Node Info Stores certain info that helps track rotations and
	 * deletion value up the tree
	 */
	private class NodeInfo {
		// instance fields
		private boolean stopRotating;
		private boolean succeeded;

		public NodeInfo() {
			this.stopRotating = false;
			this.succeeded = false;
		}
	}

	/**
	 * Lazy in-order iterator implementation
	 */
	private class LazyInOrderIterator implements Iterator<T> {

		private Stack<Node> s;
		private Node current;

		/**
		 * Creates a new Lazy in-order iterator
		 */
		public LazyInOrderIterator() {
			s = new Stack<Node>();
			current = root;
		}

		/**
		 * @return true if the tree has a next element to iterate over, otherwise false
		 */
		@Override
		public boolean hasNext() {
			return current != NULL_NODE || !s.isEmpty();
		}

		/**
		 * Gets the subsequent element of the tree
		 * 
		 * @return the next element of the tree
		 */
		@Override
		public T next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			while (current != NULL_NODE) {
				s.push(current);
				current = current.left;
			}
			Node node = s.pop();
			current = node.right;
			return node.data;
		}
	}
}
