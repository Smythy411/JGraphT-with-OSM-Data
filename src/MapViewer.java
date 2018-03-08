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
    
    boolean finished= false;
	
	public MapViewer(Graph<OSMNode, OSMEdge> g) {
		
		this.graph = g;
		
		displayViewer();
		tileSetUp();
	    
	    Set<OSMNode> nodes = graph.vertexSet();
	    Set<OSMEdge> edges = graph.edgeSet();
	    Object[] temp = nodes.toArray();
	    OSMEdge[] e = edges.toArray(new OSMEdge[edges.size()]);
	    
	    Graph<OSMNode, OSMEdge> subGraph = new SimpleGraph<OSMNode, OSMEdge>(OSMEdge.class);

	    //Inital Edge
	    OSMNode source = e[0].getSourceNode();
    	OSMNode target = e[0].getTargetNode();
    	
    	subGraph.addVertex(source);
    	subGraph.addVertex(target);
    	subGraph.addEdge(source, target);
    	
    	addToPainter(source);
    	addToPainter(target);
    	

    	
    	int size = 40;
    	constructG(size, source, target);
    	
	    List<GeoPosition> track = geopoints;
	    
	    drawRoute(waypoints, track);
	    
        mapViewer.setZoom(5);
        mapViewer.setAddressLocation(geopoints.get(0));
	}
	
	public void constructG(int size, OSMNode source, OSMNode target) {
		System.out.println(size);
		
		ArrayList<OSMEdge> edges = target.getEdges();
		
		if (size == 0) {
			finished = true;
		}
		
		if (edges.size() == 1) {
			//System.out.println("Dead End");
			target = edges.get(0).getTargetNode();
			addToPainter(target);
			
			return;
		} else {
			for (int i = 0; i < edges.size(); i++) {
				if (finished) {
					return;
				}
				
				target = edges.get(i).getTargetNode();
				
				addToPainter(target);
				
				if (edges.equals(target.getEdges())) {
					//System.out.println("Edge Already visited");
				} else {
					ArrayList<OSMEdge> subEdges = target.getEdges();
					//System.out.println(subEdges);
					for (int j = 0; j < subEdges.size(); j++) {
						constructG(size--, target, subEdges.get(j).getTargetNode());
					}
				}
			}
		}
		
	}
	
	public void addToPainter(OSMNode node) {
		GeoPosition geoP = new GeoPosition(Double.parseDouble(node.getLat()), Double.parseDouble((node.getLon())));
		geopoints.add(geoP);
		 
		waypoints.add(new DefaultWaypoint(geoP));
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
