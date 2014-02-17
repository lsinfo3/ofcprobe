/**
 * 
 */
package ofcprobe.vswitch.connection;

import ofcprobe.util.AddressPositions;
import ofcprobe.util.Util;

/**
 * A MetaPacket for storing Packets in the Queue
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public class QueuedPacket {
	
	/**
	 * The Payload
	 */
	private byte[] payload;
	/**
	 * The ingress port
	 */
	private short port;
	/**
	 * The SafeFlag
	 */
	private boolean safeFlag;
	
	/**
	 * Constructor
	 * @param payload packet as byte[]
	 * @param port ingress Port
	 * @param safeFlag safe it for arp/tcp ing?
	 */
	public QueuedPacket(byte[] payload, short port, boolean safeFlag){
		this.payload = payload;
		this.port = port;
		this.safeFlag = safeFlag;
	}
	
	/**
	 * Get the Payload
	 * @return the Payload
	 */
	public byte[] getPayload(){
		return this.payload;
	}
	
	/**
	 * Get the Port
	 * @return the Port
	 */
	public short getPort(){
		return this.port;
	}
	
	/**
	 * Get Safe Flag
	 * @return the flag
	 */
	public boolean getSafeFlag(){
		return this.safeFlag;
	}

	public String toString(){
		return Util.fromIPvAddressBytes(Util.getBytes(this.payload, AddressPositions.ARP_IP_DST, 4));
	}
}
