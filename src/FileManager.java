/*FileManger handles all file parsing and interacts with a DBManager 
 * to insert data into database.
 * The .csv and .osm file were generated using the osmconvert command line tool.
 * .osm has an XML structure.
 */

import com.opencsv.CSVReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class FileManager {
    private String csvFile;
    private String osmFile;

    private DBManager db;
    private String next[] = {};
    private List<String[]> list = new ArrayList<>();

    public FileManager (String cFile, String oFile) {
        this.csvFile = cFile;
        this.osmFile = oFile;

        db = new DBManager("GraphTesting");
    }//End FileHandler constructor

    //Parses data from CSVFile (Nodes)
    public void openCSVFile() {
        try {
        	Reader r = Files.newBufferedReader(Paths.get(csvFile));
            CSVReader reader = new CSVReader(r, '\t');//Specify asset file name
            //in open();
            for (; ; ) {
                next = reader.readNext();
                if (next != null) {
                    list.add(next);
                } else {
                    break;
                }//end if else
            }//end for
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }//end try catch

        for (int i = 1; i < list.size(); i++) {
            System.out.println("Inserting node " + list.get(i)[0]);
            db.insert("nodelist", new String[] {Integer.toString(i), list.get(i)[0], list.get(i)[1], list.get(i)[2]});
        }//end for
    }//End openCSVFile

    //Parses data from .osm file (Ways and Edges)
    public void parseXml(){
    	try {
	        File inputFile = new File(osmFile);
	        SAXReader reader = new SAXReader();
	        Document document = reader.read( inputFile );
	        
	        Element root = document.getRootElement();
	        System.out.println("Root element :" + root.getName());
	        
	        List<Node> ways = document.selectNodes("/osm/way");
	        
	        parseWays(ways);
	        
    	} catch (DocumentException e) {
            e.printStackTrace();
        }//End try catch

    }//End parseXml()
    
    //Parsing Ways
    public void parseWays(List<Node> ways) {
        int j = 1;
        for (Node way : ways) {
        	System.out.println(way.getName() + ": " + way.valueOf("@id"));
        	if (way.hasContent()) {
        		List<Node> nodes = way.selectNodes("*");
        		j = parseNodes(j, way.valueOf("@id"), nodes);
        	}//End outer if
        }//End for
    }//End parseWays
    
    //Parsing nodes within ways for both WayList and EdgeList
    public int parseNodes(int j, String wayID, List<Node> nodes) {
    	ArrayList<String> nodeIDs =  new ArrayList<>();
    	String landuse = null, highway = null, name = null;
    	String sourceNode = " ";
        String targetNode = " ";
        boolean nodeFound = false;
    	for (int i = 0; i < nodes.size(); i++) {
    		Node currentNode = nodes.get(i);
    		if (currentNode.getName() == "nd") {
    			String nodeRef =  nodes.get(i).valueOf("@ref");
    			nodeIDs.add(nodeRef);
    			
    			if (db.selectNode("nodeID", nodeRef)) {
        			db.insert("waylist", new String[] {Integer.toString(j), wayID, nodeRef});
        			
        			if (nodeFound == false) {
        				sourceNode = nodeRef;
        				nodeFound = true;
        			} else {
        				targetNode = nodeRef;
        				db.insert("edgelist", new String[] {Integer.toString(j - 1),
        						wayID, sourceNode, targetNode
        						});
        				sourceNode = targetNode;
        			}//End if else
        			j++;
    			}//End inner inner if
    		} else if (currentNode.getName() == "tag") {
    			String tagRef = currentNode.valueOf("@k");
    			String value = currentNode.valueOf("@v");
    			System.out.println(tagRef + " : " + value);
    			if (tagRef.equals("landuse")) {
    				landuse = value;
    			}else if (tagRef.equals("highway")) {
    				highway = value;
    			} else if (tagRef.equals("name")) {
    				int indexOf = value.indexOf('\'');
    				if (indexOf != -1) {
    					System.out.println("REGEX");
    					value = value.replaceFirst("\'", "");
    				}
    				name = value;
    			}//End inner if else
    		}//End outer if else
    	}//End for
    	String nodeString = "{";
    	for (int i = 0; i < nodeIDs.size(); i++) {
    		if (i == nodeIDs.size() - 1) {
    			nodeString = nodeString + nodeIDs.get(i) + "}";
    		} else {
    			nodeString = nodeString + nodeIDs.get(i) + ", ";
    		}
    	}//End for
    	db.insert("ways", new String[] {wayID, nodeString, highway, name, landuse});
    	return j;
    }//End parseNodes

}//End FileHandler
