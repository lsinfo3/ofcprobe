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

/**
 *
 * @author christian rachor
 */
public class Edge {

    private Node source;
    private Node target;

    private double linkspeed;

    /**
     * Constructor
     *
     * @param source Source Node
     * @param target Target Node
     * @param linkspeed Linkspeed in Mbps
     */
    public Edge(Node source, Node target, double linkspeed) {
        super();
        this.source = source;
        this.target = target;
        this.linkspeed = linkspeed;
    }

    /**
     * Gets Source Node
     *
     * @return source Node
     */
    public Node getSource() {
        return source;
    }

    /**
     * Set Source Node
     *
     * @param source
     */
    public void setSource(Node source) {
        this.source = source;
    }

    /**
     * Get Target Node
     *
     * @return the Target Node
     */
    public Node getTarget() {
        return target;
    }

    /**
     * Set Target Node
     *
     * @param target the target Node
     */
    public void setTarget(Node target) {
        this.target = target;
    }

    /**
     * Get LinkSpeed in Mbps
     *
     * @return the LinkSpeed in Mbps
     */
    public double getLinkspeed() {
        return linkspeed;
    }

    /**
     * Set LinkSpeed in Mbps
     *
     * @param linkspeed linkspeed in Mbps
     */
    public void setLinkspeed(double linkspeed) {
        this.linkspeed = linkspeed;
    }

}
