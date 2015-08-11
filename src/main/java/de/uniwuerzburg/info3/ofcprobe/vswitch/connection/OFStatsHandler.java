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

import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import de.uniwuerzburg.info3.ofcprobe.vswitch.connection.flowtable.OFFlowTable;
import de.uniwuerzburg.info3.ofcprobe.vswitch.connection.flowtable.OFFlowTableEntry;
import de.uniwuerzburg.info3.ofcprobe.vswitch.main.config.Config;
import org.openflow.protocol.OFMatch;
import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFPort;
import org.openflow.protocol.OFStatisticsReply;
import org.openflow.protocol.OFStatisticsRequest;
import org.openflow.protocol.statistics.OFAggregateStatisticsReply;
import org.openflow.protocol.statistics.OFAggregateStatisticsRequest;
import org.openflow.protocol.statistics.OFDescriptionStatistics;
import org.openflow.protocol.statistics.OFFlowStatisticsReply;
import org.openflow.protocol.statistics.OFFlowStatisticsRequest;
import org.openflow.protocol.statistics.OFPortStatisticsReply;
import org.openflow.protocol.statistics.OFPortStatisticsRequest;
import org.openflow.protocol.statistics.OFStatistics;
import org.openflow.protocol.statistics.OFStatisticsType;
import org.openflow.protocol.statistics.OFTableStatistics;
import org.openflow.protocol.statistics.OFVendorStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public class OFStatsHandler {

    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(OFStatsHandler.class);
    /**
     * The DPID as String
     */
    private String dpid;
    /**
     * The OFFlowTable
     */
    private OFFlowTable flow_table;
    /**
     * Randomize Statistics Fields
     */
    private boolean randomizeFlag;
    /**
     * The Config
     */
    private Config config;

    /**
     * Constructor
     *
     * @param config the config
     * @param flow_table the flow_table
     */
    public OFStatsHandler(Config config, OFFlowTable flow_table) {
        this.config = config;
        NumberFormat dpidFormatter = new DecimalFormat("#000");
        this.dpid = dpidFormatter.format(config.getSwitchConfig().getDpid());
        this.flow_table = flow_table;
        this.randomizeFlag = config.getSwitchConfig().getRandomizeStats();
    }

    /**
     * Build Stats Reply for incoming StatRequest
     *
     * @param incomingStat the incoing StatRequest
     * @return Stats Reply
     */
    public OFMessage buildStatsReply(OFStatisticsRequest incomingStat) {
        OFStatisticsType incomingStatType = incomingStat.getStatisticType();
        OFStatisticsReply stat_reply = new OFStatisticsReply();

        List<OFStatistics> statistics = new ArrayList<>();

        // Important to set StatisticType!!!!-> else Crash!
        switch (incomingStatType) {
            case AGGREGATE:
                logger.trace("[Switch#{}]: IMPLEMENTED STAT_REQUEST: {}", this.dpid, incomingStatType.toString());

                stat_reply.setStatisticType(OFStatisticsType.AGGREGATE);

                if (!incomingStat.getStatistics().isEmpty()) {
                    OFAggregateStatisticsRequest aggReq = (OFAggregateStatisticsRequest) incomingStat.getStatistics().get(0);
                    OFAggregateStatisticsReply aggReply = aggregateReplyBuilder(aggReq);
                    statistics.add(aggReply);
                }

                break;
            case DESC:
                stat_reply.setStatisticType(OFStatisticsType.DESC);

                OFDescriptionStatistics descStat = descStatBuilder(incomingStat);
                statistics.add(descStat);

                break;
            case FLOW:
                logger.trace("[Switch#{}]: IMPLEMENTED STAT_REQUEST: {}", this.dpid, incomingStatType.toString());
                stat_reply.setStatisticType(OFStatisticsType.FLOW);

                if (!incomingStat.getStatistics().isEmpty()) {
                    OFFlowStatisticsRequest flowReq = (OFFlowStatisticsRequest) incomingStat.getStatistics().get(0);
                    List<OFFlowStatisticsReply> flowReplyList = flowReplyBuilder(flowReq);
                    statistics.addAll(flowReplyList);
                }

                break;
            case PORT:
                logger.trace("[Switch#{}]: STAT_REQUEST: {}", this.dpid, incomingStatType.toString());
                stat_reply.setStatisticType(OFStatisticsType.PORT);

                if (!incomingStat.getStatistics().isEmpty()) {
                    OFPortStatisticsRequest stati = (OFPortStatisticsRequest) incomingStat.getStatistics().get(0);
                    List<OFPortStatisticsReply> portReply = portReplyBuilder(stati);
                    statistics.addAll(portReply);
                }

                break;
            case QUEUE:
                logger.trace("[Switch#{}]: NOT IMPLEMENTED STAT_REQUEST: {}", this.dpid, incomingStatType.toString());
                stat_reply.setStatisticType(OFStatisticsType.QUEUE);

                break;
            case TABLE:
                logger.trace("[Switch#{}]: IMPLEMENTED STAT_REQUEST: {}", this.dpid, incomingStatType.toString());
                stat_reply.setStatisticType(OFStatisticsType.TABLE);

                List<OFTableStatistics> tableStats = tableStatBuilder();
                statistics.addAll(tableStats);

                break;
            case VENDOR:
                logger.trace("[Switch#{}]: NOT FULLY IMPLEMENTED STAT_REQUEST: {}", this.dpid, incomingStatType.toString());
                stat_reply.setStatisticType(OFStatisticsType.VENDOR);

                OFVendorStatistics vendorStat = vendorReplyBuilder();
                statistics.add(vendorStat);

                break;
            default:
                logger.error("[Switch#{}]: NON-DEFINED STAT_REQUEST: {}", this.dpid, incomingStatType.toString());
                break;

        }

        stat_reply.setXid(incomingStat.getXid());
        stat_reply.setStatistics(statistics);
        return stat_reply;

    }

    /**
     * FlowStatisticsReply Builder for incoming OFFlowStatisticsRequest
     *
     * @param flowReq incoming OFFlowStatisticsRequest
     * @return List of OFFlowStatisticsReplies
     */
    private List<OFFlowStatisticsReply> flowReplyBuilder(OFFlowStatisticsRequest flowReq) {
        List<OFFlowStatisticsReply> flowReplyList = new ArrayList<OFFlowStatisticsReply>();
        short port = flowReq.getOutPort();

        Map<OFMatch, OFFlowTableEntry> matchedFlows = this.flow_table.getMatchingFlows(flowReq.getMatch(), false);
        if (!matchedFlows.isEmpty()) {
            Iterator<Map.Entry<OFMatch, OFFlowTableEntry>> matchIter = matchedFlows.entrySet().iterator();

            while (matchIter.hasNext()) {
                Map.Entry<OFMatch, OFFlowTableEntry> entry = matchIter.next();
                OFFlowTableEntry tableEntry = entry.getValue();

                OFFlowStatisticsReply flowReply = new OFFlowStatisticsReply();
                flowReply.setActions(tableEntry.getActions());
                flowReply.setCookie(tableEntry.getCookie());
                flowReply.setDurationNanoseconds(tableEntry.getNanoDuration());
                flowReply.setDurationSeconds(tableEntry.getSecondDuration());
                flowReply.setHardTimeout(tableEntry.getHardTimeOut());
                flowReply.setIdleTimeout(tableEntry.getIdleTimeOut());
                flowReply.setMatch(entry.getKey());
                flowReply.setPriority(tableEntry.getPriority());

                if (this.randomizeFlag) {
                    long newByteCount = randomLong();
                    flowReply.setByteCount(newByteCount);
                    flowReply.setPacketCount(newByteCount / 1024);
                } else {
                    flowReply.setByteCount(tableEntry.getByteCounter());
                    flowReply.setPacketCount(tableEntry.getPacketCounter());
                }
                flowReply.setTableId((byte) 42);

                if (port != OFPort.OFPP_NONE.getValue()) {
                    if (entry.getValue().actionsContainOutport(port)) {
                        flowReplyList.add(flowReply);
                    }
                } else {
                    flowReplyList.add(flowReply);
                }
            }

        }
        return flowReplyList;
    }

    /**
     * OFTableStatistics builder
     *
     * @return the list
     */
    private List<OFTableStatistics> tableStatBuilder() {
        List<OFTableStatistics> tableStats = new ArrayList<>();

        for (int i = 0; i < this.flow_table.size(); i++) {
            OFTableStatistics tableStat = new OFTableStatistics();
            tableStat.setActiveCount(42);
            tableStat.setLookupCount(42);
            tableStat.setMatchedCount(42);
            tableStat.setMaximumEntries(this.flow_table.capacity());
            tableStat.setName("toblerOne");
            tableStat.setTableId((byte) 42);

            tableStats.add(tableStat);
        }

        return tableStats;
    }

    /**
     * VendorReplyBuilder
     *
     * @return the OFVendor MSG
     */
    private OFVendorStatistics vendorReplyBuilder() {
        OFVendorStatistics vendorStat = new OFVendorStatistics();
        vendorStat.setLength(4);
        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.putInt(42);
        buf.flip();
        vendorStat.readFrom(buf);

        return vendorStat;
    }

    /**
     * PortReplyBuilder for the OF_STATISTICS_PORT_REQUEST
     *
     * @param portRequest the incoming portRequest
     * @return the List of OfPortStatsReplies
     */
    private List<OFPortStatisticsReply> portReplyBuilder(OFPortStatisticsRequest portRequest) {

        List<OFPortStatisticsReply> replyList = new ArrayList<>();
        if (portRequest.getPortNumber() != OFPort.OFPP_NONE.getValue()) {
            OFPortStatisticsReply portReply = new OFPortStatisticsReply();
            portReply.setPortNumber(portRequest.getPortNumber());
            if (this.randomizeFlag) {
                portReply.setCollisions(randomInt());
                portReply.setReceiveBytes(randomInt());
                portReply.setReceiveCRCErrors(randomInt());
                portReply.setReceiveDropped(randomInt());
                portReply.setreceiveErrors(randomInt());
                portReply.setReceiveFrameErrors(randomInt());
                portReply.setReceiveOverrunErrors(randomInt());
                portReply.setreceivePackets(randomInt());
                portReply.setTransmitBytes(randomInt());
                portReply.setTransmitDropped(randomInt());
                portReply.setTransmitErrors(randomInt());
                portReply.setTransmitPackets(randomInt());
            } else {
                portReply.setCollisions(42);
                portReply.setReceiveBytes(42);
                portReply.setReceiveCRCErrors(42);
                portReply.setReceiveDropped(42);
                portReply.setreceiveErrors(42);
                portReply.setReceiveFrameErrors(42);
                portReply.setReceiveOverrunErrors(42);
                portReply.setreceivePackets(42);
                portReply.setTransmitBytes(42);
                portReply.setTransmitDropped(42);
                portReply.setTransmitErrors(42);
                portReply.setTransmitPackets(42);
            }
            replyList.add(portReply);
        } else {
            for (int i = 1; i <= this.config.getSwitchConfig().getPortCountperSwitch(); i++) {
                replyList.addAll(
                        portReplyBuilder(
                                (new OFPortStatisticsRequest()).setPortNumber((short) i)
                        )
                );
            }
        }

        return replyList;
    }

    /**
     * Description Stats Reply Builder
     *
     * @param incomingStat incoming Stats Request
     * @return the Stats Reply
     */
    private OFDescriptionStatistics descStatBuilder(OFStatisticsRequest incomingStat) {

        OFDescriptionStatistics descStat = new OFDescriptionStatistics();
        descStat.setDatapathDescription("None");
        descStat.setHardwareDescription("OFCProbe Vswitch");
        descStat.setManufacturerDescription("Lehrstuhl fuer Informatik III, University of Wuerzburg");
        descStat.setSerialNumber(String.valueOf(this.dpid));
        descStat.setSoftwareDescription("1.0.4-SNAP");

        return descStat;
    }

    /**
     * Aggregate Stats Reply Builder
     *
     * @param aggReq the incoming AggregateStatsRequest
     * @return the Stats Reply
     */
    private OFAggregateStatisticsReply aggregateReplyBuilder(OFAggregateStatisticsRequest aggReq) {
        short port = aggReq.getOutPort();
        OFAggregateStatisticsReply aggReply = new OFAggregateStatisticsReply();
        Map<OFMatch, OFFlowTableEntry> matches = this.flow_table.getMatchingFlows(aggReq.getMatch(), false);

        long byteCount = 42;
        long packetCount = 42;
        int flowCount = 42;

        if (!matches.isEmpty()) {
            Iterator<Entry<OFMatch, OFFlowTableEntry>> iter = matches.entrySet().iterator();
            while (iter.hasNext()) {
                Entry<OFMatch, OFFlowTableEntry> entry = iter.next();
                if (port != OFPort.OFPP_NONE.getValue()) {
                    if (entry.getValue().actionsContainOutport(port)) {
                        if (!this.randomizeFlag) {
                            byteCount = entry.getValue().getByteCounter();
                            packetCount = entry.getValue().getPacketCounter();
                            flowCount = entry.getValue().getFlowCount();
                        } else {
                            long newByteCount = randomLong();
                            byteCount += newByteCount;
                            packetCount += newByteCount / 1024;
                            flowCount = entry.getValue().getFlowCount();
                        }
                    }
                } else {
                    if (!this.randomizeFlag) {
                        byteCount = entry.getValue().getByteCounter();
                        packetCount = entry.getValue().getPacketCounter();
                        flowCount = entry.getValue().getFlowCount();
                    } else {
                        long newByteCount = randomLong();
                        byteCount += newByteCount;
                        packetCount += newByteCount / 1024;
                        flowCount += newByteCount / 1024 / 1024;
                    }
                }
            }
        }
        aggReply.setByteCount(byteCount);
        aggReply.setPacketCount(packetCount);
        aggReply.setFlowCount(flowCount);

        return aggReply;
    }

    /**
     * Get Random Long - Used for random Stats
     *
     * @return a random Long
     */
    private long randomLong() {
        long x = 0L;
        long y = 12345678L;
        Random r = new Random();
        return x + (long) (r.nextDouble() * (y - x));
    }

    /**
     * A Random Int - Used for random Stats
     *
     * @return a random Int
     */
    private int randomInt() {
        int x = 0;
        int y = 12345678;
        Random r = new Random();
        return x + (int) (r.nextDouble() * (y - x));
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dpid == null) ? 0 : dpid.hashCode());
        result = prime * result
                + ((flow_table == null) ? 0 : flow_table.hashCode());
        result = prime * result + (randomizeFlag ? 1231 : 1237);
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
        OFStatsHandler other = (OFStatsHandler) obj;
        if (dpid == null) {
            if (other.dpid != null) {
                return false;
            }
        } else if (!dpid.equals(other.dpid)) {
            return false;
        }
        if (flow_table == null) {
            if (other.flow_table != null) {
                return false;
            }
        } else if (!flow_table.equals(other.flow_table)) {
            return false;
        }
        if (randomizeFlag != other.randomizeFlag) {
            return false;
        }
        return true;
    }

}
