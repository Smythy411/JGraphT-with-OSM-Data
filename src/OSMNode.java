
public class OSMNode {
	
	private int id;
	private long nodeID;
	private String lat;
	private String lon;
	
	public OSMNode() {
		
	}//End OSMNode
	
	public OSMNode(int passedID) {
		this.id = passedID;
	}//End passedOSMNode
	
	public OSMNode(long passedNodeID) {
		this.nodeID = passedNodeID;
	}//End passedOSMNode
	
	public OSMNode(int passedId, long passedNodeID, String passedLat, String passedLon) {
		this.id = passedId;
		this.nodeID = passedNodeID;
		this.lat = passedLat;
		this.lon = passedLon;
	}//End OSMNode constructor
	
	public OSMNode(long passedNodeID, String passedLat, String passedLon) {
		this.nodeID = passedNodeID;
		this.lat = passedLat;
		this.lon = passedLon;
	}//End OSMNode Constructor
	
	public void setNodeID(long passedNodeID) {
		this.nodeID = passedNodeID;
	}//End setNodeID
	
	public long getNodeID() {
		return this.nodeID;
	}//End getNodeID
	
	public void setLat(String passedLat) {
		this.lat = passedLat;
	}//End setLat
	
	public String getLat() {
		return this.lat;
	}//End getNodeID
	
	public void setLon(String passedLon) {
		this.lon = passedLon;
	}//End setLon
	
	public String getLon() {
		return this.lon;
	}//End getNodeID
	
    @Override
    public String toString()
    {
        return "(" + this.nodeID + ")";
    }
}//End OSMNode
