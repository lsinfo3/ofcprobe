/* 
 * Copyright (C) 2014 Christopher Metter
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.uniwuerzburg.info3.ofcprobe.vswitch.graphml;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
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
	
	private ArrayList<Node> nodelist= new ArrayList<Node>();
	
	private ArrayList<Edge> edgelist= new ArrayList<Edge>();
	
	private int[] edgesST;
	
//	private String graphml_filename;
		
	private Namespace ns;
	
	private Element graph;
	
	private String latitude_key="";
	private String country_key="";
	private String id_key="";
	private String longitude_key="";
	private String label_key="";
	private String linklabel_key="";
	private String internal_key="";

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
				case "Internal":
					internal_key=e.getAttributeValue("id");
					
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
	
	private Node findNodeByCoords(ArrayList<Node> list, double longitude, double latitude) {
		for (Node node : list) {
			if (Math.abs(node.getLongitude()-longitude)<0.1 && Math.abs(node.getLatitude()-latitude)<0.1)
				return node;
		}
		return null;
	}
	
	private Edge findEdge(ArrayList<Edge> list, Node sourcenode, Node targetnode) {
		for (Edge edge: list) {
			if ((edge.getSource().equals(sourcenode) && edge.getTarget().equals(targetnode))
					|| (edge.getSource().equals(targetnode) && edge.getTarget().equals(sourcenode)))
				return edge;
		}
		return null;
	}
	
	private Edge findEdge(ArrayList<Edge> list, int source, int  target) {
		for (Edge edge: list) {
			if ((edge.getSource().getNumber() == source && edge.getTarget().getNumber() == target)
					|| (edge.getSource().getNumber() == target && edge.getTarget().getNumber() == source))
				return edge;
		}
		return null;
	}
	
	public void readNodes() {
		
		//			Element node = graph.getChild("node",ns);
					List<Element> nodeelemlist = this.graph.getChildren("node", this.ns);
					int number = 1;
					for (Element e : nodeelemlist) {
						
//						Element internel_elem = findElementById(e.getChildren(), internal_key);
//						System.out.println(internel_elem.getText());
//						if (internel_elem != null && internel_elem.getText().equals("0")) {
//							internal_nodes++;
//							continue;
//						}
						Element latitude_elem = findElementById(e.getChildren(), latitude_key);
						double latitude = (latitude_elem == null) ? 0 : Double.parseDouble(latitude_elem.getText());
//						System.out.println(latitude);
						
						Element longitude_elem = findElementById(e.getChildren(), longitude_key);
						double longitude = (longitude_elem == null) ? 0 : Double.parseDouble(longitude_elem.getText());
//						System.out.println(longitude);
						
						if(latitude == 0 || longitude == 0)
							continue;
						
						Element country_elem = findElementById(e.getChildren(), country_key);						
						String country = (country_elem == null) ? "" : country_elem.getText();						
//						System.out.println(country);
						
						
						Element city_elem = findElementById(e.getChildren(), label_key);
						String city = (city_elem == null) ? "" : city_elem.getText();
//						System.out.println(city);
						
						
						Element id_elem = findElementById(e.getChildren(), id_key);
						int id = Integer.parseInt(id_elem.getText());
//						System.out.println(id);
						
						Node n = findNodeByCoords(nodelist, longitude, latitude);
						if (n != null) {
							n.addId(id);
//							for (int i : n.getIds()) {
//								System.out.println(i);
//							}
						} else {
							Node node = new Node(number, city, id, country, longitude, latitude);
							nodelist.add(node);
							number++;							
						}
							
						
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
//			System.out.println(source);
			
			Attribute target_attr=e.getAttribute("target");
			int target = Integer.parseInt(target_attr.getValue());
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
	        	if (n.getIds().contains(source)) {
	        		sourcenode = n;
	        	}
	        	if (n.getIds().contains(target)) {
	        		targetnode = n;
	        	}
	        }
	        if (sourcenode == null || targetnode == null || sourcenode == targetnode 
	        		|| findEdge(edgelist, sourcenode, targetnode) != null) {
//	        	System.out.println("node == null " + source + " " + target);
	        	continue;
	        }
			Edge edge = new Edge(sourcenode,targetnode,linkspeed);
			this.edgelist.add(edge);
		}
	}
	
	public int Dijkstra(Node startnode) {
		int[] distance = new int[nodelist.size()];
		int[] predecessor = new int[nodelist.size()];
		ArrayList<Node> q = new ArrayList<Node>();
		initializeDijkstra(startnode, distance, predecessor, q);
//		System.out.println(q.size());
		while(!q.isEmpty()) {
			Node u = q.get(0);
			for (Node node : q) {
				if (distance[node.getNumber()-1]<distance[u.getNumber()-1])
					u=node;
			}
			q.remove(u);
			for(Edge e : edgelist) {
				
				if(e.getSource().getNumber()==u.getNumber() ) {
					Node v=e.getTarget();
					if(q.contains(v))
						 distance_update(u,v,distance, predecessor);
				} else if (e.getTarget().getNumber()==u.getNumber()) {
					Node v=e.getSource();
					if(q.contains(v))
						 distance_update(u,v,distance, predecessor);;
				}
					
			}
		}
		int maxD = distance[0];
		for (int i = 0; i < distance.length; i++) {
//			System.out.print((i+1) +":"+ distance[i] +",");
			if (distance[i] > maxD) {
				maxD = distance[i];
			}
		}
//		System.out.println();
//		for (int i = 0; i < predecessor.length; i++) {
//			System.out.print((i+1) +":"+ predecessor[i] +",");
//		}
//		System.out.println();
		edgesST = Arrays.copyOf(predecessor,predecessor.length);
		return maxD;
	}
	
	private void distance_update(Node u, Node v, int[] distance,
			int[] predecessor) {
		int alt = distance[u.getNumber()-1] + 1;
		if (alt < distance[v.getNumber()-1]) {
			distance[v.getNumber()-1] = alt;
			predecessor[v.getNumber()-1] = u.getNumber();
		}
		
	}

	private void initializeDijkstra(Node startnode, int[] distance, int[] predecessor, ArrayList<Node> q) {
		for (Node node : nodelist) {
			distance[node.getNumber()-1]=Integer.MAX_VALUE;
			predecessor[node.getNumber()-1]=-1;
			q.add(node);
		}
		distance[startnode.getNumber()-1]=0;
	}

	public void writeToTopologyFile() {
		if (nodelist.size() == 0 || edgelist.size() == 0){
			logger.error("No nodes or edges provided.");
			System.exit(-1);
		}
		int distance = Integer.MAX_VALUE;
		Node startnode = null;
		for (Node node : nodelist) {
//			System.out.println("Node: " + node.getNumber() + " maxD: " + Dijkstra(node));
			if (Dijkstra(node)<distance) {
				distance = Dijkstra(node);
				startnode = node;
			}
		}
		Dijkstra(startnode);
//		System.out.println("Startnode: " + startnode.getName());
		Iterator<Edge> iter = edgelist.iterator();
		
		while (iter.hasNext()) {			
			Edge edge = iter.next();
//			System.out.println("Edge: " + edge.getSource().getName() + " - " + edge.getTarget().getName());
			if ((edge.getTarget().getNumber()==edgesST[edge.getSource().getNumber()-1])
					|| (edge.getSource().getNumber()==edgesST[edge.getTarget().getNumber()-1]))
					continue;
//			System.out.println("Edge removed");
			iter.remove();
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
				fw.write(edge.getSource().getNumber() + ":" + (ports[edge.getSource().getNumber()-1]++) + "==" + 
						  edge.getTarget().getNumber() + ":" + (ports[edge.getTarget().getNumber()-1]++));
				fw.append( System.getProperty("line.separator") ); 
			}
			for (int i=0; i<nodelist.size(); i++) {
				logger.info("Switch: " + nodelist.get(i).getName()+" "+ nodelist.get(i).getIds() + " " + nodelist.get(i).getNumber() + " Conntections: " + (ports[i]-1));
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
//					System.exit(0);
				} 
			catch ( IOException e ) { e.printStackTrace(); }
		}
	}
	
	public int getNodeCount() {
		return nodelist.size();
	}
}
