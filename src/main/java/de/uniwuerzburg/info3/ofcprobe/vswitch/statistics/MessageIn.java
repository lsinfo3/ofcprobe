package de.uniwuerzburg.info3.ofcprobe.vswitch.statistics;

import org.openflow.protocol.OFPacketIn;

import de.uniwuerzburg.info3.ofcprobe.vswitch.connection.IOFConnection;

/**
 * Dont know for what this is
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public class MessageIn {
	
	private OFPacketIn msg;
	private IOFConnection con;

	public MessageIn(OFPacketIn in, IOFConnection con){
		this.msg = in;
		this.con = con;
	}
	
	public OFPacketIn getMsg(){
		return this.msg;
	}
	
	public IOFConnection getCon(){
		return this.con;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((con == null) ? 0 : con.hashCode());
		result = prime * result + ((msg == null) ? 0 : msg.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MessageIn other = (MessageIn) obj;
		if (con == null) {
			if (other.con != null)
				return false;
		} else if (!con.equals(other.con))
			return false;
		if (msg == null) {
			if (other.msg != null)
				return false;
		} else if (!msg.equals(other.msg))
			return false;
		return true;
	}

}
