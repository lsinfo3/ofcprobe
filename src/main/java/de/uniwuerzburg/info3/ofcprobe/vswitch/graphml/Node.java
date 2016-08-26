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
