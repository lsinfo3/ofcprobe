/**
 * 
 */
package ofcprobe.util;

/**
 * AddressPositions for TCP / ARP Packets
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public class AddressPositions {
	// EthernetLayer
	public static final int ETHER_MAC_DST = 0;
	public static final int ETHER_MAC_SRC = 6;
	public static final int ETHER_TYPE = 12;
	
	// IP STUFF
	public static final int IP_PROTOCOL = 23;
	public static final int IP_DST = 30;
	public static final int IP_SRC = 26;
	
	
	// TCP STUFF
	public static final int TCP_PORT_SRC = 34;
	public static final int TCP_PORT_DST = 36;
	public static final int TCP_FLAGS = 46;
	
	
	// ARP STUFF
	public static final int ARP_MAC_SRC = 22;
	public static final int ARP_IP_SRC = 28;
	public static final int ARP_MAC_DST = 32;
	public static final int ARP_IP_DST = 38;
	public static final int ARP_PADDING = 42;
	public static final int ARP_OPCODE = 20;
}
