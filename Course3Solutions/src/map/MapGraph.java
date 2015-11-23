/**
 * 
 */
package map;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * @author UCSD MOOC development team
 * 
 * A class which reprsents a graph of geographic locations
 * Nodes in the graph are intersections between 
 *
 */
public class MapGraph {
	private HashMap<GeographicPoint,MapNode> pointNodeMap;
	private HashMap<String,MapNode> streetNodeMap;
	private HashSet<MapEdge> edges;
	
	// Need to be able to look up nodes by lat/lon or by roads 
	// that they are part of.
	
	public MapGraph()
	{
		streetNodeMap = new HashMap<String,MapNode>();
		pointNodeMap = new HashMap<GeographicPoint,MapNode>();
		edges = new HashSet<MapEdge>();
	}
	
	public int getNumVertices()
	{
		return pointNodeMap.values().size();
	}
	
	public int getNumEdges()
	{
		return edges.size();
	}
	
	public void printNodes()
	{
		System.out.println("****PRINTING NODES ********");
		System.out.println("There are " + getNumVertices() + " Nodes: \n");
		for (GeographicPoint pt : pointNodeMap.keySet()) 
		{
			MapNode n = pointNodeMap.get(pt);
			System.out.println(n);
		}
	}
	
	public void printEdges()
	{
		System.out.println("******PRINTING EDGES******");
		System.out.println("There are " + getNumEdges() + " Edges:\n");
		for (MapEdge e : edges) 
		{
			System.out.println(e);
		}
		
	}
	
	/** Add a node corresponding to an intersection */
	public void addNode(Collection<String> roadNames, double latitude, double longitude)
	{
		GeographicPoint pt = new GeographicPoint(latitude, longitude);
		this.addNode(roadNames, pt);
	}
	
	public void addNode(Collection<String> roadNames, GeographicPoint point )
	{
		
		MapNode n = pointNodeMap.get(point);
		if (n == null) {
			n = new MapNode(point);
			pointNodeMap.put(point, n);
		}
		
		for (String name : roadNames) {
			n.addStreet(name);
			streetNodeMap.put(name, n);
		}
		
	}
	
	/** Add an edge representing a segment of a road.
	 * The corresponding Nodes must have already been added to the graph.
	 * @param roadName
	 */
	public void addEdge(double lat1, double lon1, 
						double lat2, double lon2, String roadName, String roadType) 
	{
		// Find the two Nodes associated with this edge.
		// XXX An alternative is to have this method take the nodes themselves?
		// I think setting up the graph will be all internal
		// so maybe we don't need to expose this method?
		GeographicPoint pt1 = new GeographicPoint(lat1, lon1);
		GeographicPoint pt2 = new GeographicPoint(lat2, lon2);
		
		MapNode n1 = pointNodeMap.get(pt1);
		MapNode n2 = pointNodeMap.get(pt2);

		// XXX Should error check and throw exception here if the points 
		// aren't already in the graph.
		
		addEdge(n1, n2, roadName, roadType);
		
	}
	
	public void addEdge(GeographicPoint pt1, GeographicPoint pt2, String roadName,
			String roadType) {
		
		MapNode n1 = pointNodeMap.get(pt1);
		MapNode n2 = pointNodeMap.get(pt2);

		// XXX Should error check and throw exception here if the points 
		// aren't already in the graph.
		
		addEdge(n1, n2, roadName, roadType);
	}
	
	
	public boolean isNode(GeographicPoint point)
	{
		return pointNodeMap.containsKey(point);
	}
	
	//XXX will probably need a similar isNode method for the intersection

