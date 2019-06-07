
package Week1;

import java.util.Scanner;

import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.ST;

public class SAP {
	private Digraph G;

	// constructor takes a digraph (not necessarily a DAG)
	public SAP(Digraph G) {
		if (G == null)
			throw new IllegalArgumentException();
		this.G = new Digraph(G);
		// when making your implementation of bfs, make sure to not do bfs whendistance
		// already there and sop at root
	}

	private int[] shortest(int w, int v) {
		validateVertex(v);
		validateVertex(w);
		int shortestlen, shortestancestor;
		shortestlen = Integer.MAX_VALUE;
		shortestancestor = -1;
		int[] result = new int[2];
		BreadthFirstDirectedPaths bfsv = new BreadthFirstDirectedPaths(G, v);
		BreadthFirstDirectedPaths bfsw = new BreadthFirstDirectedPaths(G, w);
		for (int i = 0; i < G.V(); i++) {
			if (bfsv.hasPathTo(i) && bfsw.hasPathTo(i)) {
				int len = bfsv.distTo(i) + bfsw.distTo(i);
				if (len < shortestlen) {
					shortestlen = len;
					shortestancestor = i;
				}

			}
		}
		if (shortestancestor == -1) {
			result[0] = result[1] = -1;
		} else {
			result[0] = shortestlen;
			result[1] = shortestancestor;
		}
		return result;
	}

	private int[] shortest(Iterable<Integer> w, Iterable<Integer> v) {
		validateVertices(w);
		validateVertices(v);
		int shortestlen, shortestancestor;
		shortestlen = Integer.MAX_VALUE;
		shortestancestor = -1;
		int[] result = new int[2];
		BreadthFirstDirectedPaths bfsv = new BreadthFirstDirectedPaths(G, v);
		BreadthFirstDirectedPaths bfsw = new BreadthFirstDirectedPaths(G, w);
		for (int i = 0; i < G.V(); i++) {
			if (bfsv.hasPathTo(i) && bfsw.hasPathTo(i)) {
				int len = bfsv.distTo(i) + bfsw.distTo(i);
				if (len < shortestlen) {
					shortestlen = len;
					shortestancestor = i;
				}
			}
		}
		if (shortestancestor == -1) {
			result[0] = result[1] = -1;
		} else {
			result[0] = shortestlen;
			result[1] = shortestancestor;
		}
		return result;
	}

	private void validateVertex(int v) {
		int V = G.V();
		if (v < 0 || v >= V)
			throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V - 1));
	}

	private void validateVertices(Iterable<Integer> vertices) {
		if (vertices == null) {
			throw new IllegalArgumentException("argument is null");
		}
		int V = G.V();
		for (int v : vertices) {
			if (v < 0 || v >= V || (Object)v==null) {
				throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V - 1));
			}
		}
	}

	// length of shortest ancestral path between v and w; -1 if no such path
	public int length(int v, int w) {
		validateVertex(v);
		validateVertex(w);
		int[] result = shortest(w, v);
		return result[0];
	}

	// a common ancestor of v and w that participates in a shortest ancestral path;
	// -1 if no such path
	public int ancestor(int v, int w) {
		validateVertex(v);
		validateVertex(w);
		int[] result = shortest(w, v);
		return result[1];
	}

	// length of shortest ancestral path between any vertex in v and any vertex in
	// w; -1 if no such path
	public int length(Iterable<Integer> v, Iterable<Integer> w) {
		validateVertices(v);
		validateVertices(w);
		int[] result = shortest(w, v);
		return result[0];
	}

	// a common ancestor that participates in shortest ancestral path; -1 if no such
	// path
	public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
		validateVertices(v);
		validateVertices(w);
		int[] result = shortest(w, v);
		return result[1];
	}

	// do unit testing of this class
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		String file = scan.nextLine();
		In in = new In(file);
		Digraph G = new Digraph(in);
		SAP sap = new SAP(G);
		while (!StdIn.isEmpty()) {
			int v = StdIn.readInt();
			int w = StdIn.readInt();
			int length = sap.length(v, w);
			int ancestor = sap.ancestor(v, w);
			StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);

		}

	}
}