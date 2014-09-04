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
package de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.arping;

import java.util.ArrayList;
import java.util.List;
import org.openflow.util.HexString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.uniwuerzburg.info3.ofcprobe.util.AddressPositions;
import de.uniwuerzburg.info3.ofcprobe.util.Util;
import de.uniwuerzburg.info3.ofcprobe.vswitch.main.config.Config;
import de.uniwuerzburg.info3.ofcprobe.vswitch.main.config.HostMapper;
import de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.PayLoadGen;
import de.uniwuerzburg.info3.ofcprobe.vswitch.connection.IOFConnection;

/**
 * This Class is handling the generation of ARPs. Currently, every "HOST"
 * generates an ARP-Request for every "HOST" in the Topology. Even for the ones
 * on the same ofSwitch!
 *
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public class ARPManager {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(ARPManager.class);

    /**
     * Here to save the world.
     */
    private final byte[] arpMaster;
    /**
     * The Hostmapper
     */
    private HostMapper hostMapper;
    /**
     * The Config
     */
    private Config config;

    private PayLoadGen payloadGen;

    /**
     * Constructor, initializes the Generators
     */
    public ARPManager(Config config, List<IOFConnection> ofSwitches) {
        this.config = config;
        this.hostMapper = new HostMapper(config, ofSwitches);

        this.arpMaster = preGenerateArp();

        this.config.getTopology().setHasHostMapping(true);
        this.config.getTopology().setHostMapper(this.hostMapper);

        this.payloadGen = new PayLoadGen(config);
        logger.trace("ARPStuff has been initialized!");
    }

    /**
     * Generates ARP-Requests for every "HOST" on freePorts of "ofSwitch" for
     * every possible Target-"Host" in the Topology
     *
     * @param ofSwitch
     * @return List of ARP-Requests
     */
    public List<ArpPacket> getArpsForIOFConnection(IOFConnection ofSwitch) {
        List<ArpPacket> arps = new ArrayList<>();

        List<Short> freePorts = this.config.getTopology().getFreePorts(ofSwitch.getDpid());
        for (short port : freePorts) {
            Device device = new Device(ofSwitch, port);
            String srcMac = this.hostMapper.getMacToDevice(device);
            String srcIP = this.hostMapper.getIpToDevice(device);
            List<String> targetIPs = this.hostMapper.getTargetsForIP(srcIP);
            for (String targetIP : targetIPs) {
                logger.trace("Generating ARP for targetIP={} from sourceIP={}", targetIP, srcIP);
                byte[] payload = generateARP(srcMac, srcIP, targetIP);
                ArpPacket arp = new ArpPacket(port, payload);
                arps.add(arp);
            }
        }

        if (logger.isTraceEnabled()) {
            for (int i = 0; i < arps.size(); i++) {
                logger.trace("{}(allarps) i={} targetIP:{} sourceIP: {}", ofSwitch.toString(), i,
                        arps.get(i).IPDSTtoString(), arps.get(i).IPSRCtoString());
            }
        }
        return arps;
    }

    public List<TCPPacket> getTCPSynsForIOFConnection(IOFConnection ofSwitch) {
        List<TCPPacket> tcpSyns = new ArrayList<>();

        List<Short> freePorts = this.config.getTopology().getFreePorts(ofSwitch.getDpid());
        for (short port : freePorts) {
            Device device = new Device(ofSwitch, port);
            String srcMac = this.hostMapper.getMacToDevice(device);
            String srcIP = this.hostMapper.getIpToDevice(device);
            List<String> targetIPs = this.hostMapper.getTargetsForIP(srcIP);
            for (String targetIP : targetIPs) {
                logger.trace("Generating TCPSYN for targetIP={} from sourceIP={}", targetIP, srcIP);
                String targetMac = this.hostMapper.getMacToIp(targetIP);
                byte[] payload = this.payloadGen.generateCustomTCPSynfromStrings(srcMac, targetMac, srcIP, targetIP);
                TCPPacket tcpSyn = new TCPPacket(port, payload);
                tcpSyns.add(tcpSyn);
            }
        }

        if (logger.isTraceEnabled()) {
            for (int i = 0; i < tcpSyns.size(); i++) {
                logger.trace("{}(alltcpsyns) i={} targetIP:{} sourceIP: {}", ofSwitch.toString(), i,
                        tcpSyns.get(i).IPDSTtoString(), tcpSyns.get(i).IPSRCtoString());
            }
        }
        return tcpSyns;
    }

    /**
     * Generates an ARP-Request with the provided Addresses
     *
     * @param srcMac Source MAC
     * @param srcIP Source IP
     * @param targetIP Target IP
     * @return ARP with the provided Addresses
     */
    private byte[] generateARP(String srcMac, String srcIP, String targetIP) {
        byte[] arp = this.arpMaster;

        byte[] srcMacbytes = HexString.fromHexString(srcMac);
        byte[] srcIPbytes = Util.toIPv4AddressBytes(srcIP);
        byte[] targetIPbytes = Util.toIPv4AddressBytes(targetIP);

        arp = Util.insertByteArray(arp, srcMacbytes, AddressPositions.ETHER_MAC_SRC);
        arp = Util.insertByteArray(arp, srcMacbytes, AddressPositions.ARP_MAC_SRC);
        arp = Util.insertByteArray(arp, srcIPbytes, AddressPositions.ARP_IP_SRC);
        arp = Util.insertByteArray(arp, targetIPbytes, AddressPositions.ARP_IP_DST);

        return arp;
    }

    /**
     * Pregenerates an ARP-Request Packet. So all Fields of the Packet that are
     * always the same are set.
     *
     * @return a pregenarted ARP-Request
     */
    private byte[] preGenerateArp() {
        byte[] packet = new byte[60];
        // Inserting MAC Header Fields
        byte[] ethernetType = Util.toByteArray("0806"); // Type = ARP
        packet = Util.insertByteArray(packet, ethernetType, AddressPositions.ETHER_TYPE);

        byte[] macBroadcast = HexString.fromHexString("ff:ff:ff:ff:ff:ff");
        byte[] macUnknow = HexString.fromHexString("00:00:00:00:00:00");
        packet = Util.insertByteArray(packet, macBroadcast, AddressPositions.ETHER_MAC_DST);
        packet = Util.insertByteArray(packet, macUnknow, AddressPositions.ARP_MAC_DST);

        byte[] hardwareNprotocolTypeNhardwareNprotocolSizeNopcode = Util.toByteArray("0001080006040001");
        packet = Util.insertByteArray(packet, hardwareNprotocolTypeNhardwareNprotocolSizeNopcode, 14);

        byte[] padding = Util.toByteArray("000000000000000000000000000000000000");
        packet = Util.insertByteArray(packet, padding, AddressPositions.ARP_PADDING);

        logger.trace("ARP Master Data: {}", Util.asString(packet));
        return packet;
    }

}
