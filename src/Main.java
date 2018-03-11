/*Created by: Eoin Smyth
 * Created for the purpose of testing the JGraphtT library with OSM data for 4th year project
 */
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Set;

import org.jgrapht.*;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.*;
import org.jgrapht.io.CSVExporter;
import org.jgrapht.io.CSVFormat;
import org.jgrapht.io.ComponentNameProvider;
import org.jgrapht.io.IntegerComponentNameProvider;
import org.jgrapht.io.StringComponentNameProvider;

public class Main {

	public static void main(String[] args) {
		
		//SetUP
		
		//databaseSetUp();
		//dataSetUp();
        
		GraphTesting gt = new GraphTesting();
		
		//Simple String Graph
		DefaultDirectedGraph<String, DefaultEdge> stringGraph = gt.createStringGraph(new String[] {"v1", "v2", "v3", "v4", "v5"});
		//System.out.println(stringGraph.toString());
		
		//Checking if Graph is cyclical
		CycleDetector<String, DefaultEdge> cd = new CycleDetector<String, DefaultEdge>(stringGraph);
		System.out.println(cd.detectCycles());
		
		DBManager db = new DBManager("GraphTesting");
		
		//Creating A Graph with OSM Nodes
		ArrayList<OSMNode> nodes = new ArrayList<>();
		for (int i = 1; i <= 5; i++) {
			OSMNode node = db.getNodeById(i);
			nodes.add(node);
			//System.out.println(nodes.get(i - 1).getNodeID() + " / " + nodes.get(i - 1).getLat() + " / " + nodes.get(i - 1).getLon());
		}//End for
		
		Graph<OSMNode, DefaultEdge> nodeGraph = gt.createNodeGraph(nodes);
		//System.out.println(nodeGraph);
		
		//Creating a Graph with OSM Nodes grabbed using WayID
		ArrayList<OSMNode> nodes2 = db.getNodesbyWayId(4258427);
		ArrayList<OSMEdge> edges2 = new ArrayList<>();
		System.out.println(nodes2.size());
		for (int i = 0; i < nodes2.size() -1; i++) {
			System.out.println(nodes2.get(i) + "/ " + nodes2.get(i + 1) + " / " + (i - 1) + " / " + i);
			OSMEdge tempEdge = new OSMEdge(4258427, nodes2.get(i), nodes2.get(i + 1));
			edges2.add(tempEdge);
			nodes2.get(i).addEdge(tempEdge);
			nodes2.get(i + 1).addEdge(tempEdge);
		}//End for
		
		Graph<OSMNode, OSMEdge> edgeGraph2 = gt.createEdgeGraph(edges2);
		//System.out.println(nodeGraph2);

		//Creating a Full Graph using all OSMEdges and corresponding OSMNodes
		ArrayList<OSMNode> nodeList = db.getNodes();
		ArrayList<OSMEdge> edges = db.getEdges(nodeList);
		
		Graph<OSMNode, OSMEdge> edgeGraph = gt.createEdgeGraph(edges);
		//System.out.println(edgeGraph);

		MapViewer mv = new MapViewer(edgeGraph);
		
		//Exporting Graph
		try {
			exportGraph(edgeGraph);
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//End try catch

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

}//End Main
