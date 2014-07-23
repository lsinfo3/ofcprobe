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
package de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen;

import org.openflow.util.HexString;

import de.uniwuerzburg.info3.ofcprobe.util.AddressPositions;
import de.uniwuerzburg.info3.ofcprobe.util.Util;
import de.uniwuerzburg.info3.ofcprobe.vswitch.main.config.Config;
import de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.ipgen.IIpGenerator;
import de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.ipgen.RandomIPv4Generator;
import de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.ipgen.SerialIPGenerator;
import de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.macgen.IMacGen;
import de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.macgen.RandomMacGen;
import de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.macgen.SerialMacGen;
import de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.portgen.IPortGen;
import de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.portgen.RandomPortGen;
import de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.portgen.SerialPortGen;

/**
 * This Class generates Payload. atm only random TCP Syns supported
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public class PayLoadGen {
	
	/**
	 * Connected IPGenerator
	 */
	private IIpGenerator ipGen;
	/**
	 * Connected MACGenerator
	 */
	private IMacGen macGen;
	/**
	 * Connected PortGenerator
	 */
	private IPortGen portGen;
	/**
	 * Here to save the world.
	 */
	private final byte[] tcpSynMaster ;
	
	/**
	 * Constructor, initializes the Generators
	 */
	public PayLoadGen(Config config){
		
		switch (config.getTrafficGenConfig().getMacGenType()) {
		case RANDOM:
			this.macGen = new RandomMacGen();
			break;
		case SERIAL:
			this.macGen = new SerialMacGen();
			break;
		default:
			this.macGen = new SerialMacGen();
			break;
		}
		
		switch (config.getTrafficGenConfig().getIPGenType()) {
		case RANDOM: 
			this.ipGen = new RandomIPv4Generator();
			break; 
		case SERIAL:
			this.ipGen = new SerialIPGenerator();
			break; 
		default:
			this.ipGen = new SerialIPGenerator();
			break;
		}
		
		switch(config.getTrafficGenConfig().getPortGenType()) {
		case RANDOM:
			this.portGen = new RandomPortGen();
			break;
		case SERIAL:
			this.portGen = new SerialPortGen();
			break;
		default:
			this.portGen = new SerialPortGen();
			break;
		}

		this.tcpSynMaster = preGenerateTcpSyn();
	}
	  
	/**
	 * Pregenerates a TCPSYN Packet.
	 * So all Fields of the Packet that are always the same are set.
	 * @return a pregenarted TCPSyn
	 */
	private byte[] preGenerateTcpSyn(){
		byte[] packet = new byte[66];
		// Inserting MAC Header Fields
		byte[] macType = Util.toByteArray("0800"); // Type = IP
		packet = Util.insertByteArray(packet, macType, AddressPositions.ETHER_TYPE);
		
		// Inserting IP Header Fields
		byte[] ipverServiceLengthIdentiFlagsTTLProtocol = Util.toByteArray("450000345ad340008006"); // IP Version + Services + TotalIPHeaderLength + Identification + IPFlags + TTL + Protocol(TCP)
//		byte[] checksum = toByteArray("0000");  //std checksum (used for ip and tcp)
		packet = Util.insertByteArray(packet, ipverServiceLengthIdentiFlagsTTLProtocol, 14);
//		packet = insertByteArray(packet, checksum, 24);
			
		// Inserting TCP Header Fields
		byte[] seqNrFlagsWindowsize = Util.toByteArray("6a9ef8220000000080023908");  // TCP Seq NR, Flags(SYN), WindowSize
		byte[] options = Util.toByteArray("020405b40402080a091273c70000000001030307"); // TCP Options
		packet = Util.insertByteArray(packet, seqNrFlagsWindowsize, 38);
//		packet = insertByteArray(packet, checksum, 50);
		packet = Util.insertByteArray(packet, options, 54);
				
		return packet;
	}
	
	
	/**
	 * Generate new TCPSyn Packet
	 * @return a randomly genrated TCPSyn Packet
	 */
	public byte[] generateTCPSyn(){
		byte[] packet = tcpSynMaster;
		
		packet = buildMacHeader(packet, this.macGen.getMac(), this.macGen.getMac());
		packet = buildIpHeader(packet, this.ipGen.getIp(), this.ipGen.getIp());
		packet = buildTcpHeader(packet, this.portGen.getPort(), this.portGen.getPrivilegdedPort());
		
		return packet;
		
	}
	
	/**
	 * Creates a Custom TCP Syn with provided addresses and random Ports
	 * @param macSrc the MacSrc
	 * @param macDst the MacDst
	 * @param ipSrc the IPSrc
	 * @param ipDst the IPDst
	 * @return the TCPSyn
	 */
	public byte[] generateCustomTCPSyn(byte[] macSrc, byte[] macDst, byte[] ipSrc, byte[] ipDst) {
		byte[] packet = tcpSynMaster;
		
		packet = buildMacHeader(packet, macSrc, macDst);
		packet = buildIpHeader(packet, ipSrc, ipDst);
		packet = buildTcpHeader(packet, this.portGen.getPort(), this.portGen.getPrivilegdedPort());
		
		return packet;
	}
	
	/**
	 * Generate Custom TCP Syn with provided addresses (as String) and random Ports
	 * @param macSrc the MacSrc
	 * @param macDst the MacDst
	 * @param ipSrc the IPSrc
	 * @param ipDst the IPDst
	 * @return the TCPSyn
	 */
	public byte[] generateCustomTCPSynfromStrings(String macSrc, String macDst, String ipSrc, String ipDst) {
		byte[] packet = tcpSynMaster;
		
		packet = buildMacHeader(packet, HexString.fromHexString(macSrc), HexString.fromHexString(macDst));
		packet = buildIpHeader(packet, Util.toIPv4AddressBytes(ipSrc), Util.toIPv4AddressBytes(ipDst));
		packet = buildTcpHeader(packet, this.portGen.getPort(), this.portGen.getPrivilegdedPort());
		
		return packet;
	}
    
	/**
	 * Builds the TCP Header with Random Ports.
	 * @param packet the packet in which the TCP Header will be inserted
	 * @return the inserted packet with TCP Header.
	 */
	private byte[] buildTcpHeader(byte[] packet, byte[] srcPort, byte[] dstPort){
//		String src = Integer.toHexString(this.portGen.getPort());
//		String dst = Integer.toHexString(this.portGen.getPrivilegdedPort());
//		if (src.length() % 2 != 0) {
//			src = "0" + src;
//		}
//		if (dst.length() % 2 != 0){
//			dst = "0"+dst;
//		}
//		
//		byte[] srcPort = toByteArray(src);
//		byte[] dstPort = toByteArray(dst);
		
//		byte[] seqNrFlagsWindowsize = toByteArray("6a9ef822a0023908");
//		byte[] seqNr = toByteArray("6a9ef822");
//		byte[] flags = toByteArray("a002");
//		byte[] windowsSize = toByteArray("3908");
//		byte[] checksum = toByteArray("0000");
//		byte[] options = toByteArray("020405b40402080a091273c70000000001030307");
		
		packet = Util.insertByteArray(packet, srcPort, AddressPositions.TCP_PORT_SRC);
		packet = Util.insertByteArray(packet, dstPort, AddressPositions.TCP_PORT_DST);
//		packet = insertByteArray(packet, seqNrFlagsWindowsize, 38);
//		packet = insertByteArray(packet, seqNr, 38);
//		packet = insertByteArray(packet, flags, 46);
//		packet = insertByteArray(packet, windowsSize, 48);
//		packet = insertByteArray(packet, checksum, 50);
//		packet = insertByteArray(packet, options, 54);
		
		long checksumL = checksum(packet, 32, 34);

		Util.insertLong(packet, checksumL, 50, 2);
		
		return packet;
	}
	
	/**
	 * Builds the IP Header with Random IPs.
	 * @param packet the packet in which the IP Header will be inserted
	 * @return the inserted packet with IP Header.
	 */
	private byte[] buildIpHeader(byte[] packet, byte[] ipSrc, byte[] ipDst){
//		byte[] ipverServiceLengthIdentiFlagsTTLProtocol = toByteArray("450000345ad340008006");
//		byte[] ipVer = toByteArray("45");
//		byte[] serviceField = toByteArray("00");
//		byte[] totalLength = toByteArray("0034");
//		byte[] identi = toByteArray("5ad3");
//		byte[] flags = toByteArray("4000");
//		byte[] ttl = toByteArray("80");
//		byte[] protocol = toByteArray("06");
//		byte[] ipHeaderchecksumStd = toByteArray("0000");
//		byte[] headerchecksum = checksum(packet)
				
		
//		packet = insertByteArray(packet, ipverServiceLengthIdentiFlagsTTLProtocol, 14);
//		packet = insertByteArray(packet, ipVer,  14);
//		packet = insertByteArray(packet, serviceField, 15);
//		packet = insertByteArray(packet, totalLength, 16);
//		packet = insertByteArray(packet, identi, 18);
//		packet = insertByteArray(packet, flags, 20);
//		packet = insertByteArray(packet, ttl, 22);
//		packet = insertByteArray(packet, protocol, 23);
//		packet = insertByteArray(packet, checksum, 24);
		packet = Util.insertByteArray(packet, ipSrc, 26);
		packet = Util.insertByteArray(packet, ipDst, 30);
		
		long checksum = checksum(packet, 20, 14);
		
		Util.insertLong(packet, checksum, 24, 2);
		
		return packet;
	}
	
	/**
	 * Builds the MAC Header with MAC Addresses.
	 * @param packet the packet in which the MAC Header will be inserted
	 * @return the inserted packet with MAC Header.
	 */
	private byte[] buildMacHeader(byte[] packet, byte[] macSrc, byte[] macDst){

//		byte[] macType = toByteArray("0800");
				
		packet = Util.insertByteArray(packet, macDst, AddressPositions.ETHER_MAC_DST);
		packet = Util.insertByteArray(packet, macSrc, AddressPositions.ETHER_MAC_SRC);
//		packet = insertByteArray(packet, macType, 12);
		
		return packet;
	}
	
	/**
	 * Calculates a checksum for IP or TCP Header
	 * @param buf the packet with the header
	 * @param length length of the header
	 * @param offset startingposition of the header
	 * @return checksum of the header
	 */
	private long checksum(byte[] buf, int length, int offset) {
	    int i = 0;
	    long sum = 0;
	    while (length > 0) {
	        sum += (buf[offset+i++]&0xff) << 8;
	        if ((--length)==0) break;
	        sum += (buf[offset+i++]&0xff);
	        --length;
	    }

	    return (~((sum & 0xFFFF)+(sum >> 16)))&0xFFFF;
	}
	

	


}
