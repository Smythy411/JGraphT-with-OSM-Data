/*DBManager handles all interactions with the postgres database
 */

import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.sql.ResultSet;

public class DBManager {
	private Connection c;
	private String dbName;
	
	public DBManager(String db) {
		this.dbName = db;
		this.c = connect(dbName);
	}//End Constructor
   
   //Connect to postgreSQL database
	public Connection connect(String dbName) {
		try {
			Class.forName("org.postgresql.Driver");
			c = DriverManager
					.getConnection("jdbc:postgresql://localhost:5432/" + dbName,
							"eoin", "pass");
			c.setAutoCommit(false);
			System.out.println("Opened database successfully\n");
	    } catch ( Exception e ) {
	    	System.err.println( e.getClass().getName()+": "+ e.getMessage() );
	    	System.exit(0);
	    }//End try catch
		return c;
   }//End connect
	
	/*
	 * 		CREATES
	 */
   
   //Create a table 'Nodelist' in database connection
   public void createNodeTable() {
	   try {
		   Statement stmt = c.createStatement();
		   String sql = "CREATE TABLE NODELIST " +
		      "(ID SERIAL PRIMARY KEY NOT NULL," +
		      " nodeID BIGINT UNIQUE NOT NULL," +
		      " lon DOUBLE PRECISION NOT NULL," +
		      " lat DOUBLE PRECISION NOT NULL);";
		   System.out.println(sql);
		   stmt.executeUpdate(sql);
		   stmt.close();
		   
		   System.out.println("Table Created Successfully\n");
	   } catch ( Exception e ) {
		   System.err.println( e.getClass().getName()+": "+ e.getMessage() );
		   System.exit(0);
	   }//End try catch
   }//End createNodeTable
   
   //Create a table Waylist in database connection
   public void createWayTable() {
	   try {
		   Statement stmt = c.createStatement();
		   String sql = "CREATE TABLE WAYLIST " +
		      "(ID SERIAL PRIMARY KEY NOT NULL," +
		      " wayID BIGINT NOT NULL," +
		      " node BIGINT NOT NULL," +
		      " foreign key (node) references NodeList(nodeID));";
		   System.out.println(sql);
		   stmt.executeUpdate(sql);
		   stmt.close();
		   
		   System.out.println("Table Created Successfully\n");
	   } catch ( Exception e ) {
		   System.err.println( e.getClass().getName()+": "+ e.getMessage() );
		   System.exit(0);
	   }//End try catch
   }//End createNodeTable
   
   public void createWaysTable() {
	   try {
		   Statement stmt = c.createStatement();
		   String sql = "CREATE TABLE WAYS " +
		      "(wayID BIGINT PRIMARY KEY NOT NULL," +
		      " nodes BIGINT[]," +
		      " highway varchar(50)," +
		      " name varchar(50), " +
		      " landuse varchar(50));";
		   System.out.println(sql);
		   stmt.executeUpdate(sql);
		   stmt.close();
		   
		   System.out.println("Table Created Successfully\n");
	   } catch ( Exception e ) {
		   System.err.println( e.getClass().getName()+": "+ e.getMessage() );
		   System.exit(0);
	   }//End try catch
   }
   
 //Create a table Edgelist in database connection
   public void createEdgeTable() {
	   try {
		   Statement stmt = c.createStatement();
		   String sql = "CREATE TABLE EDGELIST " +
		      "(edgeID BIGINT PRIMARY KEY NOT NULL," +
		      " wayID BIGINT NOT NULL, " +
		      " sourceNode BIGINT NOT NULL," +
		      " targetNode BIGINT NOT NULL," +
		      " foreign key (wayID) references WayList(wayId), " +
		      " foreign key (sourceNode) references NodeList(nodeID), " +
		      " foreign key (targetNode) references NodeList(nodeID));";
		   System.out.println(sql);
		   stmt.executeUpdate(sql);
		   stmt.close();
		   
		   System.out.println("Table Created Successfully\n");
	   } catch ( Exception e ) {
		   System.err.println( e.getClass().getName()+": "+ e.getMessage() );
		   System.exit(0);
	   }//End try catch
   }//End createNodeTable
   
   /*
    * 		INSERTS
    */
   
