
package Week1;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
	private WordNet wordnet;

	public Outcast(WordNet wordnet) // constructor takes a WordNet object
	{
		this.wordnet = wordnet;
	}

	public String outcast(String[] nouns) // given an array of WordNet nouns, return an outcast
	{
		int[] distance = new int[nouns.length];
		for (int i = 0; i < nouns.length; i++) {
			for (int j = i + 1; j < nouns.length; j++) {
				int dist = wordnet.distance(nouns[i], nouns[j]);
				distance[i] += dist;
				distance[j] += dist;
			}
		}
		int maxDist = 0;
		String outcast = null;
		for (int i = 0; i < distance.length; ++i) {
			if (distance[i] > maxDist) {
				maxDist = distance[i];
				outcast = nouns[i];
			}
		}
		return outcast;
	}

	public static void main(String[] args) { // see test client belows
		WordNet wrd = new WordNet("synsets.txt", "hypernyms.txt");
		Outcast outcast = new Outcast(wrd);
		In in = new In("outcast.txt");
		String[] nouns = in.readAllStrings();
		StdOut.println(outcast.outcast(nouns));
	} 
}
