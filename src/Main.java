/*Created by: Eoin Smyth
 * Created for the purpose of testing the JGraphtT library with OSM data for 4th year project
 */
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.*;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.*;
import org.jgrapht.io.CSVExporter;
import org.jgrapht.io.CSVFormat;
import org.jgrapht.io.ComponentNameProvider;
import org.jgrapht.io.IntegerComponentNameProvider;
import org.jgrapht.io.StringComponentNameProvider;
import org.jgrapht.traverse.DepthFirstIterator;

public class Main {

	public static void main(String[] args) {
		
		//SetUP
		
		//databaseSetUp();
		//dataSetUp();
        
		GraphTesting gt = new GraphTesting();
		
		DBManager db = new DBManager("drfr-dublin");
		
		createFullGraph(gt, db);
		
		//Closing Database
		db.close();
		
	}//End main()
	
	//Sets up the postgres database by creating the tables and tests with simple data
	public static void databaseSetUp() {
		DBManager db = new DBManager("drfr-dublin");
		
		db.createNodeTable();
		db.createWayTable();
		db.createEdgeTable();
		db.createWaysTable();
		
		/*
		db.insert("waylist", new String[] {"2", "124", "12346"});
		db.selectNode("*", "null");
		*/
		
		db.close();
	}//End databaseSetUp()
	
	//Parses data from files and inserts it into the database
	public static void dataSetUp() {
		FileManager fm = new FileManager("resources/dublinNodes.csv", "resources/dublinWays.osm");
		//fm.openCSVFile();
		fm.parseXml();
	}//End dataSetUp()
	
	//Exporting Graph to CSV File as an Adjacency List. Could be useful for transport.
	//Importing Graph from CSV files is also supported
	public static void exportGraph(Graph g) throws FileNotFoundException, UnsupportedEncodingException {
		ComponentNameProvider<OSMNode> nodeName = new StringComponentNameProvider();
		ComponentNameProvider<OSMEdge> edgeName = new StringComponentNameProvider();
		
		CSVExporter cE = new CSVExporter(nodeName, CSVFormat.ADJACENCY_LIST, ',');
		cE.setEdgeIDProvider(edgeName);
		
		PrintWriter writer = new PrintWriter("resources/graph.csv", "UTF-8");
		cE.exportGraph(g, writer);
	}//End exportGraph
	
