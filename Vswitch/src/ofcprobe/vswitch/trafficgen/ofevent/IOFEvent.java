package ofcprobe.vswitch.trafficgen.ofevent;



import ofcprobe.vswitch.connection.IOFConnection;

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
