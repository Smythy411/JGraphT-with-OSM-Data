import java.util.ArrayList;
import java.util.List;

import org.jgrapht.GraphPath;

public class Route {
	
	private ArrayList<OSMEdge> edges;
	private double weight;
	
	public Route() {
		this.edges = new ArrayList<OSMEdge>();
	}
	
	public void setWeight(double w) {
		this.weight = w;
	}
	
	public double getWeight() {
		return this.weight;
	}
	
	public ArrayList<OSMEdge> getRoute() {
		return this.edges;
	}
	
	public void addToRoute(OSMEdge edge) {
		double weightToAdd = edge.getDistance();
		this.weight += weightToAdd;
		this.edges.add(edge);
	}
	
	public void addWeightlessToRoute(OSMEdge edge) {
		this.edges.add(edge);
	}
	
	public void addToRoute(GraphPath<OSMNode, OSMEdge> path) {
		List<OSMEdge> list = path.getEdgeList();
		
		double weightToAdd = path.getWeight();
		this.weight += weightToAdd;
		
		this.edges.addAll(list);
	}
	
	public void addToRoute(ArrayList<OSMEdge> path) {
		double weightToAdd = 0.0;
		for (int i = 0; i < path.size(); i++) {
			weightToAdd += path.get(i).getDistance();
		}
		this.weight += weightToAdd;
		
		this.edges.addAll(path);
	}
	
	public void addToRoute(List<OSMEdge> path) {
		double weightToAdd = 0.0;
		for (int i = 0; i < path.size(); i++) {
			weightToAdd += path.get(i).getDistance();
		}
		this.weight += weightToAdd;
		
		this.edges.addAll(path);
	}
	
	public void removeFromRoute(ArrayList<OSMEdge> edgesToRemove) {
		double weightToRemove = 0.0;
		for (int i = 0; i < edgesToRemove.size(); i++) {
			weightToRemove += edgesToRemove.get(i).getDistance();
		}
		this.weight = this.weight - weightToRemove;
		this.edges.removeAll(edgesToRemove);
	}
}
