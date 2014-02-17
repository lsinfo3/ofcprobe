package ofcprobe.vswitch.graphml;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphmlParser {
	
	private static final Logger logger = LoggerFactory.getLogger(GraphmlParser.class);
	
	private List<Node> nodelist= new ArrayList<Node>();
	
	private List<Edge> edgelist= new ArrayList<Edge>();
	
//	private String graphml_filename;
		
	private Namespace ns;
	
	private Element graph;
	
	private String latitude_key="";
	private String country_key="";
	private String id_key="";
	private String longitude_key="";
	private String label_key="";
	private String linklabel_key="";

	public GraphmlParser(String graphml_filename) {
//		this.graphml_filename = graphml_filename;
		try {
			Document doc = new SAXBuilder().build(graphml_filename);
			Element graphml = doc.getRootElement();
			this.ns = graphml.getNamespace();
			this.graph = graphml.getChild("graph",this.ns);
			List<Element> list = graphml.getChildren("key", ns);
			for (Element e : list) {
				String s =e.getAttributeValue("attr.name");
//				System.out.println(a.getValue());
				switch (s) {
				case "Latitude":
					latitude_key=e.getAttributeValue("id");
					
					break;
				case "Country":
					country_key=e.getAttributeValue("id");
					
					break;
				case "id":
					if (e.getAttributeValue("for").equals("node")) {
					id_key=e.getAttributeValue("id");
					
					}
					break;
				case "Longitude":
					longitude_key=e.getAttributeValue("id");
					
					break;
				case "label":
					if (e.getAttributeValue("for").equals("node")) {
					label_key=e.getAttributeValue("id");
					
					}
					break;
				case "LinkLabel":
					linklabel_key=e.getAttributeValue("id");
//					System.out.println(linklabel_key);
					break;
				default:
					break;
				}
			}
		} catch (JDOMException | IOException e) {
			logger.error("Error in GraphmlParser");
			System.exit(-1);
		}
	}
	
	private Element findElementById(List<Element> list, String key) {
		for (Element e : list) {
			if (e.getAttributeValue("key").equals(key)){
				return e;
			}
		}
		return null;
	}
	
	public void readNodes() {
		
		//			Element node = graph.getChild("node",ns);
					List<Element> nodeelemlist = this.graph.getChildren("node", this.ns);
					for (Element e : nodeelemlist) {
						Element country_elem = findElementById(e.getChildren(), country_key);
						String country = country_elem.getText();
//						System.out.println(country);
						
						
						Element city_elem = findElementById(e.getChildren(), label_key);
						String city = city_elem.getText();
//						System.out.println(city);
						
						Element latitude_elem = findElementById(e.getChildren(), latitude_key);
						double latitude = Double.parseDouble(latitude_elem.getText());
//						System.out.println(latitude);
						
						Element longitude_elem = findElementById(e.getChildren(), longitude_key);
						double longitude = Double.parseDouble(longitude_elem.getText());
//						System.out.println(longitude);
						
						Element id_elem = findElementById(e.getChildren(), id_key);
						int id = Integer.parseInt(id_elem.getText());
						id++;
//						System.out.println(id);
						
						Node node = new Node(city, id, country, longitude, latitude);
						nodelist.add(node);
					}
	}
	
	public void readEdges() {
		if (nodelist.size() == 0){
			logger.error("Read in Nodes first!");
			System.exit(-1);
		}
		List<Element> edgeelemlist = this.graph.getChildren("edge", this.ns);
		for (Element e : edgeelemlist) {
			Attribute source_attr=e.getAttribute("source");
			int source = Integer.parseInt(source_attr.getValue());
			source++;
//			System.out.println(source);
			
			Attribute target_attr=e.getAttribute("target");
			int target = Integer.parseInt(target_attr.getValue());
			target++;
//			System.out.println(target);
			
			Element linkspeed_elem = findElementById(e.getChildren(), linklabel_key);
			double linkspeed = 0.0;
			if (linkspeed_elem != null) {
				Scanner scanner = new Scanner(linkspeed_elem.getText());
				while (!scanner.hasNextDouble() && scanner.hasNext())
				{
					scanner.next();
				}
				if (scanner.hasNextDouble()) {

					linkspeed = scanner.nextDouble();
				} 
				if (linkspeed_elem.getText().contains("Mbps")) {
					linkspeed *= 1E06;
				}
				if (linkspeed_elem.getText().contains("Gbps")) {
					linkspeed *= 1E09;
				}
				scanner.close();
			}
//	        System.out.println(linkspeed);
	        Node sourcenode = null;
	        Node targetnode = null;
	        for (Node n : this.nodelist) {
	        	if (n.getId()==source) {
	        		sourcenode = n;
	        	}
	        	if (n.getId()==target) {
	        		targetnode = n;
	        	}
	        }
	        
			Edge edge = new Edge(sourcenode,targetnode,linkspeed);
			this.edgelist.add(edge);
		}
	}
	
	public void writeToTopologyFile() {
		if (nodelist.size() == 0 || edgelist.size() == 0){
			logger.error("No nodes or edges provided.");
			System.exit(-1);
		}
		int[] ports = new int[nodelist.size()];
		for (int i=0; i<ports.length; i++) {
			ports[i]=1;
		}
		
		Writer fw = null;
		
		try
		{
			fw = new FileWriter( "topology.ini" );
			fw.write( "###Topology");
			fw.append( System.getProperty("line.separator") );
			fw.write("#ofSwitch:Port==ofSwitch:Port" );
			fw.append( System.getProperty("line.separator") ); 
			for (Edge edge : edgelist) {
//				System.out.println(edge.getSource().getName()+ " " + edge.getSource().getId() + ":" + (ports[edge.getSource().getId()]++) + "==" + 
//						edge.getTarget().getName() + " " + edge.getTarget().getId() + ":" + (ports[edge.getTarget().getId()]++));
				fw.write(edge.getSource().getId() + ":" + (ports[edge.getSource().getId()-1]++) + "==" + 
						  edge.getTarget().getId() + ":" + (ports[edge.getTarget().getId()-1]++));
				fw.append( System.getProperty("line.separator") ); 
			}
		}
		catch ( IOException e ) {
			logger.error( "Exception during Creation of topology file" );
		}
		finally {
			if ( fw != null )
				try { 
					fw.close();
					logger.info("Writing Topology-File successful!");
				} 
			catch ( IOException e ) { e.printStackTrace(); }
		}
	}
}
