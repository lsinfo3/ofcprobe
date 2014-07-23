/* 
 * Copyright (C) 2014 Christopher Metter
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.uniwuerzburg.info3.ofcprobe.util;

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
