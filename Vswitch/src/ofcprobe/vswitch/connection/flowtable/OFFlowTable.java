/**
 * 
 */
package ofcprobe.vswitch.connection.flowtable;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import org.openflow.protocol.OFMatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ofcprobe.util.OFMatsch;
import ofcprobe.util.Util;
import ofcprobe.vswitch.main.config.Config;

/**
 * The FlowTable Representation for a ofSwitch
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 * 
 */
public class OFFlowTable {
	
	/**
	 * logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(OFFlowTable.class);
	/**
	 * The Flow Table
	 */
	private TreeMap<OFMatsch, OFFlowTableEntry> flow_table;
	/**
	 * Max Flow Entries
	 */
	private int maxSize;
	/**
	 * The Dpid
	 */
	private String dpid;

	/**
	 * The Comparator for the FlowTable!
	 * TODO: FIX DIRTY HACK!
	 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
	 * 
	 */
	class flowTableComp implements Comparator<OFMatsch> {

		@Override
		public int compare(OFMatsch o1, OFMatsch o2) {
			if (Util.equalsFlow(o1, o2) || Util.equalsFlow(o2, o1)) {
				return 0;
			}
			if (o1.subsumes(o2)) {
				return 1;
			}
			if (o2.subsumes(o1)) {
				return -1;
			}
			return o1.toString().compareTo(o2.toString());
//			return 1;
		}
		
	}
	
	/**
	 * Constructor
	 * @param config
	 */
	public OFFlowTable(Config config) {
		NumberFormat dpidFormatter = new DecimalFormat("#000");
		this.dpid = dpidFormatter.format(config.getSwitchConfig().getDpid());
		this.maxSize = config.getSwitchConfig().getFlowTableSize();
		
		this.flow_table = new TreeMap<OFMatsch, OFFlowTableEntry>(new flowTableComp());
	}
	
	// Vor = Zuerst
	// Weniger WildCards vor mehjr WildCards
	// GleichvieleWildCards --> Unterscheidung durch Priorit�ten
	// OFMatch1 subsumes(match2) -> True -> match1 allgemeiner match2 --> match2
	// braucht nicht geaddet werden
	/**
	 * Insert Flow
	 * @param match the Match
	 * @param tableEntry the TableEntries
	 */
	public void insert(OFMatch match, OFFlowTableEntry tableEntry) {
//		logger.info("[Switch#{}]: insert({}, {})", this.dpid, match, tableEntry);
		
		OFMatsch matsch = new OFMatsch(match);
//		if (this.flow_table.containsKey(matsch)) {
//			this.flow_table.remove(matsch);
//		}
			
		this.flow_table.put(matsch, tableEntry);
//		logger.trace("[Switch#{}]: Flow-Table: {}", this.flow_table.toString());
//		System.err.println(this.dpid + " has " +this.flow_table.size() + " flows stored");
	}

	/**
	 * Get Matching Flows
	 * @param match Flow to Match
	 * @param strictFlag strict matching? -> See OFSpec v1.0
	 * @return Map of Matching Flows
	 */
	public Map<OFMatch, OFFlowTableEntry> getMatchingFlows(OFMatch match, boolean strictFlag) {
//		logger.trace("[Switch#{}]: getMatchingFlows({}, {})", this.dpid, match, strictFlag);
		Iterator<Map.Entry<OFMatsch, OFFlowTableEntry>> iter = this.flow_table.entrySet().iterator();
		Map<OFMatch, OFFlowTableEntry> output = new HashMap<OFMatch, OFFlowTableEntry>();
		OFMatsch matsch = new OFMatsch(match);
		while (iter.hasNext()) {
			Map.Entry<OFMatsch, OFFlowTableEntry> entry = iter.next();
			OFMatsch entryMatch = entry.getKey();
			if (strictFlag) {
				if (Util.equalsFlow(entryMatch, matsch)) {
					output.put(entryMatch, entry.getValue());
				}
			} else {
				if (entryMatch.subsumes(matsch) || matsch.subsumes(entryMatch) || Util.equalsFlow(entryMatch, matsch)) {
					output.put(entryMatch, entry.getValue());
				}
			}
		}
//		logger.trace("[Switch#{}]: getMatchingFlowsOutput: {}", this.dpid, output.toString());
		return output;
	}

	/**
	 * Modifiy Matching Flows
	 * @param modifiedFlows Map of Flows to modify
	 */
	public void modifyMatchingFlows(Map<OFMatch, OFFlowTableEntry> modifiedFlows) {
//		logger.trace("[Switch#{}]: modifyMatchingFlows({})", this.dpid, modifiedFlows.toString());
		
		Iterator<Map.Entry<OFMatch, OFFlowTableEntry>> matchIter = modifiedFlows.entrySet().iterator();
		while (matchIter.hasNext()) {
			Entry<OFMatch, OFFlowTableEntry> entry = matchIter.next();
			this.flow_table.put((OFMatsch) entry.getKey(), entry.getValue());
		}
//		logger.trace("[Switch#{}]: modifyMatchingFlows-Result: {}", this.dpid, this.flow_table.toString());
	}

