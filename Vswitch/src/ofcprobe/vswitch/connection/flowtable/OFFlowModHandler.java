/**
 * 
 */
package ofcprobe.vswitch.connection.flowtable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ofcprobe.vswitch.connection.OFConnection1_zero;
import ofcprobe.vswitch.main.config.Config;

import org.openflow.protocol.OFError;
import org.openflow.protocol.OFFlowMod;
import org.openflow.protocol.OFFlowRemoved;
import org.openflow.protocol.OFMatch;
import org.openflow.protocol.OFFlowRemoved.OFFlowRemovedReason;
import org.openflow.protocol.OFMessage;
import org.openflow.protocol.action.OFAction;
import org.openflow.protocol.action.OFActionOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The OFFlowMod Handler handles all incoming OF_FLOW_MOD messages
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public class OFFlowModHandler {
	/**
	 * Logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(OFConnection1_zero.class);
	/**
	 * The Config
	 */
	private Config config;
	/**
	 * The FlowTable
	 */
	private OFFlowTable flow_table;
//	/**
//	 * The DPID
//	 */
//	private int dpid;
	
	/**
	 * Constructor
	 * @param config the config
	 */
	public OFFlowModHandler(Config config){
//		this.dpid = config.getSwitchConfig().getDpid();
		this.config = config;
		this.flow_table = new OFFlowTable(config);
	}
	
	/**
	 * Handles INcoming FlowMods
	 * @param flow_mod incoming FlowMOds
	 * @return Answer to FlowMods
	 */
	public List<OFMessage> handleOFFlowMod(OFFlowMod flow_mod) {
		List<OFMessage> output = new ArrayList<OFMessage>();
		
		OFMatch match = flow_mod.getMatch(); 
		OFFlowTableEntry entry = new OFFlowTableEntry(flow_mod);
		
		short flags = flow_mod.getFlags();
		boolean sendFlowRemoved = false;
		boolean checkOverlap = false;
		
		switch (flags) {
		case OFFlowMod.OFPFF_SEND_FLOW_REM:
			// Send flow removed message when flow expires or is deleted.
			sendFlowRemoved = true;
			break;
		case OFFlowMod.OFPFF_CHECK_OVERLAP:
			// the switch must check that there	are no conflicting entries with the same priority
			// If there is one, the flow mod fails and an error code is returned.
			checkOverlap = true;
			break;
		case OFFlowMod.OFPFF_EMERG:
			// the switch must consider this flow entry as an emergency entry, and only use it for forwarding when disconnected from the controller.
			// like omg dead http://youtu.be/z6jtpqGPW-8
			return null;
		default:
			logger.debug("No FLOW-MOD Flag: {} from FlOW-MOD: {}", flow_mod.getFlags(), flow_mod.toString());
			break;
		}
		
		if (!areLegalActions(flow_mod.getActions())) {
			OFError wrongActionsError = new OFError();
			wrongActionsError.setXid(flow_mod.getXid());
			wrongActionsError.setErrorType(OFError.OFErrorType.OFPET_BAD_ACTION);
			wrongActionsError.setErrorCode(OFError.OFBadActionCode.OFPBAC_BAD_OUT_PORT);
			wrongActionsError.setOffendingMsg(flow_mod);
			output.add(wrongActionsError);
			return output;
		}
		switch (flow_mod.getCommand()) {
		case OFFlowMod.OFPFC_ADD:
			/* OFPFC_ADD: New flow. */
			if (checkOverlap) {
				if (this.flow_table.checkOverlap(match)) {
					OFError error = new OFError();
					error.setXid(flow_mod.getXid());
					error.setErrorType(OFError.OFErrorType.OFPET_FLOW_MOD_FAILED);
					error.setErrorCode(OFError.OFFlowModFailedCode.OFPFMFC_OVERLAP);
					error.setOffendingMsg(flow_mod);
					output.add(error);
					return output;
				}
			}
			if (!this.flow_table.isFull()) {
				
				this.flow_table.removeMatchingFlows(match, true, sendFlowRemoved);
				this.flow_table.insert(match, entry);
			} else {
				if (this.flow_table.containsMatch(match)) {
					this.flow_table.insert(match, entry);
				} else {
					OFError tableFull= new OFError();
					tableFull.setXid(flow_mod.getXid());
					tableFull.setErrorType(OFError.OFErrorType.OFPET_FLOW_MOD_FAILED);
					tableFull.setErrorCode(OFError.OFFlowModFailedCode.OFPFMFC_ALL_TABLES_FULL);
					tableFull.setOffendingMsg(flow_mod);
					output.add(tableFull);
					return output;
				}
			}
			break;
		case OFFlowMod.OFPFC_MODIFY:
			/* OFPFC_MODIFY: Modify all matching flows. */
			if (checkOverlap) {
				if (this.flow_table.checkOverlap(match)) {
					OFError error = new OFError();
					error.setXid(flow_mod.getXid());
					error.setErrorType(OFError.OFErrorType.OFPET_FLOW_MOD_FAILED);
					error.setErrorCode(OFError.OFFlowModFailedCode.OFPFMFC_OVERLAP);
					error.setOffendingMsg(flow_mod);
					output.add(error);
					return output;
				}
			}
			
			Map<OFMatch, OFFlowTableEntry> matchingFlows = this.flow_table.getMatchingFlows(match, false);
			Map<OFMatch, OFFlowTableEntry> modifiedFlows = new TreeMap<OFMatch, OFFlowTableEntry>();
			
			if (!matchingFlows.isEmpty()) {
				Iterator<Map.Entry<OFMatch, OFFlowTableEntry>> modifyIter = matchingFlows.entrySet().iterator();
				while (modifyIter.hasNext()) {
					Map.Entry<OFMatch, OFFlowTableEntry> currentEntry = modifyIter.next();
					currentEntry.getValue().setActions(flow_mod.getActions());
					currentEntry.getValue().updateCookie(flow_mod.getCookie());
					modifiedFlows.put(currentEntry.getKey(),currentEntry.getValue());
				}
				this.flow_table.modifyMatchingFlows(modifiedFlows);
			} else {
				if (!this.flow_table.isFull()) {
					this.flow_table.insert(match, new OFFlowTableEntry(flow_mod));
				} else {
					OFError tableFull= new OFError();
					tableFull.setXid(flow_mod.getXid());
					tableFull.setErrorType(OFError.OFErrorType.OFPET_FLOW_MOD_FAILED);
					tableFull.setErrorCode(OFError.OFFlowModFailedCode.OFPFMFC_ALL_TABLES_FULL);
					tableFull.setOffendingMsg(flow_mod);
					output.add(tableFull);
					return output;
				}
			}
			break;
		case OFFlowMod.OFPFC_MODIFY_STRICT:
			/* OFPFC_MODIFY_STRICT: Modify entry strictly matching wildcards */
			Map<OFMatch, OFFlowTableEntry> matchingFlowsStrict = this.flow_table.getMatchingFlows(match, true);
			Map<OFMatch, OFFlowTableEntry> modifiedFlowsStrict = new TreeMap<OFMatch, OFFlowTableEntry>();
			
			if (matchingFlowsStrict.isEmpty()) {
				Iterator<Map.Entry<OFMatch, OFFlowTableEntry>> modifyStrictIter = matchingFlowsStrict.entrySet().iterator();
				while (modifyStrictIter.hasNext()) {
					Map.Entry<OFMatch, OFFlowTableEntry> currentEntry = modifyStrictIter.next();
					currentEntry.getValue().setActions(flow_mod.getActions());
					currentEntry.getValue().updateCookie(flow_mod.getCookie());
					modifiedFlowsStrict.put(currentEntry.getKey(),currentEntry.getValue());
				}
				this.flow_table.modifyMatchingFlows(modifiedFlowsStrict);
			} else {
				if (!this.flow_table.isFull()) {
					this.flow_table.insert(match, new OFFlowTableEntry(flow_mod));
				} else {
					OFError tableFull= new OFError();
					tableFull.setXid(flow_mod.getXid());
					tableFull.setErrorType(OFError.OFErrorType.OFPET_FLOW_MOD_FAILED);
					tableFull.setErrorCode(OFError.OFFlowModFailedCode.OFPFMFC_ALL_TABLES_FULL);
					tableFull.setOffendingMsg(flow_mod);
					output.add(tableFull);
					return output;
				}
			}
			
			break;
		case OFFlowMod.OFPFC_DELETE:
			/* OFPFC_DELETE: Delete all matching flows. */
			List<OFMatch> removedMatches = this.flow_table.removeMatchingFlows(match, false, sendFlowRemoved);
				
			if (sendFlowRemoved && !removedMatches.isEmpty()) {
				Iterator<OFMatch> matchIter = removedMatches.iterator();
				while (matchIter.hasNext()) {
					OFFlowRemoved flow_removed = new OFFlowRemoved();
					flow_removed.setReason(OFFlowRemovedReason.OFPRR_DELETE);
					flow_removed.setXid(flow_mod.getXid());
					flow_removed.setCookie(flow_mod.getCookie());
					flow_removed.setMatch(matchIter.next());
					output.add(flow_removed);
				}
				return output;
			}
				
			break;
		case OFFlowMod.OFPFC_DELETE_STRICT:
			/* OFPFC_DELETE_STRICT: Strictly match wildcards and priority. */
			List<OFMatch> removedStrictMatches = this.flow_table.removeMatchingFlows(match, true, sendFlowRemoved);
			
			if (sendFlowRemoved && !removedStrictMatches.isEmpty()) {
				Iterator<OFMatch> matchStrictIter = removedStrictMatches.iterator();
				while (matchStrictIter.hasNext()) {
					OFFlowRemoved flow_removed = new OFFlowRemoved();
					flow_removed.setReason(OFFlowRemovedReason.OFPRR_DELETE);
					flow_removed.setXid(flow_mod.getXid());
					flow_removed.setCookie(flow_mod.getCookie());
					flow_removed.setMatch(matchStrictIter.next());
					output.add(flow_removed);
				}
				return output;
			}
			
			break;
		default:
			logger.warn("Undefined FLOW_MOD Command: {}",flow_mod.getCommand());
			break;
		}
		return output;
	}
	
	/**
	 * Are these OFActions legal meaning are they executable on this switch?
	 * @param actions the Actions
	 * @return legal?
	 */
	private boolean areLegalActions(List<OFAction> actions) {
		Iterator<OFAction> actionIter = actions.iterator(); 
		while (actionIter.hasNext()) {
			if (!isLegalAction(actionIter.next())) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks one OFACtion for legallity
	 * (e.g. is Outport > maxPortNum)
	 * @param action the Action
	 * @return legal?
	 */
	private boolean isLegalAction(OFAction action) {
		switch (action.getType()) {
		case OPAQUE_ENQUEUE:
			break;
		case OUTPUT:
			OFActionOutput actionOutput = (OFActionOutput) action;
			short port = actionOutput.getPort();
			return (this.config.getSwitchConfig().getPortCountperSwitch() >= port);
		case SET_DL_DST:
			break;
		case SET_DL_SRC:
			break;
		case SET_NW_DST:
			break;
		case SET_NW_SRC:
			break;
		case SET_NW_TOS:
			break;
		case SET_TP_DST:
			break;
		case SET_TP_SRC:
			break;
		case SET_VLAN_PCP:
			break;
		case SET_VLAN_VID:
			break;
		case STRIP_VLAN:
			break;
		case VENDOR:
			break;
		default:
			break;
		
		}
		
		return true;
	}

	/**
	 * Get Flow Table
	 * @return the flow Table
	 */
	public OFFlowTable getFlowTable(){
		return this.flow_table;
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
		OFFlowModHandler other = (OFFlowModHandler) obj;
		if (flow_table == null) {
			if (other.flow_table != null)
				return false;
		} else if (!flow_table.equals(other.flow_table))
			return false;
		return true;
	}
}
