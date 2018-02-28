import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;

public class MapViewer {
	
	JXMapViewer mapViewer = new JXMapViewer();
	Set<OSMNode> set;
	
	public MapViewer(Set nodeSet) {
		
		this.set = nodeSet;
		
		displayViewer();
		tileSetUp();
	    
	    GeoPosition base = new GeoPosition(53.2795432, -6.3469185);
        mapViewer.setZoom(5);
        mapViewer.setAddressLocation(base);
        
        DefaultWaypoint wp = new DefaultWaypoint(base);
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

}
