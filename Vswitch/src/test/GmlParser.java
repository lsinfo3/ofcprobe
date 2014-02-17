package test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import ofcprobe.vswitch.graphml.Edge;
import ofcprobe.vswitch.graphml.GraphmlParser;
import ofcprobe.vswitch.graphml.Node;
import ofcprobe.vswitch.main.config.Config;
import ofcprobe.vswitch.main.config.Topology;

import org.jdom2.*;
import org.jdom2.input.SAXBuilder;

public class GmlParser {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String line = "1:1==2:1";
		int index = line.indexOf("==");
		String leftSwitch = line.substring(0, index);
		String rightSwitch = line.substring(index+2, line.length());
		System.out.println(leftSwitch);
		Config config = new Config("config.ini", 3);
		Topology topology = new Topology(config);
		topology.loadTopoFromFile();
//		GraphmlParser graphmlparser = new GraphmlParser("/home/christian/Desktop/Praktikum/archive/Aarnet.graphml");
//		
//		graphmlparser.readNodes();
//	
//		graphmlparser.readEdges();
//		
//		graphmlparser.writeToTopologyFile();
		
		
//		Document doc;
//		try {
//			doc = new SAXBuilder().build( "/home/christian/Desktop/Praktikum/archive/Aarnet.graphml" );
//			Element graphml = doc.getRootElement();
//			Namespace ns = graphml.getNamespace();
//			List<Element> list = graphml.getChildren("key", ns);
//			System.out.println(list.size());
//			String latitude_key="";
//			String country_key="";
//			String id_key="";
//			String longitude_key="";
//			String label_key="";
//			String linklabel_key="";
//			for (Element e : list) {
//				String s =e.getAttributeValue("attr.name");
////				System.out.println(a.getValue());
//				switch (s) {
//				case "Latitude":
//					latitude_key=e.getAttributeValue("id");
//					System.out.println(latitude_key);
//					break;
//				case "Country":
//					country_key=e.getAttributeValue("id");
//					System.out.println(country_key);
//					break;
//				case "id":
//					if (e.getAttributeValue("for").equals("node")) {
//					id_key=e.getAttributeValue("id");
//					System.out.println(id_key);
//					}
//					break;
//				case "Longitude":
//					longitude_key=e.getAttributeValue("id");
//					System.out.println(longitude_key);
//					break;
//				case "label":
//					if (e.getAttributeValue("for").equals("node")) {
//					label_key=e.getAttributeValue("id");
//					System.out.println(label_key);
//					}
//					break;
//				case "LinkLabel":
//					linklabel_key=e.getAttributeValue("id");
//					System.out.println(linklabel_key);
//					break;
//				default:
//					break;
//				}
//				
//				
//			}
//			Element graph = graphml.getChild("graph",ns);
//			
////			System.out.println(list.get(37).getName());
////			System.out.println(graph==null);
//			
////			Element node = graph.getChild("node",ns);
//			List<Element> nodeelemlist = graph.getChildren("node", ns);
//			List<Node> nodelist= new ArrayList<Node>();
//			for (Element e : nodeelemlist) {
//				Element country_elem = findElementById(e.getChildren(), country_key);
//				String country = country_elem.getText();
//				System.out.println(country);
//				
//				
//				Element city_elem = findElementById(e.getChildren(), label_key);
//				String city = city_elem.getText();
//				System.out.println(city);
//				
//				Element latitude_elem = findElementById(e.getChildren(), latitude_key);
//				double latitude = Double.parseDouble(latitude_elem.getText());
//				System.out.println(latitude);
//				
//				Element longitude_elem = findElementById(e.getChildren(), longitude_key);
//				double longitude = Double.parseDouble(longitude_elem.getText());
//				System.out.println(longitude);
//				
//				Element id_elem = findElementById(e.getChildren(), id_key);
//				int id = Integer.parseInt(id_elem.getText());
//				id++;
//				System.out.println(id);
//				
//				Node node = new Node(city, id, country, longitude, latitude);
//				nodelist.add(node);
//			}
////			System.out.println(nodelist.size());
//			List<Element> edgeelemlist = graph.getChildren("edge", ns);
//			List<Edge> edgelist= new ArrayList<Edge>();
//			for (Element e : edgeelemlist) {
//				Attribute source_attr=e.getAttribute("source");
//				int source = Integer.parseInt(source_attr.getValue());
//				source++;
////				System.out.println(source);
//				
//				Attribute target_attr=e.getAttribute("target");
//				int target = Integer.parseInt(target_attr.getValue());
//				target++;
////				System.out.println(target);
//				
//				Element linkspeed_elem = e.getChildren().get(0);
//				Scanner scanner = new Scanner(linkspeed_elem.getText());
//		        while (!scanner.hasNextDouble() && scanner.hasNext())
//		        {
//		            scanner.next();
//		        }
//		        double linkspeed = 0.0;
//		        if (scanner.hasNextDouble()) {
//		        	
//		        	linkspeed = scanner.nextDouble();
//		        } 
//		        if (linkspeed_elem.getText().contains("Mbps")) {
//		        	linkspeed *= 1E06;
//		        }
//		        if (linkspeed_elem.getText().contains("Gbps")) {
//		        	linkspeed *= 1E09;
//		        }
//		        scanner.close();
////		        System.out.println(linkspeed);
//		        Node sourcenode = null;
//		        Node targetnode = null;
//		        for (Node n : nodelist) {
//		        	if (n.getId()==source) {
//		        		sourcenode = n;
//		        	}
//		        	if (n.getId()==target) {
//		        		targetnode = n;
//		        	}
//		        }
//		        
//				Edge edge = new Edge(sourcenode,targetnode,linkspeed);
//				edgelist.add(edge);
//			}
////			System.out.println(edgelist.size());
////			for (Edge edge : edgelist) {
////				System.out.println("source: " + edge.getSource().getName() + " " + edge.getSource().getId() + " target: " +
////						edge.getTarget().getName()+ " " + edge.getTarget().getId() + " linkspeed: " + edge.getLinkspeed());
////			}
//			int[] ports = new int[nodelist.size()];
//			for (int i=0; i<ports.length; i++) {
//				ports[i]=1;
//			}
//			
//			Writer fw = null;
//			
//			try
//			{
//				fw = new FileWriter( "topology.ini" );
//				fw.write( "###Topology");
//				fw.append( System.getProperty("line.separator") );
//				fw.write("#ofSwitch:Port==ofSwitch:Port" );
//				fw.append( System.getProperty("line.separator") ); 
//				for (Edge edge : edgelist) {
////					System.out.println(edge.getSource().getName()+ " " + edge.getSource().getId() + ":" + (ports[edge.getSource().getId()]++) + "==" + 
////							edge.getTarget().getName() + " " + edge.getTarget().getId() + ":" + (ports[edge.getTarget().getId()]++));
//					fw.write(edge.getSource().getId() + ":" + (ports[edge.getSource().getId()-1]++) + "==" + 
//							  edge.getTarget().getId() + ":" + (ports[edge.getTarget().getId()-1]++));
//					fw.append( System.getProperty("line.separator") ); 
//				}
//			}
//			catch ( IOException e ) {
//				System.err.println( "Exception during Creation of topology file" );
//			}
//			finally {
//				if ( fw != null )
//					try { fw.close(); } catch ( IOException e ) { e.printStackTrace(); }
//			}
//			
//			
//		} catch (JDOMException | IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}
	
	public static Element findElementById(List<Element> list, String key) {
		for (Element e : list) {
			if (e.getAttributeValue("key").equals(key)){
				return e;
			}
		}
		return null;
	}

}
