package ofcprobe.vswitch.trafficgen.ofevent;

import ofcprobe.vswitch.connection.IOFConnection;

public class GenerationEndEvent implements IOFEvent {

	private EventType type;
	private IOFConnection ofSwitch;
	
	public GenerationEndEvent(IOFConnection ofSwitch){
		this.ofSwitch = ofSwitch;
		this.type = EventType.GENERATION_END;
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
