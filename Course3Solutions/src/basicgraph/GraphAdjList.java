package basicgraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** A class that implements a directed graph. 
 * The graph may have self-loops, parallel edges. 
 * Vertices are labeled by integers 0 .. n-1. 
 * The edges of the graph are not labeled.
 * Representation of edges is left abstract.
 * 
 * @author UC San Diego Intermediate Programming MOOC team
 *
 */
public class GraphAdjList extends Graph {


	private Map<Integer,ArrayList<Integer>> adjListsMap;
	
	/** Create a new empty Graph */
	public GraphAdjList () {
		adjListsMap = new HashMap<Integer,ArrayList<Integer>>();
	}
	
	public void implementAddVertex() {
		int v = getNumVertices();
		// System.out.println("Adding vertex "+v);
		ArrayList<Integer> neighbors = new ArrayList<Integer>();
		adjListsMap.put(v,  neighbors);
	}
	
	public void implementAddEdge(int v, int w) {
		(adjListsMap.get(v)).add(w);

	}
	
	public List<Integer> getNeighbors(int v) {
		return new ArrayList<Integer>(adjListsMap.get(v));
	}

	
	public String adjacencyString() {
		String s = "Adjacency list: ";
		for (int v : adjListsMap.keySet()) {
			s += "\n\t"+v+": ";
			for (int w : adjListsMap.get(v)) {
				s += w+", ";
			}
		}
		return s;
	}

	public List<Integer> getDistance2(int v) {
		List<Integer> distance2 = new ArrayList<Integer>();
		for (int w : adjListsMap.get(v)) {
			for (int u : adjListsMap.get(w)) {
				if (!distance2.contains(u)) {
					distance2.add(u);
				}
			}
		}
		System.out.println(distance2);
		return distance2;
	}


}