   //Insert values into Table
   public void insert(String table, String[] values) {
	   try {
		   Statement stmt = c.createStatement();
		   String sql = "";
		   
		   //Checks which table to insert into
		   if (table == "nodelist") {
			   sql = "INSERT INTO NODELIST (ID, nodeId, lat, lon)" +
		   "VALUES(" + values[0] + ", '" + values[1] + "', " + values[2] + 
		   ", '" + values[3] + "');";
		   } else if (table == "waylist") {
			   sql = "INSERT INTO WAYLIST (ID, wayID, node)" +
					   "VALUES(" + values[0] + ", " + values[1] + ", " + values[2] + ");";
		   } else if (table == "edgelist") {
			   sql = "INSERT INTO EDGELIST (edgeId, wayID, sourceNode, targetNode)" +
					   "VALUES(" + values[0] + ", " + values[1] + ", " + values[2] + ", " + values[3]  +");";
			   //System.out.println(sql);
		   } else if (table == "ways") {
			   sql = "INSERT INTO WAYS " +
					   "VALUES(" + values[0] + ", '" + values[1] + "', '" + values[2] + "', '" + values[3]  +"', '" + values[4] + "');";
			   //System.out.println(sql);
		   }else {
			   System.out.println("Invalid table name");
			   System.exit(0);
		   }//End if else
		   stmt.executeUpdate(sql);
		   stmt.close();
		   c.commit();
		   
		   //System.out.println("Insert Successful");
	   }catch ( Exception e ) {
		   System.err.println( e.getClass().getName()+": "+ e.getMessage() );
		   System.exit(0);
	   }//End try catch
   }//End insert
   
   /*
    * 		SELECTS
    */
   
   /*
    * 		NodeList
    */
   
   //Gets the relevant node by 'id'
   public OSMNode getNodeById(int id) {
	   OSMNode node = new OSMNode(id);
	   try {
		  Statement stmt = c.createStatement();
		  ResultSet rs = stmt.executeQuery("SELECT nodeID, lat, lon FROM nodelist WHERE id = " + id + ";");
		  while(rs.next()) {
			  node.setNodeID(rs.getLong("nodeID"));
			  node.setLat(String.valueOf(rs.getBigDecimal("lat")));
			  node.setLon(String.valueOf(rs.getBigDecimal("lon")));
		  }//End while
		  rs.close();
		  stmt.close();
	   }catch ( Exception e ) {
		   System.err.println( e.getClass().getName()+": "+ e.getMessage() );
		   System.exit(0);
	   }//End try catch
	   return node;
   }//End getNodeByID
   
 //Gets the relevant node by 'nodeID'
   public OSMNode getNodeByNodeId(long nodeID) {
	   OSMNode node = new OSMNode(nodeID);
	   try {
		  Statement stmt = c.createStatement();
		  ResultSet rs = stmt.executeQuery("SELECT nodeID, lat, lon FROM nodelist WHERE nodeID = " + nodeID + ";");
		  while(rs.next()) {
			  node.setLat(String.valueOf(rs.getBigDecimal("lat")));
			  node.setLon(String.valueOf(rs.getBigDecimal("lon")));
		  }//End while
		  rs.close();
		  stmt.close();
	   }catch ( Exception e ) {
		   System.err.println( e.getClass().getName()+": "+ e.getMessage() );
		   System.exit(0);
	   }//End try catch
	   return node;
   }//End getNodeByID
   
   public ArrayList<OSMNode> getNodes() {
	   ArrayList<OSMNode> nodes = new ArrayList<>();
	   
	   try {
		  Statement stmt = c.createStatement();
		  ResultSet rs = stmt.executeQuery("SELECT nodeID, lat, lon FROM nodelist;");
		  while(rs.next()) {
			  OSMNode node = new OSMNode(rs.getLong("nodeID"), String.valueOf(rs.getBigDecimal("lat")), String.valueOf(rs.getBigDecimal("lon")));
			  nodes.add(node);
		  }//End while
		   rs.close();
		   stmt.close();
	   }catch ( Exception e ) {
		   System.err.println( e.getClass().getName()+": "+ e.getMessage() );
		   System.exit(0);
	   }//End try catch
	   return nodes;
   }//End getNodes
   
