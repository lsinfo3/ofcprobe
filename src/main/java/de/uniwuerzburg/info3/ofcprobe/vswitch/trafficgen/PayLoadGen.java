/* 
 * Copyright 2016 christopher.metter.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen;

import org.openflow.util.HexString;

import de.uniwuerzburg.info3.ofcprobe.util.AddressPositions;
import de.uniwuerzburg.info3.ofcprobe.util.Util;
import de.uniwuerzburg.info3.ofcprobe.vswitch.main.config.Config;
import de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.ipgen.IIpGenerator;
import de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.ipgen.RandomIPv4Generator;
import de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.ipgen.SerialIPGenerator;
import de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.macgen.IMacGen;
import de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.macgen.RandomMacGen;
import de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.macgen.SerialMacGen;
import de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.portgen.IPortGen;
import de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.portgen.RandomPortGen;
import de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.portgen.SerialPortGen;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This Class generates Payload. atm only random TCP Syns supported
 *
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public class PayLoadGen {

    AtomicInteger nextIpId;

    /**
     * Connected IPGenerator
     */
    private IIpGenerator ipGen;
    /**
     * Connected MACGenerator
     */
    private IMacGen macGen;
    /**
     * Connected PortGenerator
     */
    private IPortGen portGen;
    /**
     * Here to save the world.
     */
    private final byte[] tcpSynMaster;

    /**
     * Constructor, initializes the Generators
     */
    public PayLoadGen(Config config) {
        this.nextIpId = new AtomicInteger();

        switch (config.getTrafficGenConfig().getMacGenType()) {
            case RANDOM:
                this.macGen = new RandomMacGen();
                break;
            case SERIAL:
                this.macGen = new SerialMacGen();
                break;
            default:
                this.macGen = new SerialMacGen();
                break;
        }

        switch (config.getTrafficGenConfig().getIPGenType()) {
            case RANDOM:
                this.ipGen = new RandomIPv4Generator();
                break;
            case SERIAL:
                this.ipGen = new SerialIPGenerator();
                break;
            default:
                this.ipGen = new SerialIPGenerator();
                break;
        }

        switch (config.getTrafficGenConfig().getPortGenType()) {
            case RANDOM:
                this.portGen = new RandomPortGen();
                break;
            case SERIAL:
                this.portGen = new SerialPortGen();
                break;
            default:
                this.portGen = new SerialPortGen();
                break;
        }
        this.tcpSynMaster = preGenerateTcpSyn();
    }

    /**
     * Pregenerates a TCPSYN Packet. So all Fields of the Packet that are always
     * the same are set.
     *
     * @return a pregenarted TCPSyn
     */
    private byte[] preGenerateTcpSyn() {
        byte[] packet = new byte[66];
        // Inserting MAC Header Fields
        byte[] macType = Util.toByteArray("0800"); // Type = IP
        packet = Util.insertByteArray(packet, macType, AddressPositions.ETHER_TYPE);

        // Inserting IP Header Fields
        byte[] ipverServiceLengthIdentiFlagsTTLProtocol = Util.toByteArray("450000345ad340008006"); // IP Version + Services + TotalIPHeaderLength + Identification + IPFlags + TTL + Protocol(TCP)
        //5ad3 = IP ID
        packet = Util.insertByteArray(packet, ipverServiceLengthIdentiFlagsTTLProtocol, 14);

        // Inserting TCP Header Fields
        byte[] seqNrFlagsWindowsize = Util.toByteArray("6a9ef8220000000080023908");  // TCP Seq NR, Flags(SYN), WindowSize
        byte[] options = Util.toByteArray("020405b40402080a091273c70000000001030307"); // TCP Options
        packet = Util.insertByteArray(packet, seqNrFlagsWindowsize, 38);
        packet = Util.insertByteArray(packet, options, 54);

        return packet;
    }

    /**
     * Generate new TCPSyn Packet
     *
     * @return a randomly genrated TCPSyn Packet
     */
    public byte[] generateTCPSyn() {
        byte[] packet = tcpSynMaster;

        packet = buildMacHeader(packet, this.macGen.getMac(), this.macGen.getMac());
        packet = buildIpHeader(packet, this.ipGen.getIp(), this.ipGen.getIp());
        packet = buildTcpHeader(packet, this.portGen.getPort(), this.portGen.getPrivilegdedPort());

        return packet;

    }

    /**
     * Creates a Custom TCP Syn with provided addresses and random Ports
     *
     * @param macSrc the MacSrc
     * @param macDst the MacDst
     * @param ipSrc the IPSrc
     * @param ipDst the IPDst
     * @return the TCPSyn
     */
    public byte[] generateCustomTCPSyn(byte[] macSrc, byte[] macDst, byte[] ipSrc, byte[] ipDst) {
        byte[] packet = tcpSynMaster;

        packet = buildMacHeader(packet, macSrc, macDst);
        packet = buildIpHeader(packet, ipSrc, ipDst);
        packet = buildTcpHeader(packet, this.portGen.getPort(), this.portGen.getPrivilegdedPort());

        return packet;
    }

    /**
     * Generate Custom TCP Syn with provided addresses (as String) and random
     * Ports
     *
     * @param macSrc the MacSrc
     * @param macDst the MacDst
     * @param ipSrc the IPSrc
     * @param ipDst the IPDst
     * @return the TCPSyn
     */
    public byte[] generateCustomTCPSynfromStrings(String macSrc, String macDst, String ipSrc, String ipDst) {
        byte[] packet = tcpSynMaster;

        packet = buildMacHeader(packet, HexString.fromHexString(macSrc), HexString.fromHexString(macDst));
        packet = buildIpHeader(packet, Util.toIPv4AddressBytes(ipSrc), Util.toIPv4AddressBytes(ipDst));
        packet = buildTcpHeader(packet, this.portGen.getPort(), this.portGen.getPrivilegdedPort());

        return packet;
    }

    /**
     * Builds the TCP Header with Random Ports.
     *
     * @param packet the packet in which the TCP Header will be inserted
     * @return the inserted packet with TCP Header.
     */
    private byte[] buildTcpHeader(byte[] packet, byte[] srcPort, byte[] dstPort) {
        packet = Util.insertByteArray(packet, srcPort, AddressPositions.TCP_PORT_SRC);
        packet = Util.insertByteArray(packet, dstPort, AddressPositions.TCP_PORT_DST);

        long checksumL = checksum(packet, 32, 34);
        Util.insertLong(packet, checksumL, 50, 2);

        return packet;
    }

    /**
     * Builds the IP Header with Random IPs.
     *
     * @param packet the packet in which the IP Header will be inserted
     * @return the inserted packet with IP Header.
     */
    private byte[] buildIpHeader(byte[] packet, byte[] ipSrc, byte[] ipDst) {
        packet = Util.insertByteArray(packet, ipSrc, 26);
        packet = Util.insertByteArray(packet, ipDst, 30);
        packet = insertIpId(packet, this.nextIpId.getAndIncrement());

        long checksum = checksum(packet, 20, 14);
        Util.insertLong(packet, checksum, 24, 2);

        return packet;
    }

    public byte[] insertIpId(byte[] packet, int ipId) {
        if (ipId > 65535) {
            this.nextIpId = new AtomicInteger();
            ipId = this.nextIpId.getAndIncrement();
        }
        String hexString = Integer.toHexString(ipId);
        if ((hexString.length() % 2) != 0) {
            hexString = "0" + hexString;
        }
        byte[] ipIdbyte = hexStringToByteArray(hexString);
        packet = Util.insertByteArray(packet, ipIdbyte, 18);

        return packet;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * Builds the MAC Header with MAC Addresses.
     *
     * @param packet the packet in which the MAC Header will be inserted
     * @return the inserted packet with MAC Header.
     */
    private byte[] buildMacHeader(byte[] packet, byte[] macSrc, byte[] macDst) {
        packet = Util.insertByteArray(packet, macDst, AddressPositions.ETHER_MAC_DST);
        packet = Util.insertByteArray(packet, macSrc, AddressPositions.ETHER_MAC_SRC);
        return packet;
    }

    /**
     * Calculates a checksum for IP or TCP Header
     *
     * @param buf the packet with the header
     * @param length length of the header
     * @param offset startingposition of the header
     * @return checksum of the header
     */
    private long checksum(byte[] buf, int length, int offset) {
        int i = 0;
        long sum = 0;
        while (length > 0) {
            sum += (buf[offset + i++] & 0xff) << 8;
            if ((--length) == 0) {
                break;
            }
            sum += (buf[offset + i++] & 0xff);
            --length;
        }

        return (~((sum & 0xFFFF) + (sum >> 16))) & 0xFFFF;
    }
}
