package app;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import structures.Arc;
import structures.Graph;
import structures.PartialTree;
import structures.Vertex;

/**
 * Stores partial trees in a circular linked list
 * 
 */
public class PartialTreeList implements Iterable<PartialTree> {

	/**
	 * Inner class - to build the partial tree circular linked list
	 * 
	 */
	public static class Node {
		/**
		 * Partial tree
		 */
		public PartialTree tree;

		/**
		 * Next node in linked list
		 */
		public Node next;

		/**
		 * Initializes this node by setting the tree part to the given tree, and setting
		 * next part to null
		 * 
		 * @param tree Partial tree
		 */
		public Node(PartialTree tree) {
			this.tree = tree;
			next = null;
		}
	}

	/**
	 * Pointer to last node of the circular linked list
	 */
	private Node rear;

	/**
	 * Number of nodes in the CLL
	 */
	private int size;

	/**
	 * Initializes this list to empty
	 */
	public PartialTreeList() {
		rear = null;
		size = 0;
	}

	/**
	 * Adds a new tree to the end of the list
	 * 
	 * @param tree Tree to be added to the end of the list
	 */
	public void append(PartialTree tree) {
		Node ptr = new Node(tree);
		if (rear == null) {
			ptr.next = ptr;
		} else {
			ptr.next = rear.next;
			rear.next = ptr;
		}
		rear = ptr;
		size++;
	}

	/**
	 * Initializes the algorithm by building single-vertex partial trees
	 * 
	 * @param graph Graph for which the MST is to be found
	 * @return The initial partial tree list
	 */
	public static PartialTreeList initialize(Graph graph) {
		/* COMPLETE THIS METHOD */

		// v represents the vertices of the graph
		Vertex[] v = graph.vertices;

		// ptList representing the list of all partial trees to be returned
		PartialTreeList ptList = new PartialTreeList();

		// looping through all the vertices in v
		for (int i = 0; i < graph.vertices.length; i++) {

			// Create a new partial tree for every vertex
			PartialTree pt = new PartialTree(v[i]);

			// loop through all the neighbors to add the arcs to the vertex
			for (Vertex.Neighbor n = v[i].neighbors; n != null; n = n.next) {
				pt.getArcs().insert(new Arc(v[i], n.vertex, n.weight));
			}

			// add finished partial tree to the partial tree list (ptList)
			ptList.append(pt);
		}
		return ptList;
	}

	/**
	 * Executes the algorithm on a graph, starting with the initial partial tree
	 * list for that graph
	 * 
	 * @param ptlist Initial partial tree list
	 * @return Array list of all arcs that are in the MST - sequence of arcs is
	 *         irrelevant
	 */
	public static ArrayList<Arc> execute(PartialTreeList ptlist) {
		/* COMPLETE THIS METHOD */

		ArrayList<Arc> MST = new ArrayList<Arc>();
		
		while (ptlist.size() > 1) {
			PartialTree PTX = ptlist.remove();
			Arc PQX = PTX.getArcs().deleteMin();
			
			while (PQX != null) {
				Vertex v1 = PQX.getv1();
				Vertex v2 = PQX.getv2();
				PartialTree PTY;
				PTY = ptlist.removeTreeContaining(v1);
				if (PTY == null)
					PTY = ptlist.removeTreeContaining(v2);
				if (PTY != null) {
					PTX.merge(PTY);
					MST.add(PQX);
					ptlist.append(PTX);
					break;
				}
				PQX = PTX.getArcs().deleteMin();
			}

		}
		return MST;
	}

	/**
	 * Removes the tree that is at the front of the list.
	 * 
	 * @return The tree that is removed from the front
	 * @throws NoSuchElementException If the list is empty
	 */
	public PartialTree remove() throws NoSuchElementException {

		if (rear == null) {
			throw new NoSuchElementException("list is empty");
		}
		PartialTree ret = rear.next.tree;
		if (rear.next == rear) {
			rear = null;
		} else {
			rear.next = rear.next.next;
		}
		size--;
		return ret;

	}

	/**
	 * Removes the tree in this list that contains a given vertex.
	 * 
	 * @param vertex Vertex whose tree is to be removed
	 * @return The tree that is removed
	 * @throws NoSuchElementException If there is no matching tree
	 */
	public PartialTree removeTreeContaining(Vertex vertex) throws NoSuchElementException {
		/* COMPLETE THIS METHOD */
		if (rear == null) {
			throw new NoSuchElementException("Empty PartialTree List");
		}
		
		PartialTree remove = null;
		Node temp = rear;
		
		do {
			PartialTree tree = temp.tree;
			Vertex parent = vertex;
			while (parent.parent != parent) {
				parent = parent.parent;
				System.out.println(parent);
			}
			if (parent == tree.getRoot()) {
				remove = tree;
				removeNode(temp);
				break;
			}
			temp = temp.next;
		} while (temp != rear);
		
		if (remove == null) {
			return null;
		} else
			return remove;
	}

	private void removeNode(Node n) {
		// TODO Auto-generated method stub
		Node prev = n;
		while (prev.next != n) {
			prev = prev.next;
		}

		Node next = n.next;
		if (prev == n && next == n) {
			rear = null;
			size--;
		} else if (prev == next) {
			if (n == rear) {
				rear = rear.next;
			}
			n.next.next = n.next;
			size--;
		} else {
			if (n == rear) {
				rear = prev;
			}
			prev.next = next;
			size--;
		}
	}

	/**
	 * Gives the number of trees in this list
	 * 
	 * @return Number of trees
	 */
	public int size() {
		return size;
	}

	/**
	 * Returns an Iterator that can be used to step through the trees in this list.
	 * The iterator does NOT support remove.
	 * 
	 * @return Iterator for this list
	 */
	public Iterator<PartialTree> iterator() {
		return new PartialTreeListIterator(this);
	}

	private class PartialTreeListIterator implements Iterator<PartialTree> {

		private PartialTreeList.Node ptr;
		private int rest;

		public PartialTreeListIterator(PartialTreeList target) {
			rest = target.size;
			ptr = rest > 0 ? target.rear.next : null;
		}

		public PartialTree next() throws NoSuchElementException {
			if (rest <= 0) {
				throw new NoSuchElementException();
			}
			PartialTree ret = ptr.tree;
			ptr = ptr.next;
			rest--;
			return ret;
		}

		public boolean hasNext() {
			return rest != 0;
		}

		public void remove() throws UnsupportedOperationException {
			throw new UnsupportedOperationException();
		}

	}
}
