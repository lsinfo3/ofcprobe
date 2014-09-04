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