	/**
	 * Remove Matching Flows
	 * @param match the match
	 * @param strictFlag strictFlag -> OFSpec v1.0
	 * @param sendFlowRemoved send FlowRemovedMsg?
	 * @return List of Removed Flows if sendFlowRemoved = true
	 */
	public List<OFMatch> removeMatchingFlows(OFMatch match, boolean strictFlag, boolean sendFlowRemoved) {
		sendFlowRemoved = true;
//		logger.trace("[Switch#{}]: removeMatchingFlows({}, {}, {}", this.dpid, match, strictFlag, sendFlowRemoved);
		List<OFMatch> output = new ArrayList<OFMatch>();
		
		// check if match is a empty one -> would mean to clear flowtable
		// but doesnt work like this :(
		// empty does match for everything
//		OFMatch empty = new OFMatch();
//		if (Util.equalsFlow(match, empty)) {
//			System.err.println("what? " + match.toString() + ";" + empty.toString());
//			if (sendFlowRemoved) {
//				output.addAll(this.flow_table.keySet());
//			}
//			// OFMatch is empty, so every FLOW in FLOW_DB has to be deleted
//			this.flow_table.clear();
//		}
		
		Iterator<Map.Entry<OFMatsch, OFFlowTableEntry>> iter = this.flow_table.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<OFMatsch, OFFlowTableEntry> entry = iter.next();
			OFMatch entryMatch = entry.getKey();
			if (strictFlag) {
				if (Util.equalsFlow(entryMatch, match)) {
					if (sendFlowRemoved){
						output.add(entryMatch);
					}
					iter.remove();
				}
			} else {
				if (entryMatch.subsumes(match) || match.subsumes(entryMatch) || Util.equalsFlow(match, entryMatch)) {
					if (sendFlowRemoved){
						output.add(entryMatch);
					}
					iter.remove();
				}
			}
		}
//		logger.info("[Switch#{}]: removeMatchingFlows-Result: {} - Output: {}", this.dpid, this.flow_table.toString(), output.toString());
//		logger.info("[Switch#{}]:  Output: {}", this.dpid,  output.size());
		return output;

	}

	/**
	 * Get specific Flow Table Entry
	 * @param match the flow
	 * @param strictFlag the strictFlag -> OFSpec v1.0
	 * @return the Entry
	 */
	public OFFlowTableEntry getEntry(OFMatch match, boolean strictFlag) {
		return this.flow_table.get(match);
	}

	/**
	 * Remove specific Entry
	 * @param match the Match
	 * @return true if remove action was successfull
	 */
	public boolean removeEntry(OFMatch match) {
		return false;
	}

	/**
	 * Does FlowTable contain Match?
	 * @param match the Match
	 * @return true if Map containsKey(OFMatch)
	 */
	public boolean containsMatch(OFMatch match) {
//		logger.trace("[Switch#{}]: containsMatch({})", this.dpid, match.toString());
		return this.flow_table.containsKey(new OFMatsch(match));
	}

	/**
	 * Clear FlowTable
	 */
	public void clear() {
//		logger.trace("[Switch#{}]: Flow-Table cleared()", this.dpid);
		this.flow_table.clear();
	}
	
	/**
	 * Maximum FlowTable Size
	 * @return the flow table size
	 */
	public int size(){
//		logger.trace("[Switch#{}]: Flow-Table Size: {}", this.dpid, this.flow_table.size());
		return this.flow_table.size();
	}
	
	/**
	 * Is the Flow Table Full?
	 * @return table.size >= maxSize?; Currently always false!
	 */
	public boolean isFull(){
//		logger.trace("[Switch#{}]: Flow-Table isFull()-Output: {}", this.dpid, this.flow_table.size() >= this.maxSize);
//		return this.flow_table.size() >= this.maxSize;
		return false;
	}

	/**
	 * the switch must check that there are no conflicting entries with the same
	 * priority If there is one, the flow mod fails and an error code is
	 * returned.
	 * 
	 * @param match
	 * @return
	 */
	public boolean checkOverlap(OFMatch match) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((flow_table == null) ? 0 : flow_table.hashCode());
		result = prime * result + maxSize;
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
		OFFlowTable other = (OFFlowTable) obj;
		if (flow_table == null) {
			if (other.flow_table != null)
				return false;
		} else if (!flow_table.equals(other.flow_table))
			return false;
		if (maxSize != other.maxSize)
			return false;
		return true;
	}

	/**
	 * Max Size
	 * @return the maxSize
	 */
	public int capacity() {
		return this.maxSize;
	}

}
