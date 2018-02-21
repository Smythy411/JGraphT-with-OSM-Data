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
	        
	        int j = 1;
	        String sourceNode = " ";
	        String targetNode = " ";
	        for (Node way : ways) {
	        	System.out.println(way.getName() + ": " + way.valueOf("@id"));
	        	if (way.hasContent()) {
	        		List<Node> nodes = way.selectNodes("*");
	        		int n = 0;
		        	for (int i = 0; i < nodes.size(); i++) {
		        		if (nodes.get(i).getName() == "nd") {
		        			String nodeRef =  nodes.get(i).valueOf("@ref");
		        			if (n == 0) {
		        				sourceNode = nodeRef;	
		        			}//End if
		        			//System.out.println(nodes.get(i).getName() + ": " + nodeRef);
		        			if (db.selectNode("nodeID", nodeRef)) {
			        			db.insert("waylist", new String[] {Integer.toString(j), way.valueOf("@id"), nodeRef});
			        			j++;
			        			n++;
			        			targetNode = nodeRef;
		        			}//End inner inner if
		        		}//End inner if
		        	}//End for
	        		db.insert("edgelist", new String[] {
	        				way.valueOf("@id"), sourceNode, targetNode
	        				});
	        	}//End outer if
	        }//End for
	        
    	} catch (DocumentException e) {
            e.printStackTrace();
        }//End try catch

    }//End parseXml()

}//End FileHandler
