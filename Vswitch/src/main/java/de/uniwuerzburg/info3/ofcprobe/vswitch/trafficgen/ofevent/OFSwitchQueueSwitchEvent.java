package de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.ofevent;

import de.uniwuerzburg.info3.ofcprobe.vswitch.connection.IOFConnection;

public class OFSwitchQueueSwitchEvent implements IOFEvent {

	private EventType type;
	private IOFConnection ofSwitch;
	
	public OFSwitchQueueSwitchEvent(IOFConnection ofSwitch){
		this.ofSwitch = ofSwitch;
		this.type = EventType.OFSWITCH_QUEUESWITCH_EVENT;
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
