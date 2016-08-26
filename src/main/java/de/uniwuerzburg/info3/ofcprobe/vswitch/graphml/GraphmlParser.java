/* 
 * Copyright 2016 christopher.metter.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.uniwuerzburg.info3.ofcprobe.vswitch.graphml;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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

/**
 * GraphMLparser
 *
 * @author christian rachor
 */
public class GraphmlParser {

    private static final Logger logger = LoggerFactory.getLogger(GraphmlParser.class);

    private ArrayList<Node> nodelist = new ArrayList<>();

    private ArrayList<Edge> edgelist = new ArrayList<>();

    private int[] edgesST;

    private Namespace ns;

    private Element graph;

    private String latitude_key = "";
    private String country_key = "";
    private String id_key = "";
    private String longitude_key = "";
    private String label_key = "";
    private String linklabel_key = "";
    private String internal_key = "";

    /**
     * Constructor
     *
     * @param graphml_filename the GraphML Filenam
     */
    public GraphmlParser(String graphml_filename) {
        try {
            Document doc = new SAXBuilder().build(graphml_filename);
            Element graphml = doc.getRootElement();
            this.ns = graphml.getNamespace();
            this.graph = graphml.getChild("graph", this.ns);
            List<Element> list = graphml.getChildren("key", ns);
            for (Element e : list) {
                String s = e.getAttributeValue("attr.name");
                switch (s) {
                    case "Latitude":
                        latitude_key = e.getAttributeValue("id");

                        break;
                    case "Country":
                        country_key = e.getAttributeValue("id");

                        break;
                    case "Internal":
                        internal_key = e.getAttributeValue("id");

                        break;
                    case "id":
                        if (e.getAttributeValue("for").equals("node")) {
                            id_key = e.getAttributeValue("id");

                        }
                        break;
                    case "Longitude":
                        longitude_key = e.getAttributeValue("id");

                        break;
                    case "label":
                        if (e.getAttributeValue("for").equals("node")) {
                            label_key = e.getAttributeValue("id");

                        }
                        break;
                    case "LinkLabel":
                        linklabel_key = e.getAttributeValue("id");
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

    /**
     * finds Element in List by Key
     *
     * @param list the List
     * @param key the Key
     * @return the Element
     */
    private Element findElementById(List<Element> list, String key) {
        for (Element e : list) {
            if (e.getAttributeValue("key").equals(key)) {
                return e;
            }
        }
        return null;
    }

    /**
     * Finds Node by Coordinates
     *
     * @param list the List
     * @param longitude the Longitude Coordinate
     * @param latitude the Latitude Coordinate
     * @return the Node
     */
    private Node findNodeByCoords(ArrayList<Node> list, double longitude, double latitude) {
        for (Node node : list) {
            if (Math.abs(node.getLongitude() - longitude) < 0.1 && Math.abs(node.getLatitude() - latitude) < 0.1) {
                return node;
            }
        }
        return null;
    }

    /**
     * Finds an Edge between source- and targetNode
     *
     * @param list the list
     * @param sourcenode the source Node
     * @param targetnode the target Node
     * @return the Edge between the Nodes
     */
    private Edge findEdge(ArrayList<Edge> list, Node sourcenode, Node targetnode) {
        for (Edge edge : list) {
            if ((edge.getSource().equals(sourcenode) && edge.getTarget().equals(targetnode))
                    || (edge.getSource().equals(targetnode) && edge.getTarget().equals(sourcenode))) {
                return edge;
            }
        }
        return null;
    }

    /**
     * Finds Edge between Source and Target Node, identified by NodeNumber
     *
     * @param list the list
     * @param source the source node number
     * @param target the target node number
     * @return the edge between source and target Node
     */
    private Edge findEdge(ArrayList<Edge> list, int source, int target) {
        for (Edge edge : list) {
            if ((edge.getSource().getNumber() == source && edge.getTarget().getNumber() == target)
                    || (edge.getSource().getNumber() == target && edge.getTarget().getNumber() == source)) {
                return edge;
            }
        }
        return null;
    }

    /**
     * Parses Nodes
     */
    public void readNodes() {

        List<Element> nodeelemlist = this.graph.getChildren("node", this.ns);
        int number = 1;
        for (Element e : nodeelemlist) {

            Element latitude_elem = findElementById(e.getChildren(), latitude_key);
            double latitude = (latitude_elem == null) ? 0 : Double.parseDouble(latitude_elem.getText());

            Element longitude_elem = findElementById(e.getChildren(), longitude_key);
            double longitude = (longitude_elem == null) ? 0 : Double.parseDouble(longitude_elem.getText());

            if (latitude == 0 || longitude == 0) {
                continue;
            }

            Element country_elem = findElementById(e.getChildren(), country_key);
            String country = (country_elem == null) ? "" : country_elem.getText();

            Element city_elem = findElementById(e.getChildren(), label_key);
            String city = (city_elem == null) ? "" : city_elem.getText();

            Element id_elem = findElementById(e.getChildren(), id_key);
            int id = Integer.parseInt(id_elem.getText());

            Node n = findNodeByCoords(nodelist, longitude, latitude);
            if (n != null) {
                n.addId(id);
            } else {
                Node node = new Node(number, city, id, country, longitude, latitude);
                nodelist.add(node);
                number++;
            }

        }
    }

    /**
     * Parses Edges
     */
    public void readEdges() {
        if (nodelist.isEmpty()) {
            logger.error("Read in Nodes first!");
            System.exit(-1);
        }
        List<Element> edgeelemlist = this.graph.getChildren("edge", this.ns);
        for (Element e : edgeelemlist) {
            Attribute source_attr = e.getAttribute("source");
            int source = Integer.parseInt(source_attr.getValue());

            Attribute target_attr = e.getAttribute("target");
            int target = Integer.parseInt(target_attr.getValue());

            Element linkspeed_elem = findElementById(e.getChildren(), linklabel_key);
            double linkspeed = 0.0;
            if (linkspeed_elem != null) {
                Scanner scanner = new Scanner(linkspeed_elem.getText());
                while (!scanner.hasNextDouble() && scanner.hasNext()) {
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
                continue;
            }
            Edge edge = new Edge(sourcenode, targetnode, linkspeed);
            this.edgelist.add(edge);
        }
    }

    /**
     * Creates Routing Table for Startnode using Dijkstra Algorithm
     *
     * @param startnode Startnode
     * @return Maximum Distance between Startnode and possible TargetNode
     */
    public int Dijkstra(Node startnode) {
        int[] distance = new int[nodelist.size()];
        int[] predecessor = new int[nodelist.size()];
        ArrayList<Node> q = new ArrayList<>();
        initializeDijkstra(startnode, distance, predecessor, q);
        while (!q.isEmpty()) {
            Node u = q.get(0);
            for (Node node : q) {
                if (distance[node.getNumber() - 1] < distance[u.getNumber() - 1]) {
                    u = node;
                }
            }
            q.remove(u);
            for (Edge e : edgelist) {

                if (e.getSource().getNumber() == u.getNumber()) {
                    Node v = e.getTarget();
                    if (q.contains(v)) {
                        distance_update(u, v, distance, predecessor);
                    }
                } else if (e.getTarget().getNumber() == u.getNumber()) {
                    Node v = e.getSource();
                    if (q.contains(v)) {
                        distance_update(u, v, distance, predecessor);
                    }
                }
            }
        }
        int maxD = distance[0];
        for (int i = 0; i < distance.length; i++) {

            if (distance[i] > maxD) {
                maxD = distance[i];
            }
        }
        edgesST = Arrays.copyOf(predecessor, predecessor.length);
        return maxD;
    }

    /**
     * Updates the Distance between two Nodes
     *
     * @param u
     * @param v
     * @param distance
     * @param predecessor
     */
    private void distance_update(Node u, Node v, int[] distance,
            int[] predecessor) {
        int alt = distance[u.getNumber() - 1] + 1;
        if (alt < distance[v.getNumber() - 1]) {
            distance[v.getNumber() - 1] = alt;
            predecessor[v.getNumber() - 1] = u.getNumber();
        }

    }

    /**
     *
     * @param startnode
     * @param distance
     * @param predecessor
     * @param q
     */
    private void initializeDijkstra(Node startnode, int[] distance, int[] predecessor, ArrayList<Node> q) {
        for (Node node : nodelist) {
            distance[node.getNumber() - 1] = Integer.MAX_VALUE;
            predecessor[node.getNumber() - 1] = -1;
            q.add(node);
        }
        distance[startnode.getNumber() - 1] = 0;
    }

    /**
     * Writes Parsed GraphML file to OFCProbe compatible Topology.ini File
     * Overwrites (possibly) existing File without asking
     */
    public void writeToTopologyFile() {
        if (nodelist.isEmpty() || edgelist.isEmpty()) {
            logger.error("No nodes or edges provided.");
            System.exit(-1);
        }
        int distance = Integer.MAX_VALUE;
        Node startnode = null;
        for (Node node : nodelist) {
            if (Dijkstra(node) <= distance) {
                distance = Dijkstra(node);
                startnode = node;
            }
        }
        Dijkstra(startnode);
        Iterator<Edge> iter = edgelist.iterator();

        while (iter.hasNext()) {
            Edge edge = iter.next();
            if ((edge.getTarget().getNumber() == edgesST[edge.getSource().getNumber() - 1])
                    || (edge.getSource().getNumber() == edgesST[edge.getTarget().getNumber() - 1])) {
                continue;
            }
            iter.remove();
        }
        int[] ports = new int[nodelist.size()];
        for (int i = 0; i < ports.length; i++) {
            ports[i] = 1;
        }

        Writer fw = null;

        try {
            fw = new FileWriter("topology.ini");
            fw.write("###Topology");
            fw.append(System.getProperty("line.separator"));
            fw.write("#ofSwitch:Port==ofSwitch:Port");
            fw.append(System.getProperty("line.separator"));
            for (Edge edge : edgelist) {
                fw.write(edge.getSource().getNumber() + ":" + (ports[edge.getSource().getNumber() - 1]++) + "=="
                        + edge.getTarget().getNumber() + ":" + (ports[edge.getTarget().getNumber() - 1]++));
                fw.append(System.getProperty("line.separator"));
            }
            NumberFormat dpidFormatter = new DecimalFormat("#000");
            for (int i = 0; i < nodelist.size(); i++) {
                String dpid = dpidFormatter.format(nodelist.get(i).getNumber());
                logger.info("Switch#{} \"{}\" (Nodeid:{}) has {} connections", dpid, nodelist.get(i).getName(), nodelist.get(i).getIds(), (ports[i] - 1));
            }
        } catch (IOException e) {
            logger.error("Exception during Creation of topology file");
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                    logger.info("Writing Topology-File successful!");

                } catch (IOException e) {
                    logger.error(e.getLocalizedMessage());
                }
            }
        }
    }

    /**
     * get the Node Count
     *
     * @return node Count
     */
    public int getNodeCount() {
        return nodelist.size();
    }
}
