package ofcprobe.vswitch.graphml;

public class Node {
	
	private int id;
	
	private String name;
	private String country;
	
	private double longitude;
	private double latitude;
	public Node(String name, int id, String country, double longitude, double latitude) {
		super();
		this.name = name;
		this.country = country;
		this.longitude = longitude;
		this.latitude = latitude;
		this.id = id;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
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
}