   public ArrayList<OSMNode> getNodesByBoundary(String x1, String y1, String x2, String y2) {
	   ArrayList<OSMNode> nodes = new ArrayList<>();
	   
	   try {
		  Statement stmt = c.createStatement();
		  String query = "SELECT nodeID, lat, lon FROM nodelist WHERE lat BETWEEN " +
				  x1 + " AND " + x2 + " AND lon BETWEEN " + y1 + " AND " + y2 + ";";
		  System.out.println(query);
		  ResultSet rs = stmt.executeQuery(query);
		  while(rs.next()) {
			  OSMNode node = new OSMNode(rs.getLong("nodeID"), String.valueOf(rs.getBigDecimal("lat")), String.valueOf(rs.getBigDecimal("lon")));
			  nodes.add(node);
		  }//End while
		   rs.close();
		   stmt.close();
	   }catch ( Exception e ) {
		   System.err.println( e.getClass().getName()+": "+ e.getMessage() );
		   System.exit(0);
	   }//End try catch
	   return nodes;
   }//End getNodesByBoundary
   
   /*
    * 		WayList
    */
   
 //Gets the relevant nodes by 'wayID'
   public ArrayList<OSMNode> getNodesbyWayId(long wayID) {
	   ArrayList<OSMNode> nodes = new ArrayList<>();
	   try {
		   Statement stmt = c.createStatement();
		   ResultSet rs = stmt.executeQuery("SELECT node FROM waylist WHERE wayID = " + wayID + ";");
		   while(rs.next()) {
			   OSMNode node = getNodeByNodeId(rs.getLong("node"));
			   nodes.add(node);
		   }//End while
		   rs.close();
		   stmt.close();
	   }catch ( Exception e ) {
		   System.err.println( e.getClass().getName()+": "+ e.getMessage() );
		   System.exit(0);
	   }//End try catch
	   
	   return nodes;
   }//End getNodesByWayId
   
   /*
    * 		Ways
    */
   
   public ArrayList<OSMWay> getWays(ArrayList<OSMNode> passedNodes, ArrayList<OSMEdge> passedEdges) {
	   ArrayList<OSMWay> ways = new ArrayList<>();
	   try {
		   Statement stmt = c.createStatement();
		   ResultSet rs = stmt.executeQuery("SELECT * FROM ways;");
		   while(rs.next()) {
			   long wayID = rs.getLong("wayid");
			   ArrayList<OSMNode> tempNodes = new ArrayList<>();
			   ArrayList<OSMEdge> tempEdges = new ArrayList<>();
				for (int i = 0; i < passedEdges.size(); i++) {
					long tempWID = passedEdges.get(i).getWayID();	
					if (wayID == tempWID) {
						tempEdges.add(passedEdges.get(i));
						tempNodes.add(passedEdges.get(i).getTargetNode());
					}//end if
				}//end for
			   /*
			   Array z = rs.getArray("nodes");
			   String[] nds = (String[])z.getArray();
			   */
			   String name = rs.getString("name");
			   String landuse = rs.getString("landuse");
			   String highway = rs.getString("highway");
			   if (tempEdges.size() <= 0) {
				   OSMWay way = new OSMWay(wayID, name, landuse, highway);
				   ways.add(way);
			   } else {
				   OSMWay way = new OSMWay(wayID, tempNodes, tempEdges, name, landuse, highway);
				   ways.add(way);
			   }//End if else
		   }//End while
		   rs.close();
		   stmt.close();
	   }catch ( Exception e ) {
		   System.err.println( e.getClass().getName()+": "+ e.getMessage() );
		   System.exit(0);
	   }//End try catch
	   return ways;
   }//End getWays
   
