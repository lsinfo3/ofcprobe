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
package de.uniwuerzburg.info3.ofcprobe.vswitch.connection;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import de.uniwuerzburg.info3.ofcprobe.util.AddressPositions;
import de.uniwuerzburg.info3.ofcprobe.util.Util;
import de.uniwuerzburg.info3.ofcprobe.vswitch.connection.buffer.BufferID;
import de.uniwuerzburg.info3.ofcprobe.vswitch.connection.buffer.SwitchBufferBitSet;
import de.uniwuerzburg.info3.ofcprobe.vswitch.connection.flowtable.OFFlowModHandler;
import de.uniwuerzburg.info3.ofcprobe.vswitch.main.config.Config;
import de.uniwuerzburg.info3.ofcprobe.vswitch.main.config.Topology;
import de.uniwuerzburg.info3.ofcprobe.vswitch.runner.OFSwitchRunner;
import de.uniwuerzburg.info3.ofcprobe.vswitch.statistics.ControllerCPUnRAMMonitor;
import de.uniwuerzburg.info3.ofcprobe.vswitch.statistics.IStatistics;
import de.uniwuerzburg.info3.ofcprobe.vswitch.statistics.PacketsPerSecond;
import de.uniwuerzburg.info3.ofcprobe.vswitch.statistics.RoundTripTime;
import de.uniwuerzburg.info3.ofcprobe.vswitch.statistics.TimeStampLogger;
import de.uniwuerzburg.info3.ofcprobe.vswitch.statistics.special.QueueLengthMonitor;
import de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.arping.Device;

import org.openflow.io.OFMessageAsyncStream;
import org.openflow.protocol.OFBarrierReply;
import org.openflow.protocol.OFEchoReply;
import org.openflow.protocol.OFFeaturesReply;
import org.openflow.protocol.OFFlowMod;
import org.openflow.protocol.OFGetConfigReply;
import org.openflow.protocol.OFHello;
import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFPacketIn;
import org.openflow.protocol.OFPacketOut;
import org.openflow.protocol.OFPhysicalPort;
import org.openflow.protocol.OFPort;
import org.openflow.protocol.OFSetConfig;
import org.openflow.protocol.OFStatisticsRequest;
import org.openflow.protocol.OFType;
import org.openflow.protocol.OFVendor;
import org.openflow.protocol.OFPacketIn.OFPacketInReason;
import org.openflow.protocol.action.OFAction;
import org.openflow.protocol.action.OFActionOutput;
import org.openflow.protocol.action.OFActionType;
import org.openflow.protocol.factory.BasicFactory;
import org.openflow.util.HexString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;
import java.nio.BufferUnderflowException;

