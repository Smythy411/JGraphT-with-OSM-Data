import java.net.*;
import java.util.ArrayList;

import org.jgrapht.*;
import org.jgrapht.graph.*;

public class GraphTesting {
	
	public GraphTesting() {
		System.out.println("New GraphTesting");
	}
  
	public static Graph<OSMNode, OSMEdge> createEdgeGraph(ArrayList<OSMEdge> edges) {
		
		System.out.println("Creating Graph with " + edges.size() + " edges");
		Graph<OSMNode, OSMEdge> g = new SimpleGraph<OSMNode, OSMEdge>(OSMEdge.class);
		
		for (int i = 0; i < edges.size(); i++) {
			OSMEdge edge = edges.get(i);
			g.addVertex(edge.getSourceNode());
			g.addVertex(edge.getTargetNode());
			g.addEdge(edge.getSourceNode(), edge.getTargetNode(), edge);
			System.out.println("Edge " + i + "..." + edge.getWayID());
		}//End for
		
		System.out.println("Graph Created");
		return g;
	}//End createNodeGraph
	
	public static Graph<OSMNode, DefaultEdge> createNodeGraph(ArrayList<OSMNode> vertices) {
		
		Graph<OSMNode, DefaultEdge> g = new SimpleGraph<OSMNode, DefaultEdge>(DefaultEdge.class);

		for (int i = 0; i < vertices.size(); i++) {
			g.addVertex(vertices.get(i));
			if (i >= 1) {
				g.addEdge(vertices.get(i - 1), vertices.get(i));
			}
			if (i == vertices.size() - 1) {
				g.addEdge(vertices.get(i), vertices.get(0));
			}
		}//End for
		return g;
	}//End createNodeGraph
	
	public static DefaultDirectedGraph<String, DefaultEdge> createStringGraph(String[] vertices)
	{
		DefaultDirectedGraph<String, DefaultEdge> g = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);

		for (int i = 0; i < vertices.length; i++) {
			g.addVertex(vertices[i]);
			if (i >= 1) {
				g.addEdge(vertices[i - 1], vertices[i]);
			}
			if (i == vertices.length - 1) {
				g.addEdge(vertices[i], vertices[0]);
			}
		}//End for
		return g;
	}//End createStringGraph
}//End GraphTesting