   //SELECT COUNT(DISTINCT ways.wayid, highway, name, landuse) FROM ways INNER JOIN edgelist ON ways.wayid = edgelist.wayid INNER JOIN nodelist ON nodelist.nodeid = edgelist.sourcenode WHERE lat BETWEEN 53.2745 AND 53.2897 AND lon BETWEEN -6.3553 AND -6.3302;
   public ArrayList<OSMWay> getWaysByBoundary(ArrayList<OSMNode> passedNodes, ArrayList<OSMEdge> passedEdges, String x1, String y1, String x2, String y2) {
	   ArrayList<OSMWay> ways = new ArrayList<>();
	   
	   try {
		   Statement stmt = c.createStatement();
		   String query = "SELECT DISTINCT ways.wayid, nodes, highway, name, landuse FROM ways " +
		   	"INNER JOIN edgelist ON ways.wayid = edgelist.wayid " +
		   	"INNER JOIN nodelist ON nodelist.nodeid = edgelist.sourcenode " +
		   	"WHERE lat BETWEEN " + x1 + " AND " + x2 + " AND lon BETWEEN " + y1 + " AND " + y2 + ";";
		   System.out.println(query);
		   ResultSet rs = stmt.executeQuery(query);
		   while(rs.next()) {
			   long wayID = rs.getLong("wayid");
			   ArrayList<OSMNode> tempNodes = new ArrayList<>();
			   ArrayList<OSMEdge> tempEdges = new ArrayList<>();
				for (int i = 0; i < passedEdges.size(); i++) {
					long tempWID = passedEdges.get(i).getWayID();	
					if (wayID == tempWID) {
						tempEdges.add(passedEdges.get(i));
						tempNodes.add(passedEdges.get(i).getTargetNode());
					}//end if
				}//end for
			   /*
			   Array z = rs.getArray("nodes");
			   String[] nds = (String[])z.getArray();
			   */
			   String name = rs.getString("name");
			   String landuse = rs.getString("landuse");
			   String highway = rs.getString("highway");
			   if (tempEdges.size() <= 0) {
				   OSMWay way = new OSMWay(wayID, name, landuse, highway);
				   ways.add(way);
			   } else {
				   OSMWay way = new OSMWay(wayID, tempNodes, tempEdges, name, landuse, highway);
				   ways.add(way);
			   }//End if else
		   }//End while
		   
		   rs.close();
		   stmt.close();
	   }catch ( Exception e ) {
		   System.err.println( e.getClass().getName()+": "+ e.getMessage() );
		   System.exit(0);
	   }//End try catch
	   return ways;
   }//End getWays
   
   /*
    * 		EdgeList
    */
   
   //Gets all edges from database
   public ArrayList<OSMEdge> getEdges(ArrayList<OSMNode> nodes) {
	   ArrayList<OSMEdge> edges = new ArrayList<>();
	   
	   try {
		   Statement stmt = c.createStatement();
		   ResultSet rs = stmt.executeQuery("SELECT * FROM edgelist;");
		   while(rs.next()) {

			   OSMNode sourceNode = null, targetNode = null;
			   for (int i = 0; i < nodes.size(); i++) {
				   OSMNode node = nodes.get(i);
				   if (node.getNodeID() == rs.getLong("targetNode")) {
					   targetNode = node;
				   }
				   if (node.getNodeID() == rs.getLong("sourceNode") ) {
					   sourceNode = node; 
				   }
			   }
			   
			   if (sourceNode == targetNode) {
				  // System.out.println("Loops not allowed");
			   } else {			   
				   OSMEdge edge = new OSMEdge(rs.getLong("edgeID"), rs.getLong("wayID"), sourceNode, targetNode);
				   sourceNode.addEdge(edge);
				   targetNode.addEdge(edge);
				   edges.add(edge);
			   }
		   }//End while
		   rs.close();
		   stmt.close();
	   }catch ( Exception e ) {
		   System.err.println( e.getClass().getName()+": "+ e.getMessage() );
		   System.exit(0);
	   }//End try catch
	   return edges;
   }//End getEdges
   
   public ArrayList<OSMEdge> getEdgesByBoundary(ArrayList<OSMNode> nodes, String x1, String y1, String x2, String y2) {
	   ArrayList<OSMEdge> edges = new ArrayList<>();
	   
	   try {
		   Statement stmt = c.createStatement();
		   String query = "SELECT * FROM nodelist INNER JOIN " +
				   	"edgelist ON nodelist.nodeid = edgelist.sourcenode WHERE lat BETWEEN " +
					  x1 + " AND " + x2 + " AND lon BETWEEN " + y1 + " AND " + y2 + ";";
		   System.out.println(query);
		   ResultSet rs = stmt.executeQuery(query);
		   while (rs.next()) {
			   OSMNode sourceNode = null, targetNode = null;
			   for (int i = 0; i < nodes.size(); i++) {
				   OSMNode node = nodes.get(i);
				   if (node.getNodeID() == rs.getLong("targetNode")) {
					   targetNode = node;
				   }
				   if (node.getNodeID() == rs.getLong("sourceNode") ) {
					   sourceNode = node; 
				   }
			   }//End for
			   if (sourceNode == targetNode || targetNode == null) {
				  // System.out.println("Loops not allowed and/or node may not be within boundary");
			   } else {
				   OSMEdge edge = new OSMEdge(rs.getLong("edgeID"), rs.getLong("wayID"), sourceNode, targetNode);
				   sourceNode.addEdge(edge);
				   targetNode.addEdge(edge);
				   edges.add(edge);
			   }
		   }
		   rs.close();
		   stmt.close();
	   }catch ( Exception e ) {
		   System.err.println( e.getClass().getName()+": "+ e.getMessage() );
		   System.exit(0);
	   }//End try catch
	   return edges;
   }//End getEdgesByBoundary
   
