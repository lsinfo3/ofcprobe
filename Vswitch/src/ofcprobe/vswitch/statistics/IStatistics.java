/**
 * 
 */
package ofcprobe.vswitch.statistics;

import org.openflow.protocol.OFMessage;

/**
 * Interface for Statistic Modules, every written Module has to have these Methods.
 * @author Christopher Metter
 *
 */
public interface IStatistics {
	
	/**
	 * Defines the targetFile
	 * @param file outputfile
	 */
	public void setReportFile(String file);
	
	/**
	 * Incoming Packet FROM the Controller
	 * @param in OFMessage from Controller
	 */
	public void packetIn(OFMessage in);
	
	/**
	 * Outgoing Packet TO the Controller
	 * @param out OFMessage to Controller
	 */
	public void packetOut(OFMessage out);
	
	/**
	 * Evaluate Modules Statistics and produce results
	 */
	public void evaluate();
	
	/**
	 * Do tha reportin'
	 */
	public void report();

	/**
	 * Session now Started
	 */
	public void start();
	
	/**
	 * Session now over
	 */
	public void stop();
}
