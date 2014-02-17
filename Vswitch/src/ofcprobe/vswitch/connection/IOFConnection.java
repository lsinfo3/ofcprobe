/**
 * 
 */
package ofcprobe.vswitch.connection;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.List;

import ofcprobe.vswitch.runner.OFSwitchRunner;
import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFPhysicalPort;

/**
 * Interface for all ofSwitch Implementations. This is the current Set of required Methods per Implementation.
 * Basically Handles the OFMessages for a TCPSocket
 * @author Christopher Metter
 *
 */
public interface IOFConnection {
	
		
	/**
	 * Sends OFMessage to Controller.
	 * @param out
	 */
	public void send(OFMessage out);
	
	/**
	 * Receives OFMessage from controller.
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public void receive();
	
	/**
	 * Gets OFVersion 
	 * @return byte array 0x01 -> Version 1.0
	 */
	public byte getOFVersion();

	/**
	 * Sets the SocketChannel
	 * @param chan the SocketChannel
	 * @throws IOException can be thrown ;P
	 */
	public void setChannel(SocketChannel chan) throws IOException;
	
	/**
	 * Gets the SocketChannel
	 * @return theSocketChannel of this ofSwitch
	 */
	public SocketChannel getChannel();
	
	/**
	 * Returns String Representation
	 * @return String Representation
	 */
	public String toString();
	
	/**
	 * getNextFreeBufferId.
	 * @return next Free Buffer Id of this ofSwitch
	 */
	public int getNextFreeBufferId();
	
	/**
	 * Start Evaluation.
	 */
	public void evaluate();
	
	
	/**
	 * Start Reporting.
	 */
	public void report();
	
	/**
	 * Set Flag to Benching. All future Packets will now be processed by the Statistics MOdules 
	 */
	public void startSession();
	
	/**
	 * Stop Session. Wait for x Seconds (configurable), when in this interval no new packet comes in, the channel will be closed
	 */
	public void stopSession();
	
	/**
	 * queue a new Packet for the PacketInMsgQueue
	 * @param payload a complete TCP/UDP/... Packet as byte[]
	 * @param port the incoming Port of this
	 * @param safeFlag safe this payload?
	 */
	public void queuePacketIn(byte[] payload, short port, boolean safeFlag);
	
	/**
	 * queue multiple Packets (see #queuePacketIn(byte[] payload, int port)).
	 * 
	 * @param payloadList
	 */
	public void queuePacketInS(Collection<byte[]> payloadList);
	
	/**
	 * capsulate packets queue in OFPacketIn Messages and send them to Controller.
	 */
	public void sendPacketIn();
	
	/**
	 * Check if packetQueue of this ofSwitch Object is empty
	 * @return true-> packets queued, false -> queue is empty
	 */
	public boolean hasPacketInQueued();
	
	/**
	 * Returns the PacketInQueeLength.
	 * @return packetInQueue.length()
	 */
	public int packetInQueueLength();
	
	/**
	 * When has last OFPacketIn arrived.
	 * @return the time since last OFPacketIn
	 */
	public long lastPacketInTime();
	
	/**
	 * Gets the Thread for this ofSwitch.
	 * @return the Thread/Object running this ofSwitch
	 */
	public OFSwitchRunner getRunner();

	/**
	 * Has this ofSwitch already communicated in OF-Protocol?
	 * @return yes or no ;)
	 */
	public boolean hadOFComm();
	
	/**
	 * Returns the Initialization Time Delay of the Switch
	 * 0 = immediately, x = 0+x  
	 * @return Time after Time in Millis!
	 */
	public long getStartDelay();
	
	/**
	 * Returns the Stop Time Delay of the Switch
	 * 0 = immediately, x = 0+x  
	 * @return Time after Time in Millis!
	 */
	public long getStopDelay();

	/**
	 * The DPID
	 * @return dpid as long
	 */
	public long getDpid();
	
	/**
	 * Get the Switches ports
	 * @return the ports
	 */
	public List<OFPhysicalPort> getPorts();
	
	/**
	 * Get the IAT of generated Packet_ins
	 * @return
	 */
	public int getIAT();
	
	
	/**
	 * Get the FillThreshold for this ofSwitch
	 * @return the fillthreshold
	 */
	public int getFillThreshold();
	
	/**
	 * Get the PcapFileName. If not set, String is "notSet!"
	 * @return the PcapFileName
	 */
	public String getPcapFileName();
	
	/**
	 * Get the Set Distribution for this ofSwitch
	 * @return the Distribution; "none" when not set
	 */
	public String getDistribution();
	
	/**
	 * Get Distribution Parameter1
	 * @return
	 */
	public double getDistributionPara1();
	
	/**
	 * Get Distiribution Parameter2
	 * @return
	 */
	public double getDistributionPara2();
	
}
