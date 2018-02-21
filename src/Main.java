import java.util.ArrayList;
import java.util.Set;

import org.jgrapht.*;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.*;

public class Main {

	public static void main(String[] args) {
		
		//SetUP
		
		//databaseSetUp();
		//dataSetUp();
		
		GraphTesting gt = new GraphTesting();
		
		//Simple String Graph
		DefaultDirectedGraph<String, DefaultEdge> stringGraph = gt.createStringGraph(new String[] {"v1", "v2", "v3", "v4", "v5"});
		System.out.println(stringGraph.toString());
		
		//Checking if Graph is cyclical
		CycleDetector cd = new CycleDetector(stringGraph);
		System.out.println(cd.detectCycles());
		
		DBManager db = new DBManager("GraphTesting");
		
		//Creating A Graph with OSM Nodes
		ArrayList<OSMNode> nodes = new ArrayList<>();
		for (int i = 1; i <= 5; i++) {
			OSMNode node = db.getNodeById(i);
			nodes.add(node);
			System.out.println(nodes.get(i - 1).getNodeID() + " / " + nodes.get(i - 1).getLat() + " / " + nodes.get(i - 1).getLon());
		}//End for
		
		Graph<OSMNode, DefaultEdge> nodeGraph = gt.createNodeGraph(nodes);
		System.out.println(nodeGraph);
		
		//Creating a Graph with OSM Nodes grabbed using WayID
		ArrayList<OSMNode> nodes2 = db.getNodesbyWayId(4258427);
		for (int i = 0; i < nodes.size(); i++) {
			System.out.println(nodes.get(i).getNodeID() + " / " + nodes.get(i).getLat() + " / " + nodes.get(i).getLon());
		}//End for
		
		Graph<OSMNode, DefaultEdge> nodeGraph2 = gt.createNodeGraph(nodes2);
		System.out.println(nodeGraph2);

		//Creating a Full Graph using all OSMEdges and corresponding OSMNodes
		
		ArrayList<OSMEdge> edges = db.getEdges();
		
		Graph<OSMNode, OSMEdge> edgeGraph = gt.createEdgeGraph(edges);
		System.out.println(edgeGraph);

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
		fm.openCSVFile();
		fm.parseXml();
	}//End dataSetUp()

}//End Main
