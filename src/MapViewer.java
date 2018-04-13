import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.event.MouseInputListener;

import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleGraph;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;

/*
 * This class was used for visually testing the routing algorithms.
 * Its a simple Java swing that loads OSM map tiles
 */
public class MapViewer {
	
	JXMapViewer mapViewer = new JXMapViewer();
	ArrayList<GeoPosition>  geopoints= new ArrayList<>();
    Set<Waypoint> waypoints = new HashSet<Waypoint>();
    List<GeoPosition> track;
	
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
		
		track = geopoints;
		
		drawRoute(waypoints, track);
		    
	    mapViewer.setZoom(4);
	    mapViewer.setAddressLocation(geopoints.get(0));
	}//End Constructor
	
	public MapViewer(OSMNode source) {
		displayViewer();
		tileSetUp();
		
		addToPainter(source);
    	addWayPoint(source);
    	
    	track = geopoints;
    	
    	drawRoute(waypoints, track);
    	
    	mapViewer.setZoom(4);
	    mapViewer.setAddressLocation(geopoints.get(0));
	    
	}//End constructor
	
	public void updateMap(OSMEdge edge) {
		this.mapViewer.repaint();
		addToPainter(edge.getTargetNode());
		drawRoute(waypoints, track);
	}
	
	public void reset() {
		track.clear();;
	}
	
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
	    
        // Add interactions
        MouseInputListener mia = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mia);
        mapViewer.addMouseMotionListener(mia);
        mapViewer.addMouseListener(new CenterMapListener(mapViewer));
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));
        mapViewer.addKeyListener(new PanKeyListener(mapViewer));
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
