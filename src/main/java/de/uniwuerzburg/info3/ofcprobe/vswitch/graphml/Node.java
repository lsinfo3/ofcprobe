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

import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author christian rachor
 */
public class Node {

    private static final Logger logger = LoggerFactory.getLogger(Node.class);

    private ArrayList<Integer> idlist;

    private int number;

    private String name;
    private String country;

    private double longitude;
    private double latitude;

    /**
     * Constructor
     *
     * @param number
     * @param name
     * @param id
     * @param country
     * @param longitude
     * @param latitude
     */
    public Node(int number, String name, int id, String country, double longitude, double latitude) {
        super();
        this.name = name;
        this.country = country;
        this.longitude = longitude;
        this.latitude = latitude;
        this.idlist = new ArrayList<>();
        this.idlist.add(id);
        this.setNumber(number);
    }

    /**
     * Get IDs
     *
     * @return the IDs
     */
    public ArrayList<Integer> getIds() {
        return this.idlist;
    }

    /**
     * Add ID
     *
     * @param id the id
     */
    public void addId(int id) {
        this.idlist.add(id);
    }

    /**
     * Prints ID
     */
    public void printIds() {
        System.out.print(name + ":");
        for (int i : idlist) {
            System.out.print(" " + i);
        }
        System.out.println();
    }

    /**
     * Gets the Node Name
     *
     * @return the Node Name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the Node Name
     *
     * @param name the Node Name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the Country the Node is in
     *
     * @return the Country
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the Country the Node is in
     *
     * @param country the Country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Get Longitude Location of Node
     *
     * @return the Longitude location
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Set the Longitude Location of Node
     *
     * @param longitude the Longitude Location
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Get Latitude Location of the Node
     *
     * @return the Latitude Location
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Set Latitude Location of the Node
     *
     * @param latitude
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Gets the Node Number
     *
     * @return the Number
     */
    public int getNumber() {
        return number;
    }

    /**
     * Sets the Node Number
     *
     * @param number
     */
    public void setNumber(int number) {
        this.number = number;
    }
}
