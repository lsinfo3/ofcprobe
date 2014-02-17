/**
 * 
 */
package ofcprobe.vswitch.statistics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import ofcprobe.util.Util;
import ofcprobe.vswitch.main.config.Config;
import org.openflow.protocol.OFMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The TimeStampLogger
 * Loggs SystenTimeStamps of each OFPAcketOut and OFPacketIn -> Can be Used for IAT of two OF_PACKET_INs
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public class TimeStampLogger implements IStatistics {
	
	/**
	 * The Logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(TimeStampLogger.class);
	/**
	 * Is session Running
	 */
	private boolean sessionRunning;
	/**
	 * the Dpid
	 */
	private String dpid;
	/**
	 * The OutputFile
	 */
	private String file;
	/**
	 * MilliSecondsPerSecond
	 */
	private static int milliSecondsPerSecond = 1000;
	/**
	 * Incoming TimeStamps
	 */
	private List<Long> incomingStamps;
	/**
	 * List of MeanIncomingTimeStamps per Seconds
	 */
	private List<Double> incomingMeanIATperIntervall;
	/**
	 * Temp List of Incoming TimeStamps for mean List
	 */
	private List<Long> incomingTemp;
	/**
	 * Time of Next Reset for incoming Means
	 */
	private Date nextIncomingReset;
	/**
	 * List of Outgoiung TimeStamps
	 */
	private List<Long> outgoingStamps;
	/**
	 * List of MeanOutogingTimeStamps per Seocnds
	 */
	private List<Double> outgoingMeanIATperIntervall;
	/**
	 * Temp List of Outoging TimeStamps for MeanList
	 */
	private List<Long> outgoingTemp;
	/**
	 * Time of Next Reset for outgoingMeans
	 */
	private Date nextOutgoingReset;
	private Date startDate;

	/**
	 * The Constructor
	 * @param config the Config
	 */
	public TimeStampLogger(Config config){

		this.sessionRunning = false;
		NumberFormat formatter = new DecimalFormat("#000");
		this.dpid = formatter.format(config.getSwitchConfig().getDpid());
		this.startDate = new Date();
		
		/**
		 * TIMESTAMPS OF INCOMING PACKETS = OF_PACKET_OUT
		 */
		this.incomingStamps = new ArrayList<Long>();
		int simulationTime = ((int) Math.floor(config.getSimTime() / 1000)) + 15; 
		ArrayList<Double> temp1 = new ArrayList<Double>();
		Util.ensureSizeDouble(temp1, simulationTime);
		this.incomingMeanIATperIntervall = temp1;
		this.incomingTemp = new ArrayList<Long>();
		this.nextIncomingReset = new Date(System.currentTimeMillis() + milliSecondsPerSecond);
		
		/**
		 * TIMESTAMPS of OUTGOING PACKETS = OF_PACKET_IN
		 */
		this.outgoingStamps = new ArrayList<Long>();
		ArrayList<Double> temp2 = new ArrayList<Double>();
		Util.ensureSizeDouble(temp2, simulationTime);
		this.outgoingMeanIATperIntervall = temp2;
		this.outgoingTemp = new ArrayList<Long>();
		this.nextOutgoingReset = new Date(System.currentTimeMillis() + milliSecondsPerSecond);
	}

	@Override
	public void setReportFile(String file) {
		this.file = file;
		
	}

	@Override
	public void packetIn(OFMessage in) {
		if (!this.sessionRunning) return;
		
		switch(in.getType()) {
		case PACKET_OUT:
			Date now = new Date();
			if (now.after(this.nextIncomingReset)){
				
				Iterator<Long> iter = this.incomingTemp.iterator();
				double iat_mean = 0.0;
				while (iter.hasNext()) {
					iat_mean += iter.next();
				}
				iat_mean = iat_mean / this.incomingTemp.size();
				if (Double.isNaN(iat_mean)) {
					iat_mean = 0;
				}
				int intervall = Util.getIntervall(this.startDate, now);
				Util.listSizeCheck(this.incomingMeanIATperIntervall, intervall);
				this.incomingMeanIATperIntervall.add(intervall, iat_mean);
				logger.trace("[Switch#{}]: New Incoming IAT Intervall Mean: {}", this.dpid, iat_mean);
				this.incomingTemp.clear();
				
				this.nextIncomingReset = new Date(this.nextIncomingReset.getTime() + milliSecondsPerSecond);
			} 
			long value = lastValueGetter(false, now);
			this.incomingTemp.add(value);
			this.incomingStamps.add(now.getTime());
			logger.trace("[Switch#{}]: New Incoming TimeStamp added: {}", this.dpid, now.getTime());
			break;
		default:
			break;
		
		}
	}

	@Override
	public void packetOut(OFMessage out) {
		if (!this.sessionRunning) return;
		
		switch(out.getType()) {
		
		case PACKET_IN:
			Date now = new Date();
			if (now.after(this.nextOutgoingReset)) {
				Iterator<Long> iter = this.outgoingTemp.iterator();
				double iat_mean = 0.0;
				while (iter.hasNext()) {
					iat_mean += iter.next();
				}
				iat_mean = iat_mean / this.outgoingTemp.size();
				if (Double.isNaN(iat_mean)) {
					iat_mean = 0;
				}
				int intervall = Util.getIntervall(this.startDate, now);
				Util.listSizeCheck(this.outgoingMeanIATperIntervall, intervall);
				this.outgoingMeanIATperIntervall.add(intervall, iat_mean);
				logger.trace("[Switch#{}]: New Outgoing IAT Intervall Mean: {}",this.dpid, iat_mean);
				this.outgoingTemp.clear();
				
				this.nextOutgoingReset = new Date(this.nextOutgoingReset.getTime() + milliSecondsPerSecond);
			} 
			long value = lastValueGetter(true, now);
			this.outgoingTemp.add(value);
			this.outgoingStamps.add(now.getTime());
			logger.trace("[Switch#{}]: New Outgoing TimeStamp added: {}", this.dpid, now.getTime());
			break;
		default:
			break;
		
		}
	}

	@Override
	public void evaluate() {
		if (!this.outgoingTemp.isEmpty()) {
			
			Iterator<Long> iter = this.outgoingTemp.iterator();
			double iat_mean = 0.0;
			while(iter.hasNext()) {
				Long next = iter.next();
				iat_mean += next;
			}
			iat_mean = iat_mean/this.outgoingTemp.size();
			if (Double.isNaN(iat_mean)) {
				iat_mean = 0;
			}
			this.outgoingMeanIATperIntervall.add(iat_mean);
			this.outgoingTemp.clear();
			
		}
		
		if (!this.incomingTemp.isEmpty()) {
			
			Iterator<Long> iter = this.incomingTemp.iterator();
			double iat_mean =  0;
			while(iter.hasNext()) {
				Long next = iter.next();
				iat_mean += next;
			}
			iat_mean = iat_mean/this.incomingTemp.size();
			if (Double.isNaN(iat_mean)) {
				iat_mean = 0;
			}
			this.incomingMeanIATperIntervall.add(iat_mean);
			this.incomingTemp.clear();
		}
		
		double totalIncomingIatMean = 0.0;
		Iterator<Double> incomingIter = this.incomingMeanIATperIntervall.iterator();
		while (incomingIter.hasNext()) {
			totalIncomingIatMean += incomingIter.next();
		}
		totalIncomingIatMean = totalIncomingIatMean / this.incomingMeanIATperIntervall.size();
		if (Double.isNaN(totalIncomingIatMean)) {
			totalIncomingIatMean = 0.0;
		}
		logger.info("[Switch#{}]: Total Incoming IAT Mean: {}", this.dpid, totalIncomingIatMean);
		
		double totalOutgoingIatMean = 0.0;
		Iterator<Double> outgoingIter = this.outgoingMeanIATperIntervall.iterator();
		while (outgoingIter.hasNext()) {
			totalOutgoingIatMean += outgoingIter.next();
		}
		totalOutgoingIatMean = totalOutgoingIatMean / this.outgoingMeanIATperIntervall.size();
		if (Double.isNaN(totalOutgoingIatMean)) {
			totalOutgoingIatMean = 0.0;
		}
		logger.info("[Switch#{}]: Total Outgoing IAT Mean: {}", this.dpid, totalOutgoingIatMean);
		
	}

	@Override
	public void report() {
		writeToFile();
		
	}

	@Override
	public void start() {
		this.sessionRunning = true;
		this.startDate = new Date();
		this.nextIncomingReset = new Date(System.currentTimeMillis() + milliSecondsPerSecond);
		this.nextOutgoingReset = this.nextIncomingReset;
		
	}

	@Override
	public void stop() {
		if (this.sessionRunning) {
			this.sessionRunning = false;
			
			
		}
	}
	
	/**
	 * Write Results to File
	 */
	private void writeToFile() {
		try {
			File filou = new File(this.file);
			if (!filou.getParentFile().exists()) {
				filou.getParentFile().mkdirs();
			}
			PrintWriter out = new PrintWriter(this.file);
			
			if (!this.outgoingMeanIATperIntervall.isEmpty()) {
				Iterator<Double> outIATiter = this.outgoingMeanIATperIntervall.iterator();
				while(outIATiter.hasNext()){
					out.print(outIATiter.next());
					out.print(";");
				}
			} else {
				out.print("0;");
			}
			out.print("\n");
			
			if (!this.incomingMeanIATperIntervall.isEmpty()) {
				Iterator<Double> inIATiter = this.incomingMeanIATperIntervall.iterator();
				while(inIATiter.hasNext()){
					out.print(inIATiter.next());
					out.print(";");
				}
			} else {
				out.print("0;");
			}
			out.print("\n");
			
			if (!this.outgoingStamps.isEmpty()) {
				Iterator<Long> outIter = this.outgoingStamps.iterator();
				while (outIter.hasNext()) {
					out.print(outIter.next());
					out.print(";");
				}
			} else {
				out.print("0;");
			}
			out.print("\n");
			
			if (!this.incomingStamps.isEmpty()) {
				Iterator<Long> inIter = this.incomingStamps.iterator();
				while(inIter.hasNext()) {
					out.print(inIter.next());
					out.print(";");
				}
			} else {
				out.print("0;");
			}
			out.print("\n");
			
			
			out.close();
		} catch (FileNotFoundException e) {
			logger.error("[Switch#{}]: {}", this.dpid, e);
		}
	}

	/**
	 * Get Last Values
	 * @param outgoing flag if Outgoing or Incoming Value is searched
	 * @param now now Date
	 * @return long
	 */
	private long lastValueGetter(boolean outgoing, Date now){
		if (outgoing) {//packetOut()
			if (!this.outgoingStamps.isEmpty()) {
				long value = now.getTime() - this.outgoingStamps.get(this.outgoingStamps.size() - 1);
				return value;
			} else {
				return (long) 0;
			}
		} else { // packetIn()
			if (!this.incomingStamps.isEmpty()) {
				long value = now.getTime() - this.incomingStamps.get(this.incomingStamps.size() - 1);
				return value;
			} else {
				return (long) 0;
			}
		}
	}
}
