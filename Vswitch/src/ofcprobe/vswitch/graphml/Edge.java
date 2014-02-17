package ofcprobe.vswitch.graphml;

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
