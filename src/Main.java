/*Created by: Eoin Smyth
 * Created for the purpose of testing the JGraphtT library with OSM data for 4th year project
 */
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jgrapht.*;
import org.jgrapht.alg.CycleDetector;
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
		
		DBManager db = new DBManager("GraphTesting");
		/*
		 *  Testing Ways
		ArrayList<OSMEdge >wayGraph = graphByWayID(gt, db, 264454262);
		double distance = 0.0;
		for (int i = 0; i < wayGraph.size(); i++) {
			distance = distance + wayGraph.get(i).getDistance();
		}
		System.out.println("WayGraph Distance: " + distance);
		MapViewer mv = new MapViewer(wayGraph);
		*/
		
		createFullGraph(gt, db);
		
		//Closing Database
		db.close();
	}//End main()
	
	//Sets up the postgres database by creating the tables and tests with simple data
	public static void databaseSetUp() {
		DBManager db = new DBManager("GraphTesting");
		
		db.createNodeTable();
		db.createWayTable();
		db.createEdgeTable();
		
		/*
		db.insert("waylist", new String[] {"2", "124", "12346"});
		db.selectNode("*", "null");
		*/
		
		db.close();
	}//End databaseSetUp()
	
	//Parses data from files and inserts it into the database
	public static void dataSetUp() {
		FileManager fm = new FileManager("resources/UseCaseNodes.csv", "resources/UseCaseWays.osm");
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
		ArrayList<OSMNode> nodeList = db.getNodes();
		ArrayList<OSMEdge> edges = db.getEdges(nodeList);
		ArrayList<OSMWay> ways = db.getWays(nodeList, edges);
		
		System.out.println(ways.size());
		
		//Creating ways
		//ArrayList<OSMWay> ways = createWays(edges);
		
		ArrayList<OSMEdge> wayEdges = new ArrayList<>();
		for (int i = 0; i < ways.size(); i++) {
			if (ways.get(i).getHighway().equals("null") || ways.get(i).getHighway().equals("service")) {
				
			} else {
				wayEdges.addAll(ways.get(i).getEdges());
			}
		}
		Graph<OSMNode, OSMEdge> wayGraph = gt.createEdgeGraph(wayEdges);
		Graph<OSMNode, OSMEdge> edgeGraph = gt.createEdgeGraph(edges);
		OSMNode waysource = wayEdges.get(500).getSourceNode();
		
		  //Graph Traversal + Map Viewing
		
	    OSMEdge[] edgeSet = edgeGraph.edgeSet().toArray(new OSMEdge[edgeGraph.edgeSet().size()]);
	    OSMNode source = edgeSet[500].getSourceNode();
    	OSMNode target = edgeSet[edgeSet.length - 1].getTargetNode();
		
    	/*
		ArrayList<OSMEdge> subGraph = gt.constructSubGraph(false, 1, source, target, new ArrayList<OSMEdge>());
		System.out.println(subGraph.size() + " : " + subGraph);
		MapViewer mv = new MapViewer(subGraph);
		*/

    	
    	ArrayList<OSMEdge> pincerGraph = gt.constructPincerGraph(1000, waysource, wayGraph);
    	//MapViewer mv = new MapViewer(pincerGraph);
    	
    	//MapViewer mv = new MapViewer(wayEdges);
    	//ArrayList<OSMEdge> randomGraph = gt.constructRandomWalk(1000000, source, edgeGraph, nodeList.size());
    	
    	//MapViewer mv = new MapViewer(randomGraph);
		//Exporting Graph
		try {
			exportGraph(edgeGraph);
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
