/**
 * 
 */
package ofcprobe.vswitch.main.config;

import java.net.InetSocketAddress;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * The Configuration of ofcprobe.vswitch.connection.IOFConnection Implementation
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public class SwitchConfig {

	private NumberFormat dpidFormatter = new DecimalFormat("#000");
	/**
	 * The Address of the Controller
	 */
	private InetSocketAddress contAddress;
	/**
	 * How many Ports per ofSwitch?
	 */
	private int portCountperSwitch;
	/**
	 * How many Buffers per ofSwitch
	 */
	private int buffersPerSwitch;
	/**
	 * The SessionNumber
	 */
	private int session;
	/**
	 * The DPID for this ofSwitch
	 */
	private int dpid;
	/**
	 * Send for real?
	 */
	private boolean sendFlag;
	/**
	 * Send queued PacketIn msgs as batch or one-by-one?
	 */
	private boolean batchSending;
	/**
	 * Disable Nagle? Default: True
	 */
	private boolean disableNagle;
	/**
	 * Maximum flowTableSize per ofSwitch
	 */
	private int flowTableSize;
	private boolean randomizeStats;

	/**
	 * Constructor with default values.
	 */
	public SwitchConfig(){
		this.portCountperSwitch = 4;
		this.buffersPerSwitch = 256;
		this.session = 1;
		this.contAddress = new InetSocketAddress("127.0.0.1", 6633);
		this.dpid = 1;
		this.sendFlag = true;
		this.batchSending = false;
		this.disableNagle = true;
		this.flowTableSize = 128;
		this.randomizeStats = true;
	}
	
	
	/**
	 * Set the number of Ports per ofSwitch
	 * @param count number of Ports
	 */
	public void setPortCountperSwitch(int count){
		this.portCountperSwitch = count;
	}
	
	/**
	 * Get the number of Ports per ofSwitch
	 * @return number of Ports
	 */
	public int getPortCountperSwitch() {
		return this.portCountperSwitch;
	}

	/**
	 * Set the number of Buffers per ofSwitch
	 * @param buffCount the number of buffers
	 */
	public void setBuffersPerSwitch(int buffCount) {
		this.buffersPerSwitch = buffCount;
	}
	
	/**
	 * Gets the number of Buffers per ofSwitch
	 * @return the number of buffers
	 */
	public int getBuffersPerSwitch() {
		return this.buffersPerSwitch;
	}
	
	/**
	 * Sets the Session
	 * @param sess the current Session
	 */
	public void setSession(int sess){
		this.session = sess;
	}
	
	/**
	 * Get the Session
	 * @return the current Session
	 */
	public int getSession(){
		return this.session;
	}
	
	/**
	 * Get Controller Address
	 * @return the address
	 */
	public InetSocketAddress getContAddress() {
		return this.contAddress;
	}
	
	/**
	 * Set the Controller Address
	 * @param sock the Controller address as InetSocketAddress
	 */
	public void setContAddress(InetSocketAddress sock) {
		this.contAddress = sock;
	}
	
	/**
	 * Set the dpid for this ofSwitch
	 * @param dpid wanted dpid
	 */
	public void setDpid(int dpid){
		this.dpid = dpid;
	}
	
	/**
	 * Get the Dpid of this ofSwitch
	 * @return the dpid of this ofSwitch
	 */
	public int getDpid(){
		return this.dpid;
	}
	
	/**
	 * Send for real?
	 * @param flag true -> no sending at all
	 */
	public void setSendFlag(boolean flag){
		this.sendFlag = flag;
	}
	
	/**
	 * Send for real?
	 * @return true -> no sending at all
	 */
	public boolean getSendFlag() {
		return this.sendFlag;
	}
	
	/**
	 * Set wether queued PacketInMsgs should be processed all at once or one-after-another
	 * @param flag true -> all at once
	 */
	public void setBatchSending(boolean flag){
		this.batchSending = flag;
	}
	
	/**
	 * Get wether queued PacketInMsgs should be processed all at once or one-after-another
	 * @return true -> all at once
	 */
	public boolean getBatchSending(){
		return this.batchSending;
	}

	/**
	 * DisableNagle?
	 * @return flag
	 */
	public boolean disableNagle() {
		return this.disableNagle;
	}
	
	/**
	 * Set the DisableNagle Flag
	 * @param flag
	 */
	public void setDisableNagle(boolean flag) {
		this.disableNagle = flag;
	}
	
	public String toString(){
		String output = "SwitchConfig: ControllerAddress="+this.contAddress + "; Session=" + this.session 
				+ "; DPID=" + this.dpid + "; portCountPerSwitch=" + this.portCountperSwitch + "; BuffersPerSwitch="+this.buffersPerSwitch
				+ "; SendFlag=" + this.sendFlag + "; BatchSending=" + this.batchSending + "; disableNagle=" + this.disableNagle;
		return output;
	}

	/**
	 * The FlowTableSize
	 * @return the size
	 */
	public int getFlowTableSize() {
		return this.flowTableSize;
	}
	
	/**
	 * Sets the FlowTableSize
	 * @param size the Size
	 */
	public void setFlowTableSize(int size){
		this.flowTableSize = size;
	}
	
	/**
	 * Stats are randomized? 
	 * @return 
	 */
	public boolean getRandomizeStats(){
		return this.randomizeStats;
	}
	
	/**
	 * Sets stat Randomization
	 * @param flag
	 */
	public void setRandomizeStats(boolean flag){
		this.randomizeStats = flag;
	}
	
	/**
	 * Get DPIDString 
	 * @return formatted String of Type: ofSwitch#001
	 */
	public String getDPIDString(){
		return dpidFormatter.format(this.dpid);
	}
}