	//Creating a Full Graph using all OSMEdges and corresponding OSMNodes
	public static void createFullGraph(GraphTesting gt, DBManager db) {
		ArrayList<OSMNode> nodeList = db.getNodesByBoundary("53.2745", "-6.3553", "53.2897", "-6.3302");
		System.out.println(nodeList.size());
		Map<Long, OSMNode> nodeMap = new HashMap<Long, OSMNode>();
		for (int i = 0; i < nodeList.size(); i++) {
			OSMNode node = nodeList.get(i);
			nodeMap.put(node.getNodeID(), node);
		}
		
		ArrayList<OSMEdge> edges = db.getEdgesByBoundary(nodeMap, "53.2745", "-6.3553", "53.2897", "-6.3302");
		System.out.println(edges.size());
		
		ArrayList<OSMWay> ways = db.getWaysByBoundary(nodeList, edges, "53.2745", "-6.3553", "53.2897", "-6.3302");
		System.out.println(ways.size());
		
		double sourceLat = 53.279;
		double sourceLon = -6.346;
		
		OSMNode closestNode = new OSMNode();
		double closestDistance = 0.5;
		
		ArrayList<OSMEdge> wayEdges = new ArrayList<>();
		System.out.println("Finding Closest Node");
		for (int i = 0; i < ways.size(); i++) {
			if (ways.get(i).getHighway().equals("null") || ways.get(i).getHighway().equals("service") || 
					ways.get(i).getHighway().equals("primary")) {
				
			} else {
				ArrayList<OSMEdge> edgesToAdd = ways.get(i).getEdges();
				wayEdges.addAll(edgesToAdd);
				
				for (int j = 0; j < edgesToAdd.size(); j++) {
					if (j == 0) {
						OSMNode firstNode = edgesToAdd.get(j).getSourceNode();
						double checkDistance = Haversine.distance(sourceLat, sourceLon, Double.parseDouble(firstNode.getLat()), Double.parseDouble(firstNode.getLon()));
						if (checkDistance < closestDistance) {
							closestDistance = checkDistance;
							closestNode = firstNode;
						}
					}
					OSMNode checkNode = edgesToAdd.get(j).getTargetNode();
					double checkDistance = Haversine.distance(sourceLat, sourceLon, Double.parseDouble(checkNode.getLat()), Double.parseDouble(checkNode.getLon()));
					if (checkDistance < closestDistance) {
						closestDistance = checkDistance;
						closestNode = checkNode;
					}
				}
			}
		}
		System.out.println("Closest Node is " + closestNode.getLat() + ", " + closestNode.getLon() + " which is " + closestDistance + " from current Location");
		
		
		Graph<OSMNode, OSMEdge> wayGraph = gt.createEdgeGraph(wayEdges);
		OSMNode waysource = wayEdges.get(0).getSourceNode();
		
		  //Graph Traversal + Map Viewing
		OSMEdge[] wayEdgeSet = wayGraph.edgeSet().toArray(new OSMEdge[wayGraph.edgeSet().size()]);
		Route DJKRoute = gt.constructDJKRoute(wayGraph, wayEdgeSet, closestNode, 5.0);
    	//ArrayList<OSMEdge> pincerGraph = gt.constructPincerGraph(2000, closestNode, wayGraph);
		System.out.println(DJKRoute.getRoute().size());
    	MapViewer mv = new MapViewer(DJKRoute.getRoute());
    	
    	//2.35 // 16.04
		/*	Testing
		Graph<OSMNode, OSMEdge> edgeGraph = gt.createEdgeGraph(edges);
	    OSMEdge[] edgeSet = edgeGraph.edgeSet().toArray(new OSMEdge[edgeGraph.edgeSet().size()]);
	    OSMNode source = edgeSet[500].getSourceNode();
    	OSMNode target = edgeSet[edgeSet.length - 1].getTargetNode();
		
    	
		ArrayList<OSMEdge> subGraph = gt.constructSubGraph(false, 1, source, target, new ArrayList<OSMEdge>());
		System.out.println(subGraph.size() + " : " + subGraph);
		MapViewer mv = new MapViewer(subGraph);
    	
    	//MapViewer mv = new MapViewer(wayEdges);
    	//ArrayList<OSMEdge> randomGraph = gt.constructRandomWalk(1000000, source, edgeGraph, nodeList.size());
    	
    	//MapViewer mv = new MapViewer(randomGraph);
    	  */
    	
		//Exporting Graph
		try {
			exportGraph(wayGraph);
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//End try catch
		
	}//End createFullGraph
	
	//Creating a Graph with OSM Nodes grabbed using WayID (*TESTING*)
	public static ArrayList<OSMEdge> graphByWayID(GraphTesting gt, DBManager db, long wayID) {
		ArrayList<OSMNode> nodes = db.getNodesbyWayId(wayID);
		ArrayList<OSMEdge> edges = new ArrayList<>();
		for (int i = 0; i < nodes.size() -1; i++) {
			//System.out.println(nodes.get(i) + "/ " + nodes.get(i + 1) + " / " + (i - 1) + " / " + i);
			OSMEdge tempEdge = new OSMEdge(4258427, nodes.get(i), nodes.get(i + 1));
			edges.add(tempEdge);
			nodes.get(i).addEdge(tempEdge);
			nodes.get(i + 1).addEdge(tempEdge);
		}//End for
		
		Graph<OSMNode, OSMEdge> edgeGraph = gt.createEdgeGraph(edges);
		return edges;
	}//End graphByWayID
	
	//Creating ways
	public static ArrayList<OSMWay> createWays(ArrayList<OSMEdge> edges) {
		ArrayList<OSMWay> ways = new ArrayList<OSMWay>();
		long wayID = edges.get(0).getWayID();
		
		ArrayList<OSMNode> tempNodes = new ArrayList<>();
		ArrayList<OSMEdge> tempEdges = new ArrayList<>();
		for (int i = 0; i < edges.size(); i++) {
			long tempWID = edges.get(i).getWayID();
			
			if (wayID == tempWID) {
				tempNodes.add(edges.get(i).getSourceNode());
				tempEdges.add(edges.get(i));
			} else {
				tempNodes.add(edges.get(i -1).getTargetNode());
				OSMWay way = new OSMWay(wayID, tempNodes, tempEdges);
				ways.add(way);
				
				wayID = tempWID;
			}//end if else
		}//end for
		
		System.out.println(ways.size() + " ways created.");
		
		return ways;
	}//End createWays

}//End Main
