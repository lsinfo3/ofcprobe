/**
 * 
 */
package de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.ofevent;

import de.uniwuerzburg.info3.ofcprobe.vswitch.connection.IOFConnection;

/**
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public class TCPafterARP implements IOFEvent {

	private EventType type;
	private IOFConnection ofSwitch;
	
	public TCPafterARP(IOFConnection ofSwitch){
		this.ofSwitch = ofSwitch;
		this.type = EventType.TCP_AFTER_ARP;
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