/**
 * The Connection Handler aka ofSwitch for OpenFlow Version 1.0
 *
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public class OFConnection1_zero implements IOFConnection {

    private Set<BufferID> savedBuffIds;

    private Map<BufferID, byte[]> savedPayloads;

    /**
     * Last Used TransactionID for Packet_INs
     */
    private int lastXid = 1;
    /**
     * The OpenFlow Version spoken by this ofSwitch Implementation
     */
    private static byte OFP_VERSION = 0x01;
    /**
     * List of connected Statistic Modules
     */
    private List<IStatistics> statistics;
    /**
     * The QueueLengthMonitor
     */
    private QueueLengthMonitor queueLengthMonitor;

    /**
     * DataPathId
     */
    private long dpid;
    /**
     * Debugger
     */
    private static final Logger logger = LoggerFactory.getLogger(OFConnection1_zero.class);
    /**
     * Amount of Ports
     */
    private int ports;
    /**
     * BufferSize
     */
    private int bufferSize;
    /**
     * The Socket on which this ofSwitch communicates with the Controller.
     */
    private SocketChannel socket;
    /**
     * Generated OFConfig-reply
     */
    private OFGetConfigReply config_reply;
    /**
     * Generated OFFeaturesReply
     */
    private OFFeaturesReply feat_reply;
    /**
     * Generated Config
     */
    private Config config;
    /**
     * The OFMessageAsynStream used to write/read OFMessages of the Channel
     */
    private OFMessageAsyncStream ofStream;
    /**
     * The Buffer used by this ofSwitch
     */
    private SwitchBufferBitSet buffer;
    /**
     * The SessionCount, used for Foldername
     */
    private int session;
    /**
     * If False -> OFMessages to send are only transmitted to Statistic methods
     */
    private boolean sendFlag;

    /**
     * The Thread to this ofSwitch
     */
    private OFSwitchRunner runner;
    /**
     * Date to save last PacketIN time
     */
    private Date lastPacketIn;
    /**
     * Boolean to save info that this benchingsessions has ended.
     */
    private boolean sessionStopped;
    /**
     * Flag to enable/disable batchSending of queued PacketIn msgs
     */
    private boolean batchSending;
    /**
     * Flag for crashhandling(e.g. connection closed etc)
     */
    private boolean crashed;

    /**
     * The PayloadQueue, only the TrafficGen writes here
     */
    private List<QueuedPacket> incomingListTrafficGenerator;
    /**
     * The PayloadQueue of this Thread, only this Thread writes here.
     */
    private List<QueuedPacket> incomingListSwitchRunner;

    /**
     * Bool to check if this ofSwitch already had OFComm
     */
    private boolean hadOFComm;
    /**
     * How long to connection Delayed establishment delayed from initialzisation
     */
    private long conDelay;
    /**
     * How long to Delay start from 'normal' Switches in Millis
     */
    private long startDelay;
    /**
     * How long to Delay stop from 'normal' Switches in Millis
     */
    private long stopDelay;
    /**
     * The Topology
     */
    private Topology topology;
    private int iat;
    private int fillThreshold;

    private OFFlowModHandler flowmod_handler;

    private OFStatsHandler stats_handler;

    private String dpidString;

    private String pcapFileName;

    private String iatDistri;

    private double iatDistriPara1;

    private double iatDistriPara2;

    /**
     * Constructor.
     *
     * @param starter the corresponding Thread/
     * @param config the Configuration
     */
    public OFConnection1_zero(OFSwitchRunner starter, Config config) {
        // initialize Class Variables
        this.sessionStopped = false;
        this.statistics = new ArrayList<IStatistics>();
        this.incomingListTrafficGenerator = new ArrayList<>();
        this.incomingListSwitchRunner = new ArrayList<>();
        this.crashed = false;
        this.hadOFComm = false;
        this.conDelay = 0;
        this.startDelay = 0;
        this.stopDelay = 0;
        this.topology = null;
        this.savedBuffIds = new HashSet<>();
        this.savedPayloads = new HashMap<>();

        this.flowmod_handler = new OFFlowModHandler(config);
        this.stats_handler = new OFStatsHandler(config, this.flowmod_handler.getFlowTable());

        // Load Values from Config
        this.runner = starter;
        this.config = config;
        this.sendFlag = this.config.getSwitchConfig().getSendFlag();
        this.batchSending = this.config.getSwitchConfig().getBatchSending();
//		this.controller = this.config.getSwitchConfig().getContAddress();
        this.dpid = this.config.getSwitchConfig().getDpid();
        this.dpidString = this.config.getSwitchConfig().getDPIDString();
        this.ports = this.config.getSwitchConfig().getPortCountperSwitch();
        this.bufferSize = this.config.getSwitchConfig().getBuffersPerSwitch();
        this.buffer = new SwitchBufferBitSet(this.bufferSize);
        this.session = this.config.getSwitchConfig().getSession();
        this.iat = config.getTrafficGenConfig().getIAT();
        this.fillThreshold = config.getTrafficGenConfig().getFillThreshold();
        this.pcapFileName = "notSet!";
        this.iatDistri = "none!";
        this.queueLengthMonitor = null;
        if (config.hasTopology()) {
            this.topology = config.getTopology();
        }

        if (this.config.checkForIndividualSettings()) {
            loadIndividualSettings();
        }

        // Initialize Statistic Modules
        initializeStatistics();
    }

    private void loadIndividualSettings() {
        Properties props = new Properties();
        String configFile = config.getIndividualSwitchSettingsFileName();

        try {
            props.load(new BufferedInputStream(new FileInputStream(configFile)));
            this.conDelay = Long.parseLong(props.getProperty(dpidString + ".conDelay", "0"));
            this.startDelay = Long.parseLong(props.getProperty(dpidString + ".start", "0"));
            this.stopDelay = Long.parseLong(props.getProperty(dpidString + ".stop", "0"));
            if (this.conDelay > 0) {
                logger.info("Switch #{} successfully loaded Individual Switch ConDelay: {}", dpidString, this.conDelay);
            }
            if (this.startDelay > 0) {
                logger.info("Switch #{} successfully loaded Individual Switch StartDelay: {}", dpidString, this.startDelay);
            }
            if (this.stopDelay != 0) {
                logger.info("Switch #{} successfully loaded Individual Switch StopDelay: {}", dpidString, this.stopDelay);
            }
            this.iat = Integer.parseInt(props.getProperty(dpidString + ".iat", Integer.toString(config.getTrafficGenConfig().getIAT())));
            if (this.iat != this.config.getTrafficGenConfig().getIAT()) {
                logger.info("Switch #{} successfully loaded Individual Switch IAT: {}", dpidString, this.iat);
            }
            this.fillThreshold = Integer.parseInt(props.getProperty(dpidString + ".fillthreshold", Integer.toString(this.fillThreshold)));
            if (this.fillThreshold != this.config.getTrafficGenConfig().getFillThreshold()) {
                logger.info("Switch #{} successfully loaded Individual Switch Fillthreshold: {}", dpidString, this.fillThreshold);
            }
            if (this.fillThreshold != this.config.getTrafficGenConfig().getFillThreshold() || this.iat != this.config.getTrafficGenConfig().getIAT()) {
                double targetPacketCount = (1000 / this.iat) * this.fillThreshold * this.config.getTrafficGenConfig().getCountPerEvent();
                logger.info("Switch #{} Target Packets Generated per Second: {}", dpidString, targetPacketCount);
            }
            this.pcapFileName = props.getProperty(dpidString + ".pcapFile", "notSet!");
            this.iatDistri = props.getProperty(dpidString + ".iatDistribution", "none");
            this.iatDistriPara1 = Double.parseDouble(props.getProperty(dpidString + ".iatDistributionParamter1", "100.0"));
            this.iatDistriPara2 = Double.parseDouble(props.getProperty(dpidString + ".iatDistributionParamter2", "1.0"));

        } catch (FileNotFoundException e) {
            logger.error("Could not find Switch-Config File!");
            System.exit(-1);
        } catch (NumberFormatException e) {
            logger.error("Wrong Switch-ConfigFile Format!");
            System.exit(-1);
        } catch (IOException e) {
            // Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Initializes the Statistic Modules, will be dynamically in the Future
     */
    private void initializeStatistics() {
        List<String> stats = this.config.getStatConfig().getStatModules();

        NumberFormat formatter = new DecimalFormat("#000");
        String dpidString = formatter.format(this.dpid);

        Iterator<String> statIter = stats.iterator();
        boolean snmpStarted = false;
        while (statIter.hasNext()) {
            String stat = statIter.next();
            if (stat.equals("PPS")) {
                // PPS Module
                IStatistics pps = new PacketsPerSecond(this.config);
                pps.setReportFile("./statistics/" + formatter.format(this.session) + "/pps." + dpidString + ".txt");
                this.statistics.add(pps);
            }

            if (stat.equals("RTT")) {
                // RTT Module
                IStatistics rtt = new RoundTripTime(this.config);
                rtt.setReportFile("./statistics/" + formatter.format(this.session) + "/rtt." + dpidString + ".txt");
                this.statistics.add(rtt);
            }

            if (stat.equals("CPU") || stat.equals("RAM")) {
                if (!snmpStarted) {
                    snmpStarted = true;
                    // Controller CPU Monitor
                    IStatistics cpuRam = new ControllerCPUnRAMMonitor(this.config);
                    cpuRam.setReportFile("./statistics/" + formatter.format(this.session) + "/cpu." + dpidString + ".txt");
                    cpuRam.setReportFile("./statistics/" + formatter.format(this.session) + "/ram." + dpidString + ".txt");
                    this.statistics.add(cpuRam);
                }
            }

            if (stat.equals("TSL")) {
                // Time Stamp Stuff for MJ
                IStatistics timeStampLogger = new TimeStampLogger(this.config);
                timeStampLogger.setReportFile("./statistics/" + formatter.format(this.session) + "/tsl." + dpidString + ".txt");
                this.statistics.add(timeStampLogger);
            }

            if (stat.equals("QLM")) {
                // QueueLength Monitor Modules
                this.queueLengthMonitor = new QueueLengthMonitor(this.config);
                this.queueLengthMonitor.setReportFile("./statistics/" + formatter.format(this.session) + "/qlm." + dpidString + ".txt");
            }
        }
    }

    /**
     * Sets features for the FeatureRequest/Reply.
     *
     * @param portCount Count of Ports.
     */
    private void setFeatures(int portCount, int bufferCount) {
        this.feat_reply = new OFFeaturesReply();
        this.feat_reply.setDatapathId(this.dpid);
        this.feat_reply.setPorts(getPorts(portCount));
        this.feat_reply.setCapabilities(199); // Following Values have
        this.feat_reply.setActions(4095);     // been taken
        this.feat_reply.setBuffers(bufferCount);      // from
        this.feat_reply.setTables((byte) -1);  // Open vSwitch
    }

    /**
     * Builds some Ports for our Switch.
     *
     * @param portCount Count of Ports
     * @return List of these Ports.
     */
    private List<OFPhysicalPort> getPorts(int portCount) {
        List<OFPhysicalPort> portList = new ArrayList<>();
        long lastMAC = 0x000000000001L;
        int MACAddress_WIDTH = 6;
        for (int i = 0; i < portCount; i++) {
            OFPhysicalPort port = new OFPhysicalPort();
            byte[] hwAddress = new byte[6];
            Util.insertLong(hwAddress, lastMAC++, 0, MACAddress_WIDTH);
            port.setHardwareAddress(hwAddress);
            port.setName("eth" + i);
            port.setPortNumber((short) (i + 1));
            portList.add(port);
        }
        return portList;
    }

    /**
     * Safe config set by Controller.
     *
     * @param config OFSetConfig
     */
    private void setConfig(OFSetConfig config) {
        this.config_reply = new OFGetConfigReply();
        this.config_reply.setFlags(config.getFlags());
        this.config_reply.setMissSendLength(config.getMissSendLength());
    }

    /**
     * Build a VENDOR OF Message.
     *
     * @return OFVendor reply
     */
    private OFVendor buildVendorReply() {
        OFVendor vendor = new OFVendor();
        vendor.setVendor(42);
        String data = "OFCProbe vSwitch";
        vendor.setData(data.getBytes());

        return vendor;
    }

    /**
     * Checks if provided Port is local Port
     *
     * @param port
     * @return true --> port is local
     */
    private OFPhysicalPort getLocalPort(OFPacketOut po) {
        // Action port check
        short port = (short) -42;
        if (po.getActions().size() == 1) {
            OFAction action = po.getActions().get(0);
            if (action.getType().equals(OFActionType.OUTPUT)) {
                OFActionOutput outputAction = (OFActionOutput) action;
                port = outputAction.getPort();
            }
        }

        List<OFPhysicalPort> phyPorts = this.feat_reply.getPorts();
        for (OFPhysicalPort phyPort : phyPorts) {
            if (phyPort.getPortNumber() == port) {
                return phyPort;
            }
        }
        return null;
    }

    @Override
    public void send(OFMessage out) {
        packetOut(out);
        out.computeLength();
        if (this.sendFlag && !this.crashed) {
            try {
                if (!out.getType().equals(OFType.PACKET_IN)) //				if (this.dpidString.equals("001"))
                {
                    logger.trace("[Switch#{}]: Outgoing: {}", this.dpidString, out.toString());
                }
                this.ofStream.write(out);
                while (this.ofStream.needsFlush()) {
                    this.ofStream.flush();
                }
            } catch (IOException e) {
                this.runner.endSession(); // Tryin to get at least some data out of this
                this.runner.evaluate();
                this.runner.report();

                logger.error("[Switch#{}]: " + Throwables.getStackTraceAsString(e), this.dpidString);
                logger.error("[Switch#{}]: Connection has been closed by Remotehost - Has Controller Crashed?", this.dpidString);
                logger.error("[Switch#{}]: Saving Data and Exiting ...", this.dpidString);
                this.crashed = true;
            } catch (NotYetConnectedException e) {
                logger.warn("[Switch#{}]: Connection has not yet been connected! Will retry in 5sec", this.dpidString);
                try {
                    Thread.sleep(5000);
                    send(out);
                } catch (InterruptedException ie) {
                    logger.error("[Switch#{}]: Couldn't sleep... Going suicidal now!", this.dpidString);
                    System.exit(1);
                }
            }
        }

    }

    @Override
    public void receive() {
        if (this.ofStream != null && !this.crashed) {
            try {
                List<OFMessage> ofmessages = this.ofStream.read();
                if (ofmessages != null) {
                    if (!ofmessages.isEmpty()) {
                        Iterator<OFMessage> ofiter = ofmessages.iterator();
                        while (ofiter.hasNext()) {
                            receive(ofiter.next());
                        }
                    }
                }
            } catch (IOException e) {
                this.runner.endSession(); // Tryin to get at least some data out of this
                this.runner.evaluate();
                this.runner.report();
                this.crashed = true;
                // If One of the Hosts(Controller or OFCProbe) is busy, Handshake is likely to fail and e.g. Floodlight will drop the Connection and so we land here
                logger.debug("[Switch#{}]: " + Throwables.getStackTraceAsString(e), this.dpidString);
                logger.error("[Switch#{}]: Connection has been closed by Remotehost - Has Controller Crashed?", this.dpidString);
                logger.error("[Switch#{}]: Exiting ...", this.dpidString);
//			System.exit(1);
            } catch (BufferUnderflowException b) {
                logger.debug("[Switch#{}]: " + Throwables.getStackTraceAsString(b), this.dpidString);
                logger.error("[Switch#{}]: BufferUnderflowException! Maybe Controller tried other OpenFlowProtocl Version?", this.dpidString);
            }
        }
    }

    /**
     * Process incoming OFMessage and answer to it, if needed
     *
     * @param incoming OFMessage incoming
     */
    private void receive(OFMessage incoming) {
        packetIn(incoming);

        logger.trace("[Switch#{}]: Incoming Packet: {}", this.dpidString, incoming.toString());
        logger.trace("[Switch#{}]: Flow Table size: {}", this.dpidString, this.flowmod_handler.getFlowTable().size());
        // Create Reply depending on incoming Message
        // Don't forget to setXid = incoming.getXid()
        switch (incoming.getType()) {
            case BARRIER_REPLY:
                break;
            case BARRIER_REQUEST:
                OFBarrierReply barrier_reply = new OFBarrierReply();
                barrier_reply.setXid(incoming.getXid());
                send(barrier_reply);
                break;
            case ECHO_REPLY:
                break;
            case ECHO_REQUEST:
                OFEchoReply echo_reply = new OFEchoReply();
                echo_reply.setXid(incoming.getXid());
                send(echo_reply);
                break;
            case ERROR:
                break;
            case FEATURES_REPLY:
                break;
            case FEATURES_REQUEST:
                setFeatures(this.ports, this.bufferSize);
                this.feat_reply.setXid(incoming.getXid());
                send(this.feat_reply);
                break;
            case FLOW_MOD:

                OFFlowMod flow_mod = (OFFlowMod) incoming;
                List<OFMessage> replies = this.flowmod_handler.handleOFFlowMod(flow_mod);
                for (OFMessage msg : replies) {
                    send(msg);
                }
                break;
            case FLOW_REMOVED:
                break;
            case GET_CONFIG_REPLY:
                break;
            case GET_CONFIG_REQUEST:
                OFGetConfigReply conf_reply = this.config_reply;
                if (conf_reply != null) {
                    conf_reply.setXid(incoming.getXid());
                } else {
                    conf_reply = new OFGetConfigReply();
                    conf_reply.setFlags((short) 0);
                    conf_reply.setMissSendLength((short) -1);
                }

                send(conf_reply);
                break;
            case HELLO:
                this.hadOFComm = true;
                OFHello hello = new OFHello();
                hello.setXid(incoming.getXid());
                send(hello);
                break;
            case PACKET_IN:
                break;
            case PACKET_OUT:
                OFPacketOut packet_out = (OFPacketOut) incoming;
                if (this.config.hasTopology() && isLLDP(packet_out)) {
                    short localPort = getLocalPort(packet_out).getPortNumber();

                    long connectedOfSwitch = this.topology.getConnectedOfSwitch(this.dpid, localPort);
                    IOFConnection ofSwitch = this.runner.getMain().getIOFConByDpid(connectedOfSwitch);

                    if (ofSwitch != null) {
                        ofSwitch.queuePacketIn(packet_out.getPacketData(), this.topology.getInPort(connectedOfSwitch, this.dpid), false);
                    }
                }
                BufferID buffID = new BufferID(packet_out.getBufferId());

                if (this.savedBuffIds.contains(buffID)) {
                    handleBuffid(buffID, packet_out);
                }
                this.buffer.freeBuffer(packet_out.getBufferId());
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
                OFSetConfig set_config = (OFSetConfig) incoming;
                setConfig(set_config);
                break;
            case STATS_REPLY:
                break;
            case STATS_REQUEST:
                OFStatisticsRequest stat_req = (OFStatisticsRequest) incoming;
                OFMessage stats_rep = this.stats_handler.buildStatsReply(stat_req);
                send(stats_rep);
                break;
            case VENDOR:
                OFVendor vendor = buildVendorReply();
                vendor.setXid(incoming.getXid());
                send(vendor);
                break;
            default:
                break;

        }
    }

    /**
     * Handles a saved Payload to a saved buffid
     *
     * @param buffId the buffid of the packet
     * @param in_port port#
     */
    private void handleBuffid(BufferID buffId, OFPacketOut packet_out) {

        short in_port = packet_out.getInPort();
        ArrayList<Short> outports = new ArrayList<>();
        for (OFAction action : packet_out.getActions()) {
            if (action.getType().equals(OFActionType.OUTPUT)) {
                outports.add(((OFActionOutput) action).getPort());
            }
        }
        byte[] payload = this.savedPayloads.remove(buffId);
        this.savedBuffIds.remove(buffId);
        if (isArp(payload)) {
            logger.trace("[Switch#{}]: ARP Detected", this.dpidString);
            if (!isArp4me(payload, buffId)) {
                logger.trace("[Switch#{}]: ARP is not 4 me", this.dpidString);
                List<Long> targetSwitches = new ArrayList<>();
                for (short outport : outports) {
                    if (outport == OFPort.OFPP_FLOOD.getValue()) {
                        targetSwitches = this.topology.getConnectedOfSwitches(this.dpid);
                        break;
                    }
                    targetSwitches.add(this.topology.getConnectedOfSwitch(this.dpid, outport));
                }
                Long sourceSwitch = this.topology.getConnectedOfSwitch(this.dpid, in_port);
                targetSwitches.remove(sourceSwitch);
//				logger.info("[Switch#{}]: targetSwitches: {}", this.dpidString, targetSwitches.size());
                if (targetSwitches.size() > 0) {
                    for (long dpid : targetSwitches) {
                        IOFConnection ofSwitch = this.runner.getMain().getIOFConByDpid(dpid);
                        if (ofSwitch != null) {
                            short port = this.topology.getInPort(ofSwitch.getDpid(), this.dpid);
                            logger.trace("[Switch#{}]: Switch#{} has now ARP queued on Port#{}", this.dpidString, ofSwitch.getDpid(), port);
                            ofSwitch.queuePacketIn(payload, port, true);
                        }
                    }
                } else {
                    logger.trace("[Switch#{}]: ARP has no TargetSwitch!", this.dpidString);
                }

            }
        }
        if (isTCPSyn(payload)) {
            logger.trace("[Switch#{}]: TCPSyN Detected", this.dpidString);
            if (!isTCPSyN4me(payload, buffId)) {
                logger.trace("[Switch#{}]: TCPSyN is not 4 me", this.dpidString);
                List<Long> targetSwitches = new ArrayList<>();
                for (short outport : outports) {
                    if (outport == OFPort.OFPP_FLOOD.getValue()) {
                        targetSwitches = this.topology.getConnectedOfSwitches(this.dpid);
                        break;
                    }
                    targetSwitches.add(this.topology.getConnectedOfSwitch(this.dpid, outport));
                }
                Long sourceSwitch = this.topology.getConnectedOfSwitch(this.dpid, in_port);
                targetSwitches.remove(sourceSwitch);
//				logger.info("[Switch#{}]: targetSwitches: {}", this.dpidString, targetSwitches.size());
                if (targetSwitches.size() > 0) {
                    for (long dpid : targetSwitches) {
                        IOFConnection ofSwitch = this.runner.getMain().getIOFConByDpid(dpid);
                        short port = this.topology.getInPort(ofSwitch.getDpid(), this.dpid);
                        if (ofSwitch != null) {
                            logger.trace("[Switch#{}]: Switch#{} has now TCPSyN queued on Port#{}", this.dpidString, ofSwitch.getDpid(), port);
                            ofSwitch.queuePacketIn(payload, port, true);
                        }
                    }
                } else {
                    logger.trace("[Switch#{}]: TCPSyN has no TargetSwitch!", this.dpidString);
                }

            }
        }

    }

    /**
     * Checks if packet is an TCPSyn
     *
     * @param packet the packet
     * @return the bool
     */
    private boolean isTCPSyn(byte[] packet) {
        byte[] type = Util.getBytes(packet, AddressPositions.ETHER_TYPE, 2);
        String typeStr = Util.asString(type);
        if (typeStr.equals("0800")) {
            byte[] protocol = Util.getBytes(packet, AddressPositions.IP_PROTOCOL, 1);
            String protocolStr = Util.asString(protocol);
            if (protocolStr.equals("06")) {
                byte[] flags = Util.getBytes(packet, AddressPositions.TCP_FLAGS, 2);
                String flagsStr = Util.asString(flags);
                if (flagsStr.equals("8002")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if packet is an ARP
     *
     * @param packet the packet
     * @return the bool
     */
    private boolean isArp(byte[] packet) {
        byte[] protocol = Util.getBytes(packet, AddressPositions.ETHER_TYPE, 2);
        String protocolStr = Util.asString(protocol);
        if (protocolStr.equals("0806")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if arp is 4 this switch
     *
     * @param packet the packet
     * @return the bool
     */
    private boolean isArp4me(byte[] packet, BufferID buffId) {
        byte[] dstIP = Util.getBytes(packet, AddressPositions.ARP_IP_DST, 4);
        String dstIPString = Util.fromIPv4Address(Util.toIPv4Address(dstIP));
        logger.trace("[Switch#{}]: ARP DST-IP: {}", this.dpidString, dstIPString);
        Device target = this.config.getTopology().getHostMapping().getDeviceToIp(dstIPString);
        if (target != null) {

            logger.trace("[Switch#{}]: Arp Target: {} " + this.config.getTopology().getHostMapping().getMacToDevice(target), this.dpidString, target.toString());
            if (target.getOfSwitch().equals(this)) {
                if (this.feat_reply.getPortMap().keySet().contains(target.getPort())) {
                    byte[] arpReply = arpReplyBuilder(packet);
                    queuePacketIn(arpReply, target.getPort(), false);
                    logger.trace("[Switch#{}]: ARP Reply queued!", this.dpidString);
                    return true;
                }
            }
        } else {
            logger.warn("[Switch#{}]: isArp4me: Target null", this.dpidString);
        }
        logger.trace("[Switch#{}]: ARP (BuffID: {};TargetDevice: {}) not 4 me: {}", this.dpidString, buffId.getBuffId(), target, dstIPString);
        return false;
    }

    /**
     * Checks if arp is 4 this switch
     *
     * @param packet the packet
     * @return the bool
     */
    private boolean isTCPSyN4me(byte[] packet, BufferID buffId) {
        byte[] dstIP = Util.getBytes(packet, AddressPositions.IP_DST, 4);
        String dstIPString = Util.fromIPv4Address(Util.toIPv4Address(dstIP));
        logger.trace("[Switch#{}]: TCPSyN DST-IP: {}", this.dpidString, dstIPString);
        Device target = this.config.getTopology().getHostMapping().getDeviceToIp(dstIPString);
        if (target != null) {
            logger.trace("[Switch#{}]: TCPSyN Target: {} " + this.config.getTopology().getHostMapping().getMacToDevice(target), this.dpidString, target.toString());
            if (target.getOfSwitch().equals(this)) {
                if (this.feat_reply.getPortMap().keySet().contains(target.getPort())) {
                    byte[] TCPSyNReply = TCPSyNaCKBuilder(packet);
                    queuePacketIn(TCPSyNReply, target.getPort(), false);
                    logger.trace("[Switch#{}]: TCP SYN/ACK queued!", this.dpidString);
                    return true;
                }
            }
        } else {
            logger.warn("[Switch#{}]: isTCPSyN4me: Target null", this.dpidString);
        }
        logger.trace("[Switch#{}]: TCP (BuffID: {};TargetDevice: {}) not 4 me: {}", this.dpidString, buffId.getBuffId(), target, dstIPString);
        return false;
    }

    private byte[] TCPSyNaCKBuilder(byte[] packet) {
        byte[] dstMAC = Util.getBytes(packet, AddressPositions.ETHER_MAC_SRC, 6);
        byte[] dstIP = Util.getBytes(packet, AddressPositions.IP_SRC, 4);
        byte[] srcMAC = Util.getBytes(packet, AddressPositions.ETHER_MAC_DST, 6);
        byte[] srcIP = Util.getBytes(packet, AddressPositions.IP_DST, 4);

        packet = Util.insertByteArray(packet, dstMAC, AddressPositions.ETHER_MAC_DST);
        packet = Util.insertByteArray(packet, srcMAC, AddressPositions.ETHER_MAC_SRC);
        packet = Util.insertByteArray(packet, srcIP, AddressPositions.IP_SRC);
        packet = Util.insertByteArray(packet, dstIP, AddressPositions.IP_DST);

        byte[] synAck = Util.toByteArray("12");
        packet = Util.insertByteArray(packet, synAck, AddressPositions.TCP_FLAGS);

        return packet;
    }

    /**
     * Builds an arp reply for an arp for this switch
     *
     * @param packet the packet
     * @return the arpreply
     */
    private byte[] arpReplyBuilder(byte[] packet) {
        byte[] srcIP = Util.getBytes(packet, AddressPositions.ARP_IP_DST, 4);
        String srcIPstring = Util.fromIPv4Address(Util.toIPv4Address(srcIP));
        String macString = this.topology.getHostMapping().getMacToIp(srcIPstring);
        byte[] srcMac = HexString.fromHexString(macString);
        byte[] dstIP = Util.getBytes(packet, AddressPositions.ARP_IP_SRC, 4);
        byte[] dstMac = Util.getBytes(packet, AddressPositions.ETHER_MAC_SRC, 6);
        byte[] opCode = Util.toByteArray("0002");

        packet = Util.insertByteArray(packet, dstMac, AddressPositions.ETHER_MAC_DST);//eth
        packet = Util.insertByteArray(packet, srcMac, AddressPositions.ETHER_MAC_SRC);

        packet = Util.insertByteArray(packet, srcMac, AddressPositions.ARP_MAC_SRC);//arp
        packet = Util.insertByteArray(packet, srcIP, AddressPositions.ARP_IP_SRC);
        packet = Util.insertByteArray(packet, dstMac, AddressPositions.ARP_MAC_DST);
        packet = Util.insertByteArray(packet, dstIP, AddressPositions.ARP_IP_DST);
        packet = Util.insertByteArray(packet, opCode, AddressPositions.ARP_OPCODE);

        return packet;
    }

    /**
     * Check if OFPacketOut is LLDP Packet
     *
     * @param po incoming OFPacketOut
     * @return true --> packet is LLDP
     */
    private boolean isLLDP(OFPacketOut po) {

        short ofp_none = OFPort.OFPP_NONE.getValue();

        logger.trace("[Switch#{}]: Checking for LLDP! " + ofp_none + " " + po.getInPort(), this.dpidString);
        // PO.InPort Check
        if (po.getInPort() != ofp_none) {
            return false;
        }

        // Check if OFPhysicalPort is local
        OFPhysicalPort localPort = getLocalPort(po);
        if (localPort == null) {
            return false;
        }

        // PO.Actions check
        List<OFAction> lldp_actions = new ArrayList<>();
        lldp_actions.add(new OFActionOutput(localPort.getPortNumber(), (short) 0));

        if (!po.getActions().equals(lldp_actions)) {
            // wrong actionSet or port not from this switch
            return false;
        }

        // PacketData check
        byte[] data = po.getPacketData();

        byte[] srcMac = Util.getBytes(data, AddressPositions.ETHER_MAC_SRC, 6);
        String srcMacString = HexString.toHexString(srcMac);

        byte[] dstMac = Util.getBytes(data, AddressPositions.ETHER_MAC_DST, 6);
        String dstMacString = HexString.toHexString(dstMac);

        byte[] portMac = localPort.getHardwareAddress();
        String portMacString = HexString.toHexString(portMac);

        // DST MAC reserved for LLDP
        String LLDP_STANDARD_DST_MAC_STRING = "01:80:c2:00:00:0e";

        // DST MAC has to be LLDP_MAC
        if (!dstMacString.equals(LLDP_STANDARD_DST_MAC_STRING)) {
            // Wrong destination mac (!= lldp mac)
            return false;
        }

        // SRC MAC has to be MAC of localPort
        if (!srcMacString.equals(portMacString)) {
            // Wrong src mac (!= lldp_port mac)
            return false;
        }

        // Ethertype has to be set to "0x88cc"
        String lldpTypeString = "88cc";
        byte[] etherType = Util.getBytes(data, AddressPositions.ETHER_TYPE, 2);
        if (!lldpTypeString.equals(Util.asString(etherType))) {
            return false;
        }

        logger.trace("[Switch#{}]: OFPacketOut is LLDP!", this.dpidString);
        return true;
    }

    @Override
    public byte getOFVersion() {
        return OFP_VERSION;
    }

    @Override
    public void setChannel(SocketChannel chan) throws IOException {

        this.socket = chan;
        this.ofStream = new OFMessageAsyncStream(chan, new BasicFactory());

    }

    @Override
    public SocketChannel getChannel() {
        return this.socket;
    }

    /**
     * Interface for Packet In for Statistics.
     */
    private void packetIn(OFMessage in) {
        this.lastPacketIn = new Date();
        for (IStatistics stat : this.statistics) {
            stat.packetIn(in);
        }
    }

    /**
     * Interface for Packet In for Statistics.
     */
    private void packetOut(OFMessage out) {
        for (IStatistics stat : this.statistics) {
            stat.packetOut(out);
        }
    }

    @Override
    public int getNextFreeBufferId() {
        return this.buffer.getNextFreeBufferId();
    }

    @Override
    public int packetInQueueLength() {
        if (this.queueLengthMonitor != null) {
            this.queueLengthMonitor.newQueueLength(this.incomingListSwitchRunner.size());
        }
        return this.incomingListTrafficGenerator.size();
    }

    @Override
    public void queuePacketIn(byte[] payload, short port, boolean safeFlag) {
        synchronized (incomingListTrafficGenerator) {
            incomingListTrafficGenerator.add(new QueuedPacket(payload, port, safeFlag));
        }

    }

    /**
     * Here the TrafficGenerator queues new Packets.
     *
     * @param payloads a List of byte[]-payloads.
     */
    public void queuePacketInS(Collection<byte[]> payloads) {
        List<QueuedPacket> packetQueue = new ArrayList<QueuedPacket>();
        Iterator<byte[]> iter = payloads.iterator();
        while (iter.hasNext()) {
            packetQueue.add(new QueuedPacket(iter.next(), (short) 2, false));
        }
        synchronized (incomingListTrafficGenerator) {
            incomingListTrafficGenerator.addAll(packetQueue);
        }
    }

    /**
     * Swap Pointers of the two PacketInQueues
     */
    private void swapLists() {
        synchronized (incomingListTrafficGenerator) {
            List<QueuedPacket> tmp = incomingListTrafficGenerator;
            incomingListTrafficGenerator = incomingListSwitchRunner;
            incomingListSwitchRunner = tmp;
        }
    }

    @Override
    public boolean hasPacketInQueued() {
        if (this.incomingListSwitchRunner.isEmpty()) {
            synchronized (incomingListTrafficGenerator) {
                return !incomingListTrafficGenerator.isEmpty();
            }
        } else {
            return true;
        }

    }

    @Override
    public void sendPacketIn() {
        if (this.sessionStopped) {
            return;
        }

        if (this.incomingListSwitchRunner.isEmpty()) {
            swapLists();
        }

        if (this.lastXid <= 0) {
            this.lastXid = 1;
        }

        if (!this.crashed && this.socket != null) {
            // Create newPacketIn msg dummy
            OFPacketIn newOFPacketIn = new OFPacketIn();
            newOFPacketIn.setReason(OFPacketInReason.NO_MATCH);

            if (this.batchSending) {
                // Sending imminent -> change channel to Write
                this.socket.keyFor(this.runner.getSelector()).interestOps(SelectionKey.OP_WRITE);
                for (QueuedPacket packet : incomingListSwitchRunner) {
                    if (packet != null) {
                        // Fill newPacketIn msg dummy with bufferId and payload
                        newOFPacketIn.setInPort(packet.getPort());
                        int buffid = getNextFreeBufferId();
                        newOFPacketIn.setBufferId(buffid);
                        int xid = this.lastXid++;
                        newOFPacketIn.setXid(xid);
                        newOFPacketIn.setPacketData(packet.getPayload());
                        newOFPacketIn.setLengthU(OFPacketIn.MINIMUM_LENGTH + packet.getPayload().length);
                        newOFPacketIn.setTotalLength((short) packet.getPayload().length);

                        if (packet.getSafeFlag()) {
                            BufferID buff = new BufferID(buffid);
                            this.savedBuffIds.add(buff);
                            this.savedPayloads.put(buff, packet.getPayload());
                            logger.trace("[Switch#{}]: Packet saved! Buffid: {} - Port: {}", this.dpidString, buffid, packet.getPort());
//                              System.out.println(this.toString() + ": saved ARP DST-IP: " + Util.fromIPvAddressBytes(Util.getBytes(packet.getPayload(), AddressPositions.ARP_IP_DST, 4)));
                        }

                        // Send Packet via ofSwitch
                        send(newOFPacketIn);
                    }
                }
                // Sending done -> change channel back to read
                this.socket.keyFor(this.runner.getSelector()).interestOps(SelectionKey.OP_READ);
                incomingListSwitchRunner.clear();
            } else {
                if (!incomingListSwitchRunner.isEmpty()) {
                    QueuedPacket packet = incomingListSwitchRunner.remove(0);

                    if (packet != null) {
                        // Fill newPacketIn msg dummy with bufferId and payload
                        newOFPacketIn.setInPort(packet.getPort());
                        int buffid = getNextFreeBufferId();
                        newOFPacketIn.setBufferId(buffid);
                        newOFPacketIn.setXid(this.lastXid++);
                        newOFPacketIn.setPacketData(packet.getPayload());
                        newOFPacketIn.setLengthU(OFPacketIn.MINIMUM_LENGTH + packet.getPayload().length);

                        if (packet.getSafeFlag()) {
                            this.savedBuffIds.add(new BufferID(buffid));
                            this.savedPayloads.put(new BufferID(buffid), packet.getPayload());
                            logger.trace("[Switch#{}]: Packet saved! Buffid: {} - Port: {}", this.dpidString, buffid, packet.getPort());
                        }
                        // Sending imminent -> change channel to Write
                        this.socket.keyFor(this.runner.getSelector()).interestOps(SelectionKey.OP_WRITE);
                        // Send Packet via ofSwitch
                        send(newOFPacketIn);
                        // Sending done -> change channel back to read
                        this.socket.keyFor(this.runner.getSelector()).interestOps(SelectionKey.OP_READ);
                    }
                }
            }

            if (this.incomingListSwitchRunner.isEmpty()) {
                swapLists();
            }
        }

    }

    @Override
    public long lastPacketInTime() {
        return this.lastPacketIn.getTime();
    }

    @Override
    public void startSession() {
        for (IStatistics stat : this.statistics) {
            stat.start();
        }
    }

    @Override
    public void evaluate() {
        if (this.queueLengthMonitor != null) {
            this.queueLengthMonitor.evaluate();
        }
        for (IStatistics stat : this.statistics) {
            stat.evaluate();
        }

    }

    @Override
    public void report() {
        if (this.queueLengthMonitor != null) {
            this.queueLengthMonitor.report();
        }
        for (IStatistics stat : this.statistics) {
            stat.report();
        }

    }

    @Override
    public void stopSession() {
        this.sessionStopped = true;
        for (IStatistics stat : this.statistics) {
            stat.stop();
        }

        this.lastPacketIn = new Date();
    }

    @Override
    public OFSwitchRunner getRunner() {
        return this.runner;
    }

    /**
     * toStringStuff
     *
     * @return Stuff which describes this ofSwitch
     */
    @Override
    public String toString() {
        NumberFormat formatter = new DecimalFormat("#000");
        String output = new String();
        output += "DPID:" + formatter.format(this.dpid) + ";Ports:" + this.ports;
        return output;
    }

    @Override
    public boolean hadOFComm() {
        return this.hadOFComm;
    }

    @Override
    public long getConDelay() {
        return this.conDelay;
    }

    @Override
    public long getStartDelay() {
        return this.startDelay;
    }

    @Override
    public long getStopDelay() {
        return this.stopDelay;
    }

    @Override
    public long getDpid() {
        return this.dpid;
    }

    @Override
    public List<OFPhysicalPort> getPorts() {
        return this.feat_reply.getPorts();
    }

    @Override
    public int getIAT() {
        return this.iat;
    }

    @Override
    public int getFillThreshold() {
        return this.fillThreshold;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (batchSending ? 1231 : 1237);
        result = prime * result + bufferSize;
        result = prime * result
                + ((config_reply == null) ? 0 : config_reply.hashCode());
        result = prime * result + (crashed ? 1231 : 1237);
        result = prime * result + (int) (dpid ^ (dpid >>> 32));
        result = prime * result
                + ((dpidString == null) ? 0 : dpidString.hashCode());
        result = prime * result
                + ((feat_reply == null) ? 0 : feat_reply.hashCode());
        result = prime * result + fillThreshold;
        result = prime * result
                + ((flowmod_handler == null) ? 0 : flowmod_handler.hashCode());
        result = prime * result + (hadOFComm ? 1231 : 1237);
        result = prime * result + iat;
        result = prime
                * result
                + ((incomingListSwitchRunner == null) ? 0
                        : incomingListSwitchRunner.hashCode());
        result = prime
                * result
                + ((incomingListTrafficGenerator == null) ? 0
                        : incomingListTrafficGenerator.hashCode());
        result = prime * result
                + ((lastPacketIn == null) ? 0 : lastPacketIn.hashCode());
        result = prime * result + lastXid;
        result = prime * result
                + ((pcapFileName == null) ? 0 : pcapFileName.hashCode());
        result = prime * result + ports;
        result = prime * result + ((runner == null) ? 0 : runner.hashCode());
        result = prime * result
                + ((savedBuffIds == null) ? 0 : savedBuffIds.hashCode());
        result = prime * result
                + ((savedPayloads == null) ? 0 : savedPayloads.hashCode());
        result = prime * result + (sendFlag ? 1231 : 1237);
        result = prime * result + session;
        result = prime * result + (sessionStopped ? 1231 : 1237);
        result = prime * result + (int) (conDelay ^ (conDelay >>> 32));
        result = prime * result
                + ((statistics == null) ? 0 : statistics.hashCode());
        result = prime * result
                + ((stats_handler == null) ? 0 : stats_handler.hashCode());
        result = prime * result + (int) (stopDelay ^ (stopDelay >>> 32));
        result = prime * result
                + ((topology == null) ? 0 : topology.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        OFConnection1_zero other = (OFConnection1_zero) obj;
        if (batchSending != other.batchSending) {
            return false;
        }
        if (bufferSize != other.bufferSize) {
            return false;
        }
        if (config_reply == null) {
            if (other.config_reply != null) {
                return false;
            }
        } else if (!config_reply.equals(other.config_reply)) {
            return false;
        }
        if (crashed != other.crashed) {
            return false;
        }
        if (dpid != other.dpid) {
            return false;
        }
        if (dpidString == null) {
            if (other.dpidString != null) {
                return false;
            }
        } else if (!dpidString.equals(other.dpidString)) {
            return false;
        }
        if (feat_reply == null) {
            if (other.feat_reply != null) {
                return false;
            }
        } else if (!feat_reply.equals(other.feat_reply)) {
            return false;
        }
        if (fillThreshold != other.fillThreshold) {
            return false;
        }
        if (flowmod_handler == null) {
            if (other.flowmod_handler != null) {
                return false;
            }
        } else if (!flowmod_handler.equals(other.flowmod_handler)) {
            return false;
        }
        if (hadOFComm != other.hadOFComm) {
            return false;
        }
        if (iat != other.iat) {
            return false;
        }
        if (incomingListSwitchRunner == null) {
            if (other.incomingListSwitchRunner != null) {
                return false;
            }
        } else if (!incomingListSwitchRunner
                .equals(other.incomingListSwitchRunner)) {
            return false;
        }
        if (incomingListTrafficGenerator == null) {
            if (other.incomingListTrafficGenerator != null) {
                return false;
            }
        } else if (!incomingListTrafficGenerator
                .equals(other.incomingListTrafficGenerator)) {
            return false;
        }
        if (lastPacketIn == null) {
            if (other.lastPacketIn != null) {
                return false;
            }
        } else if (!lastPacketIn.equals(other.lastPacketIn)) {
            return false;
        }
        if (lastXid != other.lastXid) {
            return false;
        }
        if (pcapFileName == null) {
            if (other.pcapFileName != null) {
                return false;
            }
        } else if (!pcapFileName.equals(other.pcapFileName)) {
            return false;
        }
        if (ports != other.ports) {
            return false;
        }
        if (runner == null) {
            if (other.runner != null) {
                return false;
            }
        } else if (!runner.equals(other.runner)) {
            return false;
        }
        if (savedBuffIds == null) {
            if (other.savedBuffIds != null) {
                return false;
            }
        } else if (!savedBuffIds.equals(other.savedBuffIds)) {
            return false;
        }
        if (savedPayloads == null) {
            if (other.savedPayloads != null) {
                return false;
            }
        } else if (!savedPayloads.equals(other.savedPayloads)) {
            return false;
        }
        if (sendFlag != other.sendFlag) {
            return false;
        }
        if (session != other.session) {
            return false;
        }
        if (sessionStopped != other.sessionStopped) {
            return false;
        }
        if (conDelay != other.conDelay) {
            return false;
        }
        if (statistics == null) {
            if (other.statistics != null) {
                return false;
            }
        } else if (!statistics.equals(other.statistics)) {
            return false;
        }
        if (stats_handler == null) {
            if (other.stats_handler != null) {
                return false;
            }
        } else if (!stats_handler.equals(other.stats_handler)) {
            return false;
        }
        if (stopDelay != other.stopDelay) {
            return false;
        }
        if (topology == null) {
            if (other.topology != null) {
                return false;
            }
        } else if (!topology.equals(other.topology)) {
            return false;
        }
        return true;
    }

    @Override
    public String getPcapFileName() {
        return this.pcapFileName;
    }

    @Override
    public String getDistribution() {
        return this.iatDistri;
    }

    @Override
    public double getDistributionPara1() {
        return this.iatDistriPara1;
    }

    @Override
    public double getDistributionPara2() {
        return this.iatDistriPara2;
    }

}
