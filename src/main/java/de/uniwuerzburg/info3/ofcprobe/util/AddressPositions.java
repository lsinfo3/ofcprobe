/* 
 * Copyright 2016 christopher.metter.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
