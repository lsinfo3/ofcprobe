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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import cn.seed.pcap.parser.PCAPPackageParser;
import de.uniwuerzburg.info3.ofcprobe.vswitch.connection.IOFConnection;
import cn.seed.pcap.parser.Package;

/**
 * PreLoads a PCAP and delivers next PacketPayload from PCAP on request
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public class PCAPLoader {
	/**
	 * Logger
	 */
//	private static final Logger logger = LoggerFactory.getLogger(OFConnection1_zero.class);
	/**
	 * The PCAP to load
	 */
	private String fileName;
	/**
	 * List of Payloads
	 */
	private List<byte[]> payloads;
	/**
	 * Iterator
	 */
	private Iterator<byte[]> iterator;

	/**
	 * Constructor
	 * @param ofSwitch
	 */
	PCAPLoader(IOFConnection ofSwitch){
		this.fileName = ofSwitch.getPcapFileName();
		
		this.payloads = new ArrayList<byte[]>();
	}
	
	/**
	 * Preload PCAP and safe Payloads in List
	 */
	public void load(){
		PCAPPackageParser parser = new PCAPPackageParser(this.fileName);
		Package packet = parser.getNextPackage();
		while (packet != null) {
			this.payloads.add(packet.data.raw_data);
			packet = parser.getNextPackage();
		}
		parser.close();
		this.iterator = this.payloads.iterator();
	}

	
	/**
	 * Gets the Next Payload, if Last Payload reached, start at payload#1
	 * @return Payload
	 */
	public byte[] nextPayload(){
		if (this.iterator.hasNext())
			return this.iterator.next();
		else 
			this.iterator = this.payloads.iterator();
			return this.iterator.next();
	}

}
