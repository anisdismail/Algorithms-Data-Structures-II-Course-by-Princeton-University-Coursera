
package Week1;

import java.util.ArrayList;
import java.util.Scanner;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.ST;

public class WordNet {
	// constructor takes the name of the two input files
	private ST<String, Integer> synsetsST;
	private ST<Integer, ArrayList<String>> idToStrings;
	private Digraph G;
	private SAP sap;
	private int numV;

	public WordNet(String synsets, String hypernyms) {
		if (synsets == null || hypernyms == null)
			throw new IllegalArgumentException();
		synsetsST = processSynsets(synsets);
		G = processDigraph(hypernyms);
		sap = new SAP(G);
		numV = 0;
		/*
		 * for(Integer i: synsets.keys()) { System.out.println(i+" : "+synsets.get(i));
		 * }
		 */}

	private Digraph processDigraph(String hypernyms) {
		G = new Digraph(numV);
		In in = new In(hypernyms);
		String line;
		String[] parts = new String[2];
		while ((line = in.readLine()) != null) {
			parts = line.split(",");
			if (parts.length != 1) {
				for (int i = 1; i < parts.length; i++)
					G.addEdge(Integer.parseInt(parts[0]), Integer.parseInt(parts[i]));
			}
		}
		DirectedCycle dc = new DirectedCycle(G);
		if (dc.hasCycle()) {
			throw new IllegalArgumentException("Cycle detected");
		}
		int numRoot = 0;
		for (int i = 0; i < G.V(); ++i) {
			if (G.outdegree(i) == 0) {
				++numRoot;
				if (numRoot > 1) {
					throw new IllegalArgumentException("More than 1 root");
				}
			}
		}

		return G;
	}

	private Digraph G() {
		return G;
	}

	// returns all WordNet nouns
	private ST<String, Integer> processSynsets(String synsets) {
		In in = new In(synsets);
		String line;
		String[] parts;
		synsetsST = new ST<>();
		idToStrings= new ST<>();
		while ((line = in.readLine()) != null) {
			// line=line.toLowerCase();
			parts = line.split(",");
			if (parts.length < 2) {
				continue;
			}
			int id=Integer.parseInt(parts[0]);
			String[] nouns = parts[1].split(" ");
			ArrayList<String> nounsList = new ArrayList<>();
			for (String n : nouns) {
				synsetsST.put(n,id );
				nounsList.add(n);
			}
			idToStrings.put(id, nounsList);
			numV++;
		}
		return synsetsST;
	}

	public Iterable<String> nouns() {
		return synsetsST.keys();
	}

	// is the word a WordNet noun?
	public boolean isNoun(String word) {
		if (word == null) {
			throw new IllegalArgumentException("word is null");
		}
		return synsetsST.contains(word);
	}

	// distance between nounA and nounB (defined below)
	public int distance(String nounA, String nounB) {
		if (!isNoun(nounA) || !isNoun(nounB)) {
			throw new IllegalArgumentException("Not WordNet noun.");
		}
		// System.out.println(nounA);
		// System.out.println(synsetsST.get(nounA));
		int nbA = synsetsST.get(nounA);

		// System.out.println(nounA+" at: "+nbA);
		int nbB = synsetsST.get(nounB);
		return sap.length(nbA, nbB);

	}

	// a synset (second field of synsets.txt) that is the common ancestor of nounA
	// and nounB
	// in a shortest ancestral path (defined below)
	public String sap(String nounA, String nounB) {
		if (!isNoun(nounA) || !isNoun(nounB)) {
			throw new IllegalArgumentException("Not WordNet noun.");
		}
		int nbA = synsetsST.get(nounA);
		int nbB = synsetsST.get(nounB);
		int anc = sap.ancestor(nbA, nbB);
		StringBuilder sap = new StringBuilder();
		ArrayList<String> nounsList = idToStrings.get(anc);
		for (String str : nounsList) {
			sap.append(str + " ");
		}

		return sap.toString();
	}

	public static void main(String[] args) {
		/*
		 * Digraph G= new Digraph(15); In in = new In("hypernyms15Path.txt"); String
		 * line; String[] parts; while (in.hasNextLine()) { line = in.readLine(); parts
		 * = line.split(","); G.addEdge(Integer.parseInt(parts[0]),
		 * Integer.parseInt(parts[1])); } System.out.println(G);
		 */
		Scanner scan = new Scanner(System.in);
		String wordA, wordB;
		WordNet wrd = new WordNet("synsets.txt", "hypernyms.txt");
		wordA = scan.nextLine();
		wordB = scan.nextLine();
		System.out.println("distance: " + wrd.distance(wordA, wordB));
		System.out.println("sap: " + wrd.sap(wordA, wordB));
		scan.close();
		// for (int i :wrd.G().adj(46483)) {
		// System.out.println(i);}
	}

}
