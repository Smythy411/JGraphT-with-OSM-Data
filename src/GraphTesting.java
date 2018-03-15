/*GraphTesting constructs the graphs using given information.
 */

import java.util.ArrayList;
import java.util.Random;

import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.traverse.ClosestFirstIterator;
import org.jgrapht.traverse.DegeneracyOrderingIterator;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.GraphIterator;
import org.jgrapht.traverse.RandomWalkIterator;

public class GraphTesting {
	
	public GraphTesting() {
		System.out.println("New GraphTesting");
	}//End constructor
  
	//Creates a Graph with OSMNode vertices and OSMEdge edges.
	public Graph<OSMNode, OSMEdge> createEdgeGraph(ArrayList<OSMEdge> edges) {
		
		System.out.println("Creating Graph with " + edges.size() + " edges");
		Graph<OSMNode, OSMEdge> g = new SimpleWeightedGraph<OSMNode, OSMEdge>(OSMEdge.class);
		
		for (int i = 0; i < edges.size(); i++) {
			OSMEdge edge = edges.get(i);
			g.addVertex(edge.getSourceNode());
			g.addVertex(edge.getTargetNode());
			g.addEdge(edge.getSourceNode(), edge.getTargetNode(), edge);
			
			OSMEdge edgeToWeight = g.getEdge(edge.getSourceNode(), edge.getTargetNode());
			Double weight = edge.getDistance();
			g.setEdgeWeight(edgeToWeight, weight);
		}//End for
		
		System.out.println("Graph Created");
		return g;
	}//End createNodeGraph
	
	//Creates a cyclical Graph with OSMNode nodes and DefaultEdge edges
	public Graph<OSMNode, DefaultEdge> createNodeGraph(ArrayList<OSMNode> vertices) {
		
		Graph<OSMNode, DefaultEdge> g = new SimpleGraph<OSMNode, DefaultEdge>(DefaultEdge.class);

		for (int i = 0; i < vertices.size(); i++) {
			g.addVertex(vertices.get(i));
			if (i >= 1) {
				g.addEdge(vertices.get(i - 1), vertices.get(i));
			}//End if
			if (i == vertices.size() - 1) {
				g.addEdge(vertices.get(i), vertices.get(0));
			}//End if
		}//End for
		return g;
	}//End createNodeGraph
	
	//Creates a cyclical String graph
	public DefaultDirectedGraph<String, DefaultEdge> createStringGraph(String[] vertices)
	{
		DefaultDirectedGraph<String, DefaultEdge> g = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);

		for (int i = 0; i < vertices.length; i++) {
			g.addVertex(vertices[i]);
			if (i >= 1) {
				g.addEdge(vertices[i - 1], vertices[i]);
			}//End if
			if (i == vertices.length - 1) {
				g.addEdge(vertices[i], vertices[0]);
			}//End if
		}//End for
		return g;
	}//End createStringGraph
	
	/*
	 * Random Walk Approach
	 */
	
	
	public ArrayList<OSMEdge> constructRandomWalk(int size, OSMNode source, Graph<OSMNode, OSMEdge> graph) {
		ArrayList<OSMNode> constructedGraph = new ArrayList<>();
		ArrayList<OSMEdge> edges = new ArrayList<>();
		
		GraphIterator<OSMNode, OSMEdge> iterator = new RandomWalkIterator<OSMNode, OSMEdge>(graph, source, true, size);
		int i = 0;
		while (iterator.hasNext()) {
			OSMNode node = iterator.next();
			if (node.getVisited() == true) {
				System.out.println("Node already visited");
			} else {
				constructedGraph.add(node);
				node.setVisited();
				i++;
			}
			
		}//End while
		System.out.println(constructedGraph);
		Double distance = 0.0;
		for (int j = 0; j < i - 1; j++) {
			if (distance <= 5.2) {
				OSMNode tempNode = constructedGraph.get(j);
				ArrayList<OSMEdge> tempEdges =  tempNode.getEdges();
				for (int k = 0; k < tempEdges.size(); k++) {
					OSMEdge tempEdge = tempEdges.get(k);
					OSMNode neighbour = tempEdge.getNeighbour(tempNode);
					if (constructedGraph.get(j + 1) == neighbour) {
						edges.add(tempEdge);
						distance = distance + tempEdge.getDistance();
					}//End if 
				}//End inner for
			} else {
				System.out.println("Max disatance reached : " + distance);
				break;
			}//End if else
		}//End outer for
		System.out.println(edges.size() + " : " + constructedGraph.size());
		System.out.println("Max disatance reached : " + distance);
		
		return edges;
	}//End ConstructRandomWalk()
	
	/*
	 * Depth First Approach
	 */
	
	public ArrayList<OSMEdge> constructPincerGraph(int size, OSMNode source, Graph<OSMNode, OSMEdge> graph) {
		ArrayList<OSMNode> constructedGraph = new ArrayList<>();
		ArrayList<OSMEdge> edges = new ArrayList<>();
		
		GraphIterator<OSMNode, OSMEdge> iterator = new DepthFirstIterator<OSMNode, OSMEdge>(graph);
		int i = 0;
		while (iterator.hasNext()) {
			if (i < size) {
				constructedGraph.add(iterator.next());
				i++;
				System.out.println(constructedGraph);
			} else {
				for (int j = 0; j < size - 1; j++) {
					OSMNode tempNode = constructedGraph.get(j);
					ArrayList<OSMEdge> tempEdges =  tempNode.getEdges();
					for (int k = 0; k < tempEdges.size(); k++) {
						OSMEdge tempEdge = tempEdges.get(k);
						OSMNode neighbour = tempEdge.getNeighbour(tempNode);
						if (constructedGraph.get(j + 1) == neighbour) {
							System.out.println(tempEdge.getDistance());
							edges.add(tempEdge);
						}//End if 
					}//End inner for
				}//End outer for
				System.out.println(edges.size() + " : " + constructedGraph.size());
				break;
			}//End if else
			
		
		
		/*for (int i = 0; i < nextEdges.size(); i++) {
			Random rand = new Random();
			int choice = rand.nextInt(nextEdges.size());
			
			System.out.println(i + " : " + choice);
			System.out.println(nextEdges.get(i));
		}*/
		}
		return edges;
	}
	
	public ArrayList<OSMEdge> constructSubGraph(boolean finished, int size, OSMNode source, OSMNode target, ArrayList<OSMEdge> full) {
		if (finished) {
			return full;
		}//end if
		ArrayList<OSMEdge> edges = target.getEdges();
		
		//Base Case
		if (size <= 0) {
			finished = true;
		}//End if
		
		if (edges.size() == 1) {
			//System.out.println("Dead End");
			target = edges.get(0).getTargetNode();
			return full;
		} else {
			for (int i = 0; i < edges.size(); i++) {
				if (finished) {
					return full;
				}//end if
				
				target = edges.get(i).getTargetNode();
				
				if (edges.equals(target.getEdges())) {
					//System.out.println("Edge Already visited");
				} else {
					ArrayList<OSMEdge> subEdges = target.getEdges();
					for (int j = 0; j < subEdges.size(); j++) {
						if (full.contains(subEdges.get(j))) {
							
						} else {
							full.add((subEdges.get(j)));
							//System.out.println(full);
							constructSubGraph(finished, size--, target, subEdges.get(j).getTargetNode(), full);
						}//End if else
					}//End for
				}//End if else
			}//End for
		}//End if else
		
		return full;
		
	}//End constructG
	
}//End GraphTesting