	// Add an edge when you already know the nodes involved in the edge
	private void addEdge(MapNode n1, MapNode n2, String roadName, String roadType)
	{
		// We need to be careful to only add edges once because 
		// we have no check for duplicate edges
		MapEdge edge = new MapEdge(roadName, roadType, n1, n2);
		edges.add(edge);
	}

	
	// Add an edge when you already know the nodes involved in the edge
	private void addEdge(MapNode n1, MapNode n2, List<GeographicPoint>ptsOnEdge,
			String roadName, String roadType)
	{
		// We need to be careful to only add edges once because 
		// we have no check for duplicate edges
		MapEdge edge = new MapEdge(roadName, roadType, ptsOnEdge, n1, n2);
		edges.add(edge);
	}

	/** Returns the nodes in terms of their geographic locations */
	public Collection<GeographicPoint> getNodes() {
		return pointNodeMap.keySet();
	}
	
	private Set<MapNode> getNeighbors(MapNode node)
	{
		return node.getNeighbors();
	}
	
	public GeographicPath bfs(GeographicPoint start, GeographicPoint goal)
	{
		// Set up
		if (start == null || goal == null) 
			throw new NullPointerException("Cannot find route from or to null node");
		MapNode startNode = pointNodeMap.get(start);
		MapNode endNode = pointNodeMap.get(goal);
		if (startNode == null) {
			System.err.println("Start node " + start + " does not exist");
			return null;
		}
		if (endNode == null) {
			System.err.println("End node " + goal + " does not exist");
			return null;
		}

		HashMap<MapNode,MapEdge> parentMap = new HashMap<MapNode,MapEdge>();
		Queue<MapNode> toExplore = new LinkedList<MapNode>();
		HashSet<MapNode> visited = new HashSet<MapNode>();
		toExplore.add(startNode);
		MapNode next = null;
		while (!toExplore.isEmpty()) {
			next = toExplore.remove();
			if (next.equals(endNode)) break;
			Set<MapEdge> edges = next.getEdges();
			for (MapEdge e : edges) {
				MapNode neighbor = e.getOtherNode(next);
				if (!visited.contains(neighbor)) {
					visited.add(neighbor);
					parentMap.put(neighbor, e);
					toExplore.add(neighbor);
				}
			}
		
		}
		if (!next.equals(endNode)) {
			System.out.println("No path found from " +start+ " to " + goal);
			return null;
		}
		// Reconstruct the parent path
		GeographicPath path = 
				reconstructPath(parentMap, startNode, endNode);
		
		
		// TODO implement this method
		return path;
	}
	
	private GeographicPath reconstructPath(HashMap<MapNode,MapEdge> parentMap,
			MapNode start, MapNode goal)
	{
		GeographicPath path = new GeographicPath();
		MapNode current = start;
		LinkedList<GeographicPoint> intersections = 
				new LinkedList<GeographicPoint>();
		LinkedList<GeographicPoint> allPoints = 
				new LinkedList<GeographicPoint>();
		
		while (!current.equals(goal)) {
			intersections.addFirst(current.getLocation());
			MapEdge backEdge = parentMap.get(current);
			
			//current = 
		}
		path.addNextIntersection(current.getLocation());
		return path;
	}
	
	public void printEdgePointsToFile(String filename)
	{
	
		try {
			PrintWriter writer = new PrintWriter(filename, "UTF-8");
			for (MapEdge e : edges) {
				writer.println(e.getPoint1() + " " + e.getPoint2());
			}	
		}
		catch (Exception e) {
			System.out.println("Exception opening file " + e);
		}
	
	}
	
	public static void main(String[] args)
	{
		System.out.print("Making a new map...");
		MapGraph theMap = new MapGraph();
		System.out.print("DONE. \nLoading the map...");
		HashMap theRoads = new HashMap<GeographicPoint,RoadSegment>();
		MapLoader.loadMap("data/test.map", theMap, theRoads);
		System.out.println("DONE.");
		
		//System.out.println("Num nodes: " + theMap.getNumVertices());
		//System.out.println("Num edges: " + theMap.getNumEdges());
		//theMap.printEdgePointsToFile("data/santa_monica.intersections.map");
		theMap.printNodes();
		theMap.printEdges();
		
	}
	
}
