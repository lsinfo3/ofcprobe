/**
 * 
 */
package de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.ofevent;

import de.uniwuerzburg.info3.ofcprobe.vswitch.connection.IOFConnection;

/**
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public class OFSwitchDisconnectEvent implements IOFEvent {

	private EventType type;
	private IOFConnection ofSwitch;
	
	public OFSwitchDisconnectEvent(IOFConnection ofSwitch){
		this.ofSwitch = ofSwitch;
		this.type = EventType.OFSWITCH_DISCONNECT_EVENT;
	}
	
	@Override
	public EventType getType() {
		return this.type;
	}

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
