/**
 * 
 */
package ofcprobe.vswitch.connection.flowtable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ofcprobe.vswitch.graphml.GraphmlParser;

import org.openflow.protocol.OFFlowMod;
import org.openflow.protocol.action.OFAction;
import org.openflow.protocol.action.OFActionOutput;
import org.openflow.protocol.action.OFActionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Entries of a Flow in the Flow Table
 * containing Actions and Counters
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public class OFFlowTableEntry {
	
	private static final Logger logger = LoggerFactory.getLogger(OFFlowTableEntry.class);
	
	private List<OFAction> actionList;
	private short priority;
	private short hardTimeOut;
	private short idleTimeOut;
	private boolean notifyOnDelete;
	private long cookie;
	private long byteCount;
	private long packetCount;
	private int flowCount;
	private long nanoTime;
	
	/**
	 * Constructor
	 * @param flow_mod to read fields
	 */
	public OFFlowTableEntry(OFFlowMod flow_mod) {
		this.nanoTime = System.nanoTime();
		this.actionList = new ArrayList<OFAction>(flow_mod.getActions());
		this.priority = flow_mod.getPriority();
		this.hardTimeOut = flow_mod.getHardTimeout();
		this.idleTimeOut = flow_mod.getIdleTimeout();
		this.cookie = flow_mod.getCookie();
		this.byteCount = 0;
		this.packetCount = 0;
		this.flowCount = 0;
		if (flow_mod.getFlags() == OFFlowMod.OFPFF_SEND_FLOW_REM) {
			this.notifyOnDelete = true;
		} else {
			this.notifyOnDelete = false;
		}
	}
	
	/**
	 * Constructor
	 */
	public OFFlowTableEntry(){
		this(new OFFlowMod());
	}
	
	/**
	 * Get OFActions
	 * @return the OFactions
	 */
	public List<OFAction> getActions(){
		return this.actionList;
	}
	
	/**
	 * Add OFAction
	 * @param action OFaction to Add
	 */
	public void addAction(OFAction action){
		if (!this.actionList.contains(action)) {
			this.actionList.add(action);
		}
	}
	
	/**
	 * Add all OFActions
	 * @param actions the OFactions
	 */
	public void addAllActions(List<OFAction> actions) {
		Iterator<OFAction> iter = actions.iterator();
		while (iter.hasNext()) {
			addAction(iter.next());
		}
	}
	
	/**
	 * Set OFActions
	 * @param actionList the OFActions
	 */
	public void setActions(List<OFAction> actionList) {
		this.actionList.clear();
		this.actionList.addAll(actionList);
	}
	
	/**
	 * Delete OFAction 
	 * @param action action to Delete
	 * @return
	 */
	public boolean delAction(OFAction action){
		return this.actionList.remove(action);
	}
	
	/**
	 * Does OFAction contain Port?
	 * @param port the port to check
	 * @return true if ofaction contains outport
	 */
	public boolean actionsContainOutport(short port){
		Iterator<OFAction> iter = this.actionList.iterator();
		while (iter.hasNext()) {
			OFAction action = iter.next();
			if (action.getType().equals(OFActionType.OUTPUT)) {
				OFActionOutput outputAction = (OFActionOutput) action;
				if (outputAction.getPort() == port) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Get Flow Prio
	 * @return the flow Priority
	 */
	public short getPriority(){
		return this.priority;
	}
	
	/**
	 * Get HardTimeOut
	 * @return the HardtimeOut
	 */
	public short getHardTimeOut(){
		return this.hardTimeOut;
	}
	
	/**
	 * Get IdleTimeOut
	 * @return the idleTimeOut
	 */
	public short getIdleTimeOut(){
		return this.idleTimeOut;
	}
	
	/**
	 * Notify on Delete
	 * @return notify on delete?
	 */
	public boolean getNotifyOnDelete(){
		return this.notifyOnDelete;
	}
	
	/**
	 * Update Flow Entry Cookie
	 * @param cookie the new Flow Entry Cookie
	 */
	public void updateCookie(long cookie){
		this.cookie = cookie;
	}
	
	/**
	 * Get The Cookie
	 * @return the Cookie
	 */
	public long getCookie(){
		return this.cookie;
	}
	
	/**
	 * Get The PacketCounter
	 * @return the PacketCounter
	 */
	public long getPacketCounter(){
		return this.packetCount;
	}
	
	/**
	 * Get the Byte Counter
	 * @return the Byte Counter
	 */
	public long getByteCounter(){
		return this.byteCount;
	}
	
	/**
	 * Get the Flow Counter 
	 * @return the Flow Counter
	 */
	public int getFlowCount() {
		return this.flowCount;
	}
	
	/**
	 * Get NanoDuration
	 * @return the NanoDuration
	 */
	public int getNanoDuration(){
		return (int) ((System.nanoTime() - this.nanoTime) - (getSecondDuration() * 1000000000));
	}
	
	/***
	 * Get Duration in Seconds
	 * @return duration in Seconds
	 */
	public int getSecondDuration(){
		return (int)((System.nanoTime() - this.nanoTime) / 1000000000);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((actionList == null) ? 0 : actionList.hashCode());
		result = prime * result + (int) (byteCount ^ (byteCount >>> 32));
		result = prime * result + (int) (cookie ^ (cookie >>> 32));
		result = prime * result + flowCount;
		result = prime * result + hardTimeOut;
		result = prime * result + idleTimeOut;
		result = prime * result + (int) (nanoTime ^ (nanoTime >>> 32));
		result = prime * result + (notifyOnDelete ? 1231 : 1237);
		result = prime * result + (int) (packetCount ^ (packetCount >>> 32));
		result = prime * result + priority;
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OFFlowTableEntry other = (OFFlowTableEntry) obj;
		if (actionList == null) {
			if (other.actionList != null)
				return false;
		} else if (!actionList.equals(other.actionList))
			return false;
		if (byteCount != other.byteCount)
			return false;
		if (cookie != other.cookie)
			return false;
		if (flowCount != other.flowCount)
			return false;
		if (hardTimeOut != other.hardTimeOut)
			return false;
		if (idleTimeOut != other.idleTimeOut)
			return false;
		if (nanoTime != other.nanoTime)
			return false;
		if (notifyOnDelete != other.notifyOnDelete)
			return false;
		if (packetCount != other.packetCount)
			return false;
		if (priority != other.priority)
			return false;
		return true;
	}
	
	/**
	 * String Builder
	 */
	public String toString(){
//		logger.info("OFFlowTableEntry toString()");
		String output = "OFTableEntry: [";
		output += "Actions=" + this.actionList.toString() + ", ";
		output += "Priority=" + this.priority + ", ";
		output += "cookie=" + this.cookie + ", ";
		output += "hardTimeout=" + this.hardTimeOut + ", ";
		output += "idleTimeout=" + this.idleTimeOut + ", ";
		output += "notifyOnDelete=" + this.notifyOnDelete + ", ";
		output += "nanoTime=" + this.nanoTime + ", ";
		output += "byteCount=" + this.byteCount +", ";
		output += "packetCount=" + this.packetCount + ", ";
		output += "flowCount=" + this.flowCount + "]";
		return output;
	}

} 
