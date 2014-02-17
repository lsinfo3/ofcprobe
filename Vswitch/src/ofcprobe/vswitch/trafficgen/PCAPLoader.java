/**
 * 
 */
package ofcprobe.vswitch.trafficgen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import cn.seed.pcap.parser.PCAPPackageParser;
import ofcprobe.vswitch.connection.IOFConnection;
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
