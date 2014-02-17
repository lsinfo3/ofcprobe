/**
 * 
 */
package ofcprobe.vswitch.trafficgen.ofevent;

import ofcprobe.vswitch.connection.IOFConnection;

/**
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public class ARPEvent implements IOFEvent {

	private EventType type;
	private IOFConnection ofSwitch;
	
	public ARPEvent(IOFConnection ofSwitch){
		this.ofSwitch = ofSwitch;
		this.type = EventType.ARP_EVENT;
	}
	
	/* (non-Javadoc)
	 * @see ofcprobe.vswitch.trafficgen.ofevent.IOFEvent#getType()
	 */
	@Override
	public EventType getType() {
		return this.type;
	}

	/* (non-Javadoc)
	 * @see ofcprobe.vswitch.trafficgen.ofevent.IOFEvent#getCon()
	 */
	@Override
	public IOFConnection getCon() {
		return this.ofSwitch;
	}
	
	public String toString(){
		String output = new String();
		output += "Type:" + this.type + ";Con:"+this.ofSwitch.toString();
		return output;
	}

}