   //Gets the wayID for a relevant Edge.
   public long getWayIdByEdge(long source, long target) {
	   long wayID = 0;
	   try {
		   Statement stmt = c.createStatement();
		   ResultSet rs = stmt.executeQuery("SELECT wayid FROM edgelist WHERE sourceNode = " + 
		   source + "AND tagetNode = " + target + ";");
		   while(rs.next()) {
			   wayID = rs.getLong("wayid");
		   }//End while
		   rs.close();
		   stmt.close();
	   }catch ( Exception e ) {
		   System.err.println( e.getClass().getName()+": "+ e.getMessage() );
		   System.exit(0);
	   }//End try catch
	   return wayID;
   }//End getWayIdByEdge
   
   /*
    * 		Testing
    */
   
   //Select * rows from table nodelist (for testing)
   public boolean selectNode(String param, String value) {
	   boolean found = false;
	   try {
		   Statement stmt = c.createStatement();
		   
		   if (param == "*") {
			   ResultSet rs = stmt.executeQuery("SELECT " + param + " FROM nodelist;");
			   while ( rs.next() ) {
				   int id = rs.getInt("id");
				   long nodeID = rs.getLong("nodeID");
				   String lat  = rs.getString("lat");
				   String  lon = rs.getString("lon");
				   System.out.println( "ID = " + id );
				   System.out.println( "nodeID = " + nodeID);
				   System.out.println( "lat = " + lat);
				   System.out.println( "lon = " + lon);System.out.println();
				   found = true;
	             }//End while
			   rs.close();
	         } else if (param == "id" || param == "nodeID"){
	        	 ResultSet rs = stmt.executeQuery("SELECT " + param + " FROM nodelist WHERE " + 
	        			 param + " = " + value + ";");
	        	 while (rs.next()) {
		    		 long result = rs.getLong(param);
		    		 //System.out.println(param + " = " + result);
		    		 found = true;
	        	 }//End while
	    		 rs.close();
    		 } else if (param == "name" || param == "address") {
	        	 ResultSet rs = stmt.executeQuery("SELECT " + param + " FROM nodelist WHERE " +
	        			 param + " = " + value + ";");
		        	 while (rs.next()) {
		    		 String result = rs.getString(param);
		    		 System.out.println(param + " = " + result);
		    		 found = true;
	        	 }//End while
	    		 rs.close();
    		 } //End if else
	         stmt.close();
	   }catch ( Exception e ) {
		   System.err.println( e.getClass().getName()+": "+ e.getMessage() );
		   System.exit(0);
	   }//End try catch
	   return found;
   }//End select
   
   /*
    * 		UPDATES
    */
   
   //Update row in table
   public void update(String table, String col, String value, String indCol, String index) {
	   try {
		   Statement stmt = c.createStatement();
		   String sql = "UPDATE " + table + " SET " + col + " = " + value +
				   " WHERE " + indCol + " = " + index + ";";
		   stmt.executeUpdate(sql);
		   c.commit();
		   stmt.close();
		   
		   System.out.println("Update Successful");
	   }catch ( Exception e ) {
		   System.err.println( e.getClass().getName()+": "+ e.getMessage() );
		   System.exit(0);
	   }//End try catch
   }//End update
   
   /*
    * 		DELETES
    */
   
   //Delete row from table
   public void delete(String table, String col, String value) {
	   try {
		   Statement stmt = c.createStatement();
		   String sql = "DELETE FROM " + table + " WHERE " + col + " = " + value + ";";
		   stmt.executeUpdate(sql);
		   c.commit();
		   stmt.close();
		   
		   System.out.println("Delete Successful");
	   }catch ( Exception e ) {
		   System.err.println( e.getClass().getName()+": "+ e.getMessage() );
		   System.exit(0);
	   }//End try catch
   }//End delete
   
   //Close connection to database
   public void close() {
	   try {
		   c.close();
		   System.out.println("Database connection closed");
	   }catch ( Exception e ) {
		   System.err.println( e.getClass().getName()+": "+ e.getMessage() );
		   System.exit(0);
	   }//End try catch
   }//End close()
   
}//End DBManager