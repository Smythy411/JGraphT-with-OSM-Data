import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;

import org.jgrapht.Graph;
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
	
	public MapViewer(Graph g) {
		
		this.graph = g;
		
		displayViewer();
		tileSetUp();
	    
	    GeoPosition base = new GeoPosition(53.2795432, -6.3469185);
	    
	    ArrayList<GeoPosition>  geopoints= new ArrayList<>();
	    Set<Waypoint> waypoints = new HashSet<Waypoint>();
	    
	    Set<OSMNode> nodes = graph.vertexSet();
	    Set<OSMEdge> edges = graph.edgeSet();
	    Object[] temp = nodes.toArray();
	    OSMEdge[] e = edges.toArray(new OSMEdge[edges.size()]);
	    System.out.println(e[1].getSourceNode());
	    for (int i = 0; i < 5; i++) {
	    	OSMNode source = e[i].getSourceNode();
	    	OSMNode target = e[i].getTargetNode();
	    	
	    	geopoints.add(new GeoPosition(Double.parseDouble(source.getLat()), Double.parseDouble((source.getLon()))));
	    	waypoints.add(new DefaultWaypoint(geopoints.get(i)));
	    }
	    
	    List<GeoPosition> track = geopoints;
	    
	    drawRoute(waypoints, track);
	    
        mapViewer.setZoom(5);
        mapViewer.setAddressLocation(base);
	}
	
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
	
	public void drawRoute(Set<Waypoint> waypoints, List<GeoPosition> track) {
		RoutePainter routePainter = new RoutePainter(track);
		
        WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<Waypoint>();
        waypointPainter.setWaypoints(waypoints);
        
        List<Painter<JXMapViewer>> painters = new ArrayList<Painter<JXMapViewer>>();
        painters.add(routePainter);
        painters.add(waypointPainter);

        CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(painters);
        mapViewer.setOverlayPainter(painter);
	}

}
