import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.StdOut;

public class BaseballElimination {
	private int[] w, r, l;
	private String[] teams;
	private int[][] g;
	private ST<String, Integer> teamsIndices;
	private int length;
	private ArrayList<String> subsets;

	public BaseballElimination(String filename) // create a baseball division from given filename in format specified
												// below
	{if(filename==null) {
		throw new IllegalArgumentException();
	}
		In scanner = new In(filename);
		length = scanner.readInt();
		w = new int[length];
		r = new int[length];
		l = new int[length];
		teams = new String[length];
		teamsIndices = new ST<>();
		g = new int[length][length];
		subsets=new ArrayList<>();
		for (int i = 0; i < length & scanner.hasNextLine(); i++) {
			String str = scanner.readString();
			teams[i] = str;
			teamsIndices.put(str, i);

			w[i] = scanner.readInt();
			l[i] = scanner.readInt();
			r[i] = scanner.readInt();
			for (int k = 0; k < length; k++) {
				g[i][k] = scanner.readInt();
			}
		}

		/*
		 * for (int i=0;i<length;i++) { for(int j=0;j<length;j++) {
		 * System.out.print(g[i][j] + "\t"); } System.out.println();}
		 */

	}

	public int numberOfTeams() // number of teams
	{
		return teams.length;
	}

	public Iterable<String> teams() // all teams
	{
		return Arrays.asList(teams);
	}

	public int wins(String team) // number of wins for given team
	{validateTeam(team);
		int i = teamsIndices.get(team);
		return w[i];
	}

	public int losses(String team) // number of losses for given team
	{validateTeam(team);
		int i = teamsIndices.get(team);
		return l[i];

	}

	public int remaining(String team) // number of remaining games for given team
	{validateTeam(team);
		int i = teamsIndices.get(team);
		return r[i];

	}

	public int against(String team1, String team2) // number of remaining games between team1 and team2
	{validateTeam(team1);
	validateTeam(team2);
		int i1 = teamsIndices.get(team1);
		int i2 = teamsIndices.get(team2);
		return g[i1][i2];
	}

	public boolean isEliminated(String team) // is given team eliminated?
	{validateTeam(team);
		String[] networkTeams = new String[length];
	int x = 0;
	int y = 0;
	int z = 0;
	int sum = 0;
	int size = 2 + length + numGameCombos(length) - 1;
	int t = teamsIndices.get(team);
	for (String current:teams) {
		if (wins(team) + remaining(team) < wins(current)) { 
			subsets.add(current);
			return true; 
		}			
	}
		
		FlowNetwork fn = new FlowNetwork(size);
		for (int i = 0; i < length; i++) {
			if (i != t) {
				for (int j = 0; j < length; j++) {
					if (j != t && j > i) {
						//System.out.println(i + ":" + j);
						fn.addEdge(new FlowEdge(size - 2, x, g[i][j]));
						sum += g[i][j];
						fn.addEdge(new FlowEdge(x, size - 1 - length + y, Double.POSITIVE_INFINITY));
						fn.addEdge(new FlowEdge(x, size - length + z, Double.POSITIVE_INFINITY));
						x++;
						z++;
					}
				}
				fn.addEdge(new FlowEdge(size - 1 - length + y, size - 1, w[t] + r[t] - w[i]));
				networkTeams[y] = teams[i];
				y++;
				z = y;

			}
		}
		int gameCombos = numGameCombos(length);
		FordFulkerson ff = new FordFulkerson(fn, size - 2, size - 1);
		if (sum == ff.value())
			return false;
		else {
			for (int v = gameCombos; v < size - 2; v++) {
				if (ff.inCut(v)) {
					subsets.add(networkTeams[v - gameCombos]);
				}
			}
			return true;
		}
	}

	private void validateTeam(String team) {
	if(!teamsIndices.contains(team))
		throw new IllegalArgumentException();
		
	}

	private int numGameCombos(int x) {
		int n = x - 1;
		return n * (n - 1) / 2;
	}

	public Iterable<String> certificateOfElimination(String team) // subset R of
	// teams that eliminates given team; null if not eliminated
	{validateTeam(team);
		if (isEliminated(team)) {
			HashSet<String> set=new HashSet<>(subsets);
			ArrayList<String> nodup=new ArrayList<>();
			for(String str:set) {
				nodup.add(str);
			}
			return nodup;
		}
		return null;

	}

	public static void main(String[] args) {
		 BaseballElimination division = new BaseballElimination("teams4.txt");
		    for (String team : division.teams()) {
		        if (division.isEliminated(team)) {
		            StdOut.print(team + " is eliminated by the subset R = { ");
		            for (String t : division.certificateOfElimination(team)) {
		                StdOut.print(t + " ");
		            }
		            StdOut.println("}");
		        }
		        else {
		            StdOut.println(team + " is not eliminated");
		        }
		    }
	}

}
