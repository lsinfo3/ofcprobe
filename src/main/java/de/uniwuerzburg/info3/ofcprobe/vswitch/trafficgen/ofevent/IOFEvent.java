package de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.ofevent;



import de.uniwuerzburg.info3.ofcprobe.vswitch.connection.IOFConnection;

public interface IOFEvent {
	
	/**
	 * Returns the EventType
	 * @return EventType
	 */
	public EventType getType();
	
	
	/**
	 * Returns the IOFConnection of this Event
	 * @return ofSwitch
	 */
	public IOFConnection getCon();
	

}
