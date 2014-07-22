package de.uniwuerzburg.info3.ofcprobe.vswitch.graphml;

import java.util.ArrayList;

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
		for (int i : idlist)
			System.out.print(" "+i);
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
