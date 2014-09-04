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

/**
 *
 * @author christian rachor
 */
public class Node {

    private ArrayList<Integer> idlist;

    private int number;

    private String name;
    private String country;

    private double longitude;
    private double latitude;

    public Node(int number, String name, int id, String country, double longitude, double latitude) {
        super();
        this.name = name;
        this.country = country;
        this.longitude = longitude;
        this.latitude = latitude;
        this.idlist = new ArrayList<Integer>();
        this.idlist.add(id);
        this.setNumber(number);
    }

    public ArrayList<Integer> getIds() {
        return this.idlist;
    }

    public void addId(int id) {
        this.idlist.add(id);
    }

    public void printIds() {
        System.out.print(name + ":");
        for (int i : idlist) {
            System.out.print(" " + i);
        }
        System.out.println();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
