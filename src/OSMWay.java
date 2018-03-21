/*OSMWay is a custom class for interacting with OSM ways.
 */

import java.util.ArrayList;

public class OSMWay {
	
	private long wayID;
	private ArrayList<OSMNode> nodes;
	private ArrayList<OSMEdge> edges;
	private OSMNode sourceNode, endNode;
	private String highway, landuse, name;
	
	/*
	 * 		CONSTRUCTORS
	 */
	
	public OSMWay(long passedWayID) {
		this.wayID = passedWayID;
	}//End OSMWay Constructor
	
	public OSMWay(long passedWayID, String passedName, String passedLanduse, String passedHighway) {
		this.wayID = passedWayID;
		this.name =passedName;
		this.landuse = passedLanduse;
		this.highway = passedHighway;
	}//End Constructor
	
	public OSMWay(long passedWayID, ArrayList<OSMNode> passedNodes) {
		this.wayID = passedWayID;
		this.nodes = passedNodes;
		this.setEdges(passedNodes);
		
		this.sourceNode = passedNodes.get(0);
		this.endNode = passedNodes.get(passedNodes.size());
	}//End OSMWay Constructor
	
	public OSMWay(long passedWayID, ArrayList<OSMNode> passedNodes, ArrayList<OSMEdge> passedEdges) {
		this.wayID = passedWayID;
		this.nodes = passedNodes;
		this.edges = passedEdges;
		
		this.sourceNode = passedNodes.get(0);
		
		int end =  passedNodes.size() - 1;
		this.endNode = passedNodes.get(end);
	}//End OSMWay Constructor
	
	public OSMWay(long passedWayID, ArrayList<OSMNode> passedNodes, ArrayList<OSMEdge> passedEdges, String lnd, String nm, String hway) {
		this.wayID = passedWayID;
		this.nodes = passedNodes;
		this.edges = passedEdges;
		
		this.sourceNode = passedNodes.get(0);
		
		int end =  passedNodes.size() - 1;
		this.endNode = passedNodes.get(end);
		
		this.landuse = lnd;
		this.name = nm;
		this.highway = hway;
	}//End OSMWay Constructor
	
	/*
	 * 		METHODS
	 */
	
	public long getWayID() {
		return this.wayID;
	}//End getWayID
	
	public void setNode(OSMNode passedNode) {
		this.nodes.add(passedNode);
	}//End setNode
	
	public void setNodes(ArrayList<OSMNode> passedNodes) {
		for (int i = 0; i < passedNodes.size(); i++) {
			setNode(passedNodes.get(i));
		}//End for
	}//End setNodes
	
	public ArrayList<OSMNode> getNodes() {
		return this.nodes;
	}//End getNodes
	
	public OSMNode getNode(long nodeID) {
		OSMNode node = new OSMNode();
		for (int i = 0; i < this.nodes.size(); i++) {
			if(nodeID == this.nodes.get(i).getNodeID()) {
				node = this.nodes.get(i);
			}//End if
		}//End for
		return node;
	}//End getNode
	
	public void setSourceNode(OSMNode node) {
		this.sourceNode = node;
	}//End setSourceNode
	
	public void setEndNode(OSMNode node) {
		this.endNode = node;
	}//End setEndNode
	
	public OSMNode getSourceNode() {
		return this.sourceNode;
	}//End getSourceNode
	
	public OSMNode setEndNode() {
		return this.endNode;
	}//End setEndNode
	
	public void setEdges(ArrayList<OSMNode> passedNodes) {
		for (int i = 1; i < passedNodes.size() ; i++) {
			OSMEdge tempEdge = new OSMEdge(this.wayID, passedNodes.get(i -1), passedNodes.get(i));
			this.edges.add(tempEdge);
			passedNodes.get(i-1).addEdge(tempEdge);
			passedNodes.get(i).addEdge(tempEdge);
		}//End for
	}//End setEdges
	
	public void setName(String nm) {
		this.name  = nm;
	}//End setName
	
	public String getName() {
		return this.name;
	}//End getName
	
	public void setLanduse(String lnd) {
		this.name  = lnd;
	}//End setLanduse
	
	public String getLanduse() {
		return this.landuse;
	}//End getLanduse
	
	public void setHighway(String hway) {
		this.name  = hway;
	}//End setHighway
	
	public String getHighway() {
		return this.highway;
	}//End getHighway
	
	public ArrayList<OSMEdge> getEdges() {
		return this.edges;
	}//End getEdges
	
    @Override
    public String toString()
    {
        return this.wayID + " : " + this.edges;
    }//End toString
	
}//End OSMEdge
