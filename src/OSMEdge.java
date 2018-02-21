import org.jgrapht.graph.DefaultEdge;

public class OSMEdge extends DefaultEdge{
	
	private static final long serialVersionUID = 1L;
	
	private String label;
	private long wayID;
	OSMNode sourceNode, targetNode;
	
	public OSMEdge() {
		super();
	}
	
	public OSMEdge(long wID) {
		this.wayID = wID;
		this.label = Long.toString(wID);
	}
	
	public OSMEdge(OSMNode source, OSMNode target) {
		/*
		DBManager db = new DBManager("GraphTesting");
		this.wayID = db.getWayIdByEdge(source.getNodeID(), target.getNodeID());
		db.close();
		this.label = Long.toString(wayID);
		*/
		this.sourceNode = source;
		this.targetNode = target;
	}
	
	public OSMEdge(long wID, OSMNode source, OSMNode target) {
		this.wayID = wID;
		this.label = Long.toString(wID);
		this.sourceNode = source;
		this.targetNode = target;
	}
	
	public long getWayID() {
		return this.wayID;
	}
	
	public void setWayID(long wID) {
		this.wayID = wID;
	}
	
	public OSMNode getSourceNode() {
		return this.sourceNode;
	}
	
	public void setSourceNode(OSMNode source) {
		this.sourceNode = source;
	}
	
	public OSMNode getTargetNode() {
		return this.targetNode;
	}
	
	public void setTargetNode(OSMNode target) {
		this.targetNode = target;
	}
	
    @Override
    public String toString()
    {
        return this.label;
    }
}
