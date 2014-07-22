package de.uniwuerzburg.info3.ofcprobe.vswitch.statistics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import de.uniwuerzburg.info3.ofcprobe.util.Util;
import de.uniwuerzburg.info3.ofcprobe.vswitch.main.config.Config;

import org.openflow.protocol.OFMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PacketsPerSecond Stats module
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public class PacketsPerSecond implements IStatistics {

	/**
	 * The Logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PacketsPerSecond.class);
	/**
	 * The Total Counter Packets
	 */
	private int packets;
	/**
	 * The Incoming Packets
	 */
	private int packetsI;
	/**
	 * The Outgoing Packets
	 */
	private int packetsO;
	/**
	 * FlowMods in Intervall
	 */
	private int fmInIntervall;
	/**
	 * List of each Packets per Second
	 */
	private ArrayList<Integer> pps;
	/**
	 * Incoming Packets = OFPACKET_OUT
	 */
	private ArrayList<Integer> ppsI;
	/**
	 * Outgoing Packets = OPFPACKET_IN
	 */
	private ArrayList<Integer> ppsO;
	/**
	 * Incoming FlowMod Packets 
	 */
	private ArrayList<Integer> fmps;
	/**
	 * Output File
	 */
	private String file;
	/**
	 * Bool to handle Running/Stop state
	 */
	private boolean running;
	/**
	 * PacketFormatter
	 */
	private static NumberFormat packetFormatter = new DecimalFormat("#000000");
	/**
	 * IntervallFormatter
	 */
	private static NumberFormat intervalFormatter = new DecimalFormat("#000");

	/**
	 * Counter incoming packet
	 */
	private int packI = 0;
	/**
	 * Counter outgoing Packet
	 */
	private int packO = 0;
	/**
	 * Counter flowMod packets
	 */
	private int fm = 0;
	/**
	 * The Dpid
	 */
	private String dpid;
	/**
	 * The startDate
	 */
	private Date startDate;
	private int lastIntervall;
	private int lastIntervallO;
	private int lastIntervallI;
	private int lastIntervallFM;

	/**
	 * Constructor.
	 */
	public PacketsPerSecond(Config config){
		int simulationTime = ((int) Math.floor(config.getSimTime() / 1000)) + 20; 
		ArrayList<Integer> temp1 = new ArrayList<Integer>();
		Util.ensureSize(temp1, simulationTime);
		this.pps = temp1;
		ArrayList<Integer> temp2 = new ArrayList<Integer>();
		Util.ensureSize(temp2, simulationTime);
		this.ppsI = temp2;
		ArrayList<Integer> temp3 = new ArrayList<Integer>();
		Util.ensureSize(temp3, simulationTime);
		this.ppsO = temp3;
		ArrayList<Integer> temp4 = new ArrayList<Integer>();
		Util.ensureSize(temp4, simulationTime);
		this.fmps = temp4;

		this.packets = 0;
		this.packetsI = 0;
		this.packetsO = 0;
		this.fmInIntervall = 0;
		this.running = false;
		this.dpid = config.getSwitchConfig().getDPIDString();
		this.startDate = new Date();

		this.lastIntervall = 0;
		this.lastIntervallO = 0;
		this.lastIntervallI = 0;
		this.lastIntervallFM = 0;
	}


	/**
	 * Processes next Packet
	 */
	private void nextPacket(Date now){

		int intervall = Util.getIntervall(this.startDate, now);

		// Check for new Intervall
		if (intervall > this.lastIntervall) {
			Util.listSizeCheck(this.pps, intervall);
			this.pps.add(intervall - 1, this.pps.get(intervall -1) + this.packets);
			logger.info("[Switch#{}]: {} Packets in Interval#{}", this.dpid, packetFormatter.format(this.packets), intervalFormatter.format(intervall ));
			this.packets = 0;
			this.lastIntervall = intervall;
		}


		// increase number of packets in this interval
		this.packets++;
	}

	private void flowModIn() {
		this.fm++;
		Date now = new Date();
//		nextPacket(now);

		int intervall = Util.getIntervall(this.startDate, now);
		if (intervall > this.lastIntervallFM) {
			Util.listSizeCheck(this.fmps, intervall);
			this.fmps.add(intervall - 1, this.fmps.get(intervall -1) + this.fmInIntervall);
			this.fmInIntervall=0;
			this.lastIntervallFM = intervall;
		}
		this.fmInIntervall++;
	}


	/**
	 * PacketInProcessor
	 */
	private void packIn(){
		
			
				this.packI++;
				Date now = new Date();
				nextPacket(now);

				int intervall = Util.getIntervall(this.startDate, now);

				// Check for new Intervall
				if (intervall > this.lastIntervallI) {
					Util.listSizeCheck(this.ppsI, intervall);
					this.ppsI.add(intervall - 1, this.ppsI.get(intervall -1) + this.packetsI);
					logger.info("[Switch#{}]: {} incoming Packets in Interval#{}", this.dpid, packetFormatter.format(this.packetsI), intervalFormatter.format(intervall));
					this.packetsI = 0;
					this.lastIntervallI = intervall;
				}


				// increase number of packets in this interval
				this.packetsI++;			
		
	}

	/**
	 * PacketOUtProcessor
	 */
	private void packOut(){
		this.packO++;
		Date now = new Date();
		nextPacket(now);

		int intervall = Util.getIntervall(this.startDate, now);

		// Check for new Intervall
		if (intervall > this.lastIntervallO) {
			Util.listSizeCheck(this.ppsI, intervall);
			this.ppsO.add(intervall - 1, this.ppsO.get(intervall -1) + this.packetsO);
			logger.info("[Switch#{}]: {} outgoing Packets in Interval#{}", this.dpid, packetFormatter.format(this.packetsO), intervalFormatter.format(intervall));
			this.packetsO = 0;
			this.lastIntervallO = intervall;
		}


		// increase number of packets in this interval
		this.packetsO++;
	}

	/**
	 * Process incoming Packets of this Switch
	 * Only interested in OFPacketOut (Response from Controller to OFPacketIn)
	 */
	public void packetIn(OFMessage in) {
		
			if (!this.running) {
				return;
			}
			switch(in.getType()){
			case BARRIER_REPLY:
				break;
			case BARRIER_REQUEST:
				break;
			case ECHO_REPLY:
				break;
			case ECHO_REQUEST:
				break;
			case ERROR:
				break;
			case FEATURES_REPLY:
				break;
			case FEATURES_REQUEST:
				break;
			case FLOW_MOD:
				flowModIn();
				break;
			case FLOW_REMOVED:
				break;
			case GET_CONFIG_REPLY:
				break;
			case GET_CONFIG_REQUEST:
				break;
			case HELLO:
				break;
			case PACKET_IN:
				break;
			case PACKET_OUT:
				packIn();
				break;
			case PORT_MOD:
				break;
			case PORT_STATUS:
				break;
			case QUEUE_CONFIG_REPLY:
				break;
			case QUEUE_CONFIG_REQUEST:
				break;
			case SET_CONFIG:
				break;
			case STATS_REPLY:
				break;
			case STATS_REQUEST:
				break;
			case VENDOR:
				break;
			default:
				break;

			}
		
	}

	/**
	 * Process outgoing packets of this switch
	 * Only interested in OFPacketIn (Generated by TrafficGen)
	 */
	public void packetOut(OFMessage out) {
		if (!this.running) {
			return;
		}
		switch(out.getType()){
		case BARRIER_REPLY:
			break;
		case BARRIER_REQUEST:
			break;
		case ECHO_REPLY:
			break;
		case ECHO_REQUEST:
			break;
		case ERROR:
			break;
		case FEATURES_REPLY:
			break;
		case FEATURES_REQUEST:
			break;
		case FLOW_MOD:
			break;
		case FLOW_REMOVED:
			break;
		case GET_CONFIG_REPLY:
			break;
		case GET_CONFIG_REQUEST:
			break;
		case HELLO:
			break;
		case PACKET_IN:
			packOut();
			break;
		case PACKET_OUT:
			break;
		case PORT_MOD:
			break;
		case PORT_STATUS:
			break;
		case QUEUE_CONFIG_REPLY:
			break;
		case QUEUE_CONFIG_REQUEST:
			break;
		case SET_CONFIG:
			break;
		case STATS_REPLY:
			break;
		case STATS_REQUEST:
			break;
		case VENDOR:
			break;
		default:
			break;

		}

	}


	/**
	 * Ends this Session.
	 */
	public void report() {
		
			this.running = false;
			cleanupList(pps);
			cleanupList(ppsI);
			cleanupList(ppsO);
			cleanupList(fmps);
			double ppsMean = meanGetter(this.pps);
			logger.info("[Switch#{}]: Packets/Second Mean in this Session: {}", this.dpid, ppsMean);
			logger.info("[Switch#{}]: packO: {} - packI: {}", this.dpid, this.packO, this.packI);
			logger.info("[Switch#{}]: FlowMods: {}",this.dpid, this.fm);
			writeToFile(ppsMean);
		
	}

	private void cleanupList(List<Integer> list) {
		ListIterator<Integer> it = list.listIterator(list.size());
		while (it.hasPrevious()) {
			if (it.previous()==0) {
				it.remove();

			} else break;
		}
	}

	private double meanGetter(List<Integer> list){
		double mean = 0;
		for (Integer uni : list ){
			mean +=uni;
		}
		mean = mean/list.size();
		if (Double.isNaN(mean)) {
			mean = 0.0;
		}
		return mean;
	}


	private void writeToFile(double ppsMean ) {
		try {
			File filou = new File(this.file);
			if (!filou.getParentFile().exists()) {
				filou.getParentFile().mkdirs();
			}
			PrintWriter out = new PrintWriter(this.file);

			if (!this.pps.isEmpty()) {
				for (long pps : this.pps){
					out.print(pps +";");
				}
			} else {
				out.print("0;");
			}
			out.print("\n");

			out.print(ppsMean + ";" + meanGetter(this.ppsO) + ";" + meanGetter(this.ppsI));

			out.print("\n");

			if (!this.ppsO.isEmpty()) {
				for (long ppsO : this.ppsO){
					out.print(ppsO +";");
				} 
			} else {
				out.print("0;");
			}

			out.print("\n");

			if (!this.ppsI.isEmpty()) {
				for (long ppsI : this.ppsI){
					out.print(ppsI +";");
				}
			} else {
				out.print("0;");
			}


			out.close();
		} catch (FileNotFoundException e) {
			logger.debug("[Switch#{}]: {}", this.dpid, e);
		}
	}

	@Override
	public void setReportFile(String file) {
		this.file = file;

	}




	@Override
	public void evaluate() {
		// Auto-generated method stub

	}

	@Override
	public void start() {

		this.startDate = new Date();


		this.running = true;

	}

	@Override
	public void stop() {
		//		this.running = false;

	}

}
