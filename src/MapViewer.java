import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;

import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleGraph;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;

public class MapViewer {
	
	JXMapViewer mapViewer = new JXMapViewer();
	Graph<OSMNode, OSMEdge> graph;
	ArrayList<GeoPosition>  geopoints= new ArrayList<>();
    Set<Waypoint> waypoints = new HashSet<Waypoint>();
    GraphTesting gt = new GraphTesting();
    
    ArrayList<OSMEdge> full = new ArrayList<>();
    
    boolean finished= false;
	
	public MapViewer(ArrayList<OSMEdge> edges) {
		displayViewer();
		tileSetUp();
		
		//Inital Edge
	    OSMNode source = edges.get(0).getSourceNode();
    	
    	addToPainter(source);
    	
    	addWayPoint(source);
		
		for (int i = 0; i < edges.size(); i++) {
			addToPainter(edges.get(i).getTargetNode());
		}//end for
		
		List<GeoPosition> track = geopoints;
		
		drawRoute(waypoints, track);
		    
	     mapViewer.setZoom(4);
	     mapViewer.setAddressLocation(geopoints.get(0));
	}//End Constructor
	
	//Adds a node to the relevant painters
	public void addToPainter(OSMNode node) {
		GeoPosition geoP = new GeoPosition(Double.parseDouble(node.getLat()), Double.parseDouble((node.getLon())));
		geopoints.add(geoP);
		 
		//waypoints.add(new DefaultWaypoint(geoP));
	}//End AddToPainter
	
	public void addWayPoint(OSMNode node) {
		GeoPosition geoP = new GeoPosition(Double.parseDouble(node.getLat()), Double.parseDouble((node.getLon())));
		waypoints.add(new DefaultWaypoint(geoP));
	}//End AddWayPoint
	
	public void displayViewer() {
	    // Display the viewer in a JFrame
	    JFrame frame = new JFrame("JXMapviewer2");
	    frame.getContentPane().add(mapViewer);
	    frame.setSize(800, 600);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setVisible(true);
	}//End displayViewer()
	
	public void tileSetUp() {
	    // Create a TileFactoryInfo for OpenStreetMap
	    TileFactoryInfo info = new OSMTileFactoryInfo();
	    DefaultTileFactory tileFactory = new DefaultTileFactory(info);
	    tileFactory.setThreadPoolSize(8);
	    mapViewer.setTileFactory(tileFactory);
	}//End tileSetUp()
	
	//Takes in a nodes and edges, creates painters and draws them on the map
	public void drawRoute(Set<Waypoint> waypoints, List<GeoPosition> track) {
		RoutePainter routePainter = new RoutePainter(track);
		
        WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<Waypoint>();
        waypointPainter.setWaypoints(waypoints);
        
        List<Painter<JXMapViewer>> painters = new ArrayList<Painter<JXMapViewer>>();
        painters.add(routePainter);
        painters.add(waypointPainter);

        CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(painters);
        mapViewer.setOverlayPainter(painter);
	}//End drawRoute

}//End MapViewer
