import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
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
   
   //Create a table in database connection
   public void createNodeTable() {
	   try {
		   Statement stmt = c.createStatement();
		   String sql = "CREATE TABLE NODELIST " +
		      "(ID SERIAL PRIMARY KEY NOT NULL," +
		      " nodeID BIGINT UNIQUE NOT NULL," +
		      " lon varchar(50) NOT NULL," +
		      " lat varchar(50) NOT NULL);";
		   System.out.println(sql);
		   stmt.executeUpdate(sql);
		   stmt.close();
		   
		   System.out.println("Table Created Successfully\n");
	   } catch ( Exception e ) {
		   System.err.println( e.getClass().getName()+": "+ e.getMessage() );
		   System.exit(0);
	   }//End try catch
   }//End createNodeTable
   
   //Create a table in database connection
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
   
   public void createEdgeTable() {
	   try {
		   Statement stmt = c.createStatement();
		   String sql = "CREATE TABLE EDGELIST " +
		      "(wayID BIGINT PRIMARY KEY NOT NULL," +
		      " sourceNode BIGINT NOT NULL," +
		      " targetNode BIGINT NOT NULL," +
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
   
   //Insert values into Table
   public void insert(String table, String[] values) {
	   try {
		   Statement stmt = c.createStatement();
		   String sql = "";
		   if (table == "nodelist") {
			   sql = "INSERT INTO NODELIST (ID, nodeId, lat, lon)" +
		   "VALUES(" + values[0] + ", '" + values[1] + "', " + values[2] + 
		   ", '" + values[3] + "');";
		   } else if (table == "waylist") {
			   sql = "INSERT INTO WAYLIST (ID, wayID, node)" +
					   "VALUES(" + values[0] + ", " + values[1] + ", " + values[2] + ");";
		   } else if (table == "edgelist") {
			   sql = "INSERT INTO EDGELIST (wayID, sourceNode, targetNode)" +
					   "VALUES(" + values[0] + ", " + values[1] + ", " + values[2] + ");";
			   System.out.println(sql);
		   }else {
			   System.out.println("Invalid table name");
			   System.exit(0);
		   }//End if else
		   stmt.executeUpdate(sql);
		   stmt.close();
		   c.commit();
		   
		   System.out.println("Insert Successful");
	   }catch ( Exception e ) {
		   System.err.println( e.getClass().getName()+": "+ e.getMessage() );
		   System.exit(0);
	   }//End try catch
   }//End insert
   
   public OSMNode getNodeById(int id) {
	   OSMNode node = new OSMNode(id);
	   try {
		  Statement stmt = c.createStatement();
		  ResultSet rs = stmt.executeQuery("SELECT nodeID, lat, lon FROM nodelist WHERE id = " + id + ";");
		  while(rs.next()) {
			  node.setNodeID(rs.getLong("nodeID"));
			  node.setLat(rs.getString("lat"));
			  node.setLon(rs.getString("lon"));
		  }//End while
		  rs.close();
		  stmt.close();
	   }catch ( Exception e ) {
		   System.err.println( e.getClass().getName()+": "+ e.getMessage() );
		   System.exit(0);
	   }//End try catch
	   return node;
   }//End getNodeByID
   
   public OSMNode getNodeByNodeId(long nodeID) {
	   OSMNode node = new OSMNode(nodeID);
	   try {
		  Statement stmt = c.createStatement();
		  ResultSet rs = stmt.executeQuery("SELECT nodeID, lat, lon FROM nodelist WHERE nodeID = " + nodeID + ";");
		  while(rs.next()) {
			  node.setLat(rs.getString("lat"));
			  node.setLon(rs.getString("lon"));
		  }//End while
		  rs.close();
		  stmt.close();
	   }catch ( Exception e ) {
		   System.err.println( e.getClass().getName()+": "+ e.getMessage() );
		   System.exit(0);
	   }//End try catch
	   return node;
   }//End getNodeByID
   
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
   }
   
   public ArrayList<OSMEdge> getEdges() {
	   ArrayList<OSMEdge> edges = new ArrayList<>();
	   
	   try {
		   Statement stmt = c.createStatement();
		   ResultSet rs = stmt.executeQuery("SELECT * FROM edgelist;");
		   while(rs.next()) {
			   OSMNode sourceNode = getNodeByNodeId(rs.getLong("sourceNode"));
			   OSMNode targetNode = getNodeByNodeId(rs.getLong("targetNode"));
			   OSMEdge edge = new OSMEdge(rs.getLong("wayID"), sourceNode, targetNode);
			   edges.add(edge);
		   }//End while
		   rs.close();
		   stmt.close();
	   }catch ( Exception e ) {
		   System.err.println( e.getClass().getName()+": "+ e.getMessage() );
		   System.exit(0);
	   }//End try catch
	   return edges;
   }
   
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
   }
   
   //Select rows from table
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
		    		 System.out.println(param + " = " + result);
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
   }
   
}//End DBManager