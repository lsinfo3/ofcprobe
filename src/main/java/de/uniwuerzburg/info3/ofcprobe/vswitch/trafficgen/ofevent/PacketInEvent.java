package de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.ofevent;

import de.uniwuerzburg.info3.ofcprobe.vswitch.connection.IOFConnection;

public class PacketInEvent implements IOFEvent {
	
	private EventType type;
	private IOFConnection ofSwitch;
	
	/**
	 * Constructor
	 * @param ofSwitch
	 */
	public PacketInEvent(IOFConnection ofSwitch){
		this.ofSwitch = ofSwitch;
		this.type = EventType.PACKET_IN_EVENT;
	}

	@Override
	public IOFConnection getCon(){
		return this.ofSwitch;
	}

	@Override
	public EventType getType() {
		return this.type;
	}
	
	
	public String toString(){
		String output = new String();
		output += "Type:" + this.type + ";Con:"+this.ofSwitch.toString();
		return output;
	}

}
