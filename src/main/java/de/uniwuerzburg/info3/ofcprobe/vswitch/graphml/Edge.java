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

    public Edge(Node source, Node target, double linkspeed) {
        super();
        this.source = source;
        this.target = target;
        this.linkspeed = linkspeed;
    }

    public Node getSource() {
        return source;
    }

    public void setSource(Node source) {
        this.source = source;
    }

    public Node getTarget() {
        return target;
    }

    public void setTarget(Node target) {
        this.target = target;
    }

    public double getLinkspeed() {
        return linkspeed;
    }

    public void setLinkspeed(double linkspeed) {
        this.linkspeed = linkspeed;
    }

}
