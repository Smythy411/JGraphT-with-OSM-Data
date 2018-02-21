import java.util.ArrayList;

import org.jgrapht.graph.DefaultEdge;
public class OSMWay {
	
	private int id;
	private long wayID;
	private ArrayList<OSMNode> nodes;
	
	public OSMWay(int passedId, long passedWayID) {
		this.id = passedId;
		this.wayID = passedWayID;
	}//End OSMEdge Constructor
	
	public OSMWay(int passedId, long passedWayID, ArrayList<OSMNode> passedNodes) {
		this.id = passedId;
		this.wayID = passedWayID;
		this.nodes = passedNodes;
	}//End OSMEdge Constructor
	
	public long getWayID() {
		return this.wayID;
	}//End getWayID
	
	public void setNode(OSMNode passedNode) {
		nodes.add(passedNode);
	}//End setNode
	
	public void setNodes(ArrayList<OSMNode> passedNodes) {
		for (int i = 0; i < passedNodes.size(); i++) {
			setNode(passedNodes.get(i));
		}//End for
	}//End setNodes
	
	public ArrayList<OSMNode> getNodes() {
		return nodes;
	}//End getNodes`
	
	public OSMNode getNode(long nodeID) {
		OSMNode node = new OSMNode();
		for (int i = 0; i < nodes.size(); i++) {
			if(nodeID == nodes.get(i).getNodeID()) {
				node = nodes.get(i);
			}//End if
		}//End for
		return node;
	}//End getNode
	

}//End OSMEdge
