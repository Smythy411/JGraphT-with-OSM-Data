/*OSMEdge class extends the Default Edge class to work better with OSMData
 * and allows for easier testing
 */

import org.jgrapht.graph.DefaultEdge;

public class OSMEdge extends DefaultEdge{
	
	private static final long serialVersionUID = 1L;
	
	private long edgeID;
	private long wayID;
	OSMNode sourceNode, targetNode;
	
	/*
	 * 		CONSTRUCTORS
	 */
	
	public OSMEdge() {
		super();
	}//End constructor
	
	public OSMEdge(long wID) {
		this.wayID = wID;
	}//End constructor
	
	public OSMEdge(long eID, long wID) {
		this.edgeID = eID;
		this.wayID = wID;
	}//End constructor
	
	public OSMEdge(OSMNode source, OSMNode target) {
		/*
		DBManager db = new DBManager("GraphTesting");
		this.wayID = db.getWayIdByEdge(source.getNodeID(), target.getNodeID());
		db.close();
		this.label = Long.toString(wayID);
		*/
		this.sourceNode = source;
		this.targetNode = target;
	}//End constructor
	
	public OSMEdge(long wID, OSMNode source, OSMNode target) {
		this.wayID = wID;
		this.sourceNode = source;
		this.targetNode = target;
	}//End constructor
	
	public OSMEdge(long eID, long wID, OSMNode source, OSMNode target) {
		this.edgeID = eID;
		this.wayID = wID;
		this.sourceNode = source;
		this.targetNode = target;
	}//End constructor
	
	/*
	 * 		METHODS
	 */
	
	public long getEdgeID() {
		return this.edgeID;
	}//End getWayID()
	
	public void setEdgeID(long eID) {
		this.edgeID = eID;
	}//End setWayID()
	
	public long getWayID() {
		return this.wayID;
	}//End getWayID()
	
	public void setWayID(long wID) {
		this.wayID = wID;
	}//End setWayID()
	
	public OSMNode getSourceNode() {
		return this.sourceNode;
	}//End getSourceNode()
	
	public void setSourceNode(OSMNode source) {
		this.sourceNode = source;
	}//End setSourceNode()
	
	public OSMNode getTargetNode() {
		return this.targetNode;
	}//End getTargetNode()
	
	public void setTargetNode(OSMNode target) {
		this.targetNode = target;
	}//End setTargetNode()
	
    @Override
    public String toString()
    {
        return "Way: " +  this.wayID + ", Edge: " + this.sourceNode + "->" + this.targetNode;
    }//End toString()
}//End OSMEdge
