/*GraphTesting constructs the graphs using given information.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jgrapht.*;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.scoring.Coreness;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.*;
import org.jgrapht.traverse.ClosestFirstIterator;
import org.jgrapht.traverse.DegeneracyOrderingIterator;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.GraphIterator;
import org.jgrapht.traverse.RandomWalkIterator;

public class GraphTesting {
	
	private double distance;
	
	public GraphTesting() {
		System.out.println("New GraphTesting");
		this.distance = 0.0;
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
	
	
	public ArrayList<OSMEdge> constructRandomWalk(int size, OSMNode source, Graph<OSMNode, OSMEdge> graph, int nodeCount) {
		MapViewer test = new MapViewer(source);
		ArrayList<OSMNode> constructedGraph = new ArrayList<>();
		
		GraphIterator<OSMNode, OSMEdge> iterator = new RandomWalkIterator<OSMNode, OSMEdge>(graph, source, true, size);
		constructedGraph.add(source);
		int i = 0;
		while (iterator.hasNext()) {
			if (i >= nodeCount) {
				break;
			} else {
				OSMNode node = iterator.next();
				if (node.getVisited() == true) {
					//System.out.println("Node already visited");
				} else {
					constructedGraph.add(node);
					node.setVisited();
					i++;
				}//End inner if else
			}//End outer if else
			
		}//End while
		
		ArrayList<OSMEdge> edges = constructWalk(i, 5.0, constructedGraph, test);
		
		return edges;
	}//End ConstructRandomWalk()
	
	/*
	 * Depth First Approach
	 */
	
	public ArrayList<OSMEdge> constructPincerGraph(int size, OSMNode source, Graph<OSMNode, OSMEdge> graph) {
		ArrayList<OSMEdge> edges = new ArrayList<>();
		MapViewer mv = new MapViewer(source);
		ArrayList<OSMNode> constructedGraph = new ArrayList<>();
		
		GraphIterator<OSMNode, OSMEdge> iterator = new DepthFirstIterator<OSMNode, OSMEdge>(graph, source);
		constructedGraph.add(source);
		int i = 0;
		
		Coreness<OSMNode, OSMEdge> core = new Coreness<OSMNode, OSMEdge>(graph);
		
		while (iterator.hasNext()) {
			if (i >= size) {
				break;
			} else {
				OSMNode node = iterator.next();
				if (node.getVisited() == true) {
					//System.out.println("Node already visited");
				} else {
					if (core.getVertexScore(node) == 0 || core.getVertexScore(node) == 1) {
						//System.out.println("Dead End");
					} else {
						constructedGraph.add(node);
						node.setVisited();
						i++;
					}
				}//End inner if else
			}//End outer if else
			
		}//End while
		
		double currentDist = 2.5;
		while (this.distance < 5.0) {
			if (currentDist == 5.0) {
				break;
			}
			mv.reset();
			currentDist = currentDist + 0.1;
			
			edges = constructWalk(i, currentDist, constructedGraph, mv);
			
			if (isPath(graph, edges, source) ==  true) {
				System.out.println("Removing");
				graph.removeAllEdges(edges);
			}
			
			
	    	DijkstraShortestPath dj = new DijkstraShortestPath(graph);
	    	GraphPath<OSMNode, OSMEdge> gp = dj.getPath(edges.get(edges.size() - 1).getTargetNode(), source);
	    	if (gp != null) {
		    	List<OSMEdge> sp = gp.getEdgeList();
		    	ArrayList<OSMEdge> spEdges =  new ArrayList<>();
		    	spEdges.addAll(sp);
		    	
		    	for (int j = 0; j < spEdges.size(); j++) {
					OSMEdge tempEdge = spEdges.get(j);
					edges.add(tempEdge);
					distance = distance + tempEdge.getDistance();
					mv.updateMap(tempEdge);
		    	}
	    	} else {
	    		System.out.println("Dead End");
	    	}
	    	
	    	//System.out.println("Full Distance: " + this.distance);
		}
    	
		System.out.println("Full Distance: " + this.distance);
		return edges;
	}//End constructPincerGraph
	
	public boolean isPath(Graph<OSMNode, OSMEdge> graph, ArrayList<OSMEdge> edges, OSMNode source) {
		Graph<OSMNode, OSMEdge> tempGraph = new AsSubgraph(graph);
		tempGraph.removeAllEdges(edges);
		
    	DijkstraShortestPath dj = new DijkstraShortestPath(tempGraph);
    	if (dj.getPath(edges.get(edges.size() - 1).getTargetNode(), source) != null) {
    		return true;
    	} else {
    		return false;
    	}
	}
	
	public ArrayList<OSMEdge> constructWalk(int i, double targetDistance, ArrayList<OSMNode> constructedGraph, MapViewer mv) {
		ArrayList<OSMEdge> edges = new ArrayList<>();
		
		this.distance = 0.0;
		for (int j = 0; j < i - 1; j++) {
			if (distance <= targetDistance) {
				OSMNode tempNode = constructedGraph.get(j);
				ArrayList<OSMEdge> tempEdges =  tempNode.getEdges();
				if (tempEdges.size() == 1) {
					//System.out.println("Dead End");
					//test.reset();
				} else {
					for (int k = 0; k < tempEdges.size(); k++) {
						OSMEdge tempEdge = tempEdges.get(k);
						OSMNode neighbour = tempEdge.getNeighbour(tempNode);
						if (constructedGraph.get(j + 1) == neighbour) {
							edges.add(tempEdge);
							distance = distance + tempEdge.getDistance();
							mv.updateMap(tempEdge);
						}//End if 
					}//End inner for
				}//end if else
			} else {
				break;
			}//End if else
		}//End outer for
		System.out.println("Depth-First Distance : " + distance);
		//System.out.println(edges.size() + " : " + constructedGraph.size());
		
		return edges;
	}//End constructWalk
	
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