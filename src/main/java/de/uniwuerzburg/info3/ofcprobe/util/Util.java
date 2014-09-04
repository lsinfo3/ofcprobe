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
package de.uniwuerzburg.info3.ofcprobe.util;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.openflow.protocol.OFMatch;
import org.openflow.util.U8;

/**
 * Static Utilities Class. Containing helper Methods that are used over and over
 * again
 *
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public class Util {

    /**
     * Parses a string containing hex numbers to a byte array.
     *
     * @param source source string
     * @return byte array from the parsed data
     */
    public static byte[] toByteArray(String source) {
        String s = source.replaceAll(" |\n", "");
        byte[] b = new byte[s.length() / 2];
        if ((s.length() % 2) != 0) {
            throw new IllegalArgumentException(
                    "need even number of hex double digits [" + s.length()
                    + "]");
        }
        for (int i = 0; i < s.length(); i += 2) {
            String q = s.substring(i, i + 2);
            b[i / 2] = (byte) Integer.parseInt(q, 16);
        }
        return b;
    }

    /**
     * Insert array in greater array
     *
     * @param array the target array
     * @param insert the source array
     * @param pos starting position of the target array
     * @return filled target array
     */
    public static byte[] insertByteArray(byte[] array, byte[] insert, int pos) {
        if (pos + insert.length <= array.length) {
            for (int i = 0; i < insert.length; i++) {
                array[i + pos] = insert[i];
            }
        }
        return array;
    }

    /**
     * Insert long value into byte array.
     *
     * @param buf the target array
     * @param value the long to insert into the array.
     * @param pos starting position of the target array.
     * @param cnt the number of bytes to insert.
     */
    public static void insertLong(byte[] buf, long value, int pos, int cnt) {
        for (int i = 0; i < cnt; i++) {
            buf[pos + cnt - i - 1] = (byte) (value & 0xff);
            value >>= 8;
        }
    }

    /**
     * Get a part of a Byte[]
     *
     * @param data byte[] to read from
     * @param offset offset to start reading
     * @param cnt number of bytes to read
     * @return byte[]-part
     */
    public static byte[] getBytes(byte[] data, int offset, int cnt) {
        byte[] output = new byte[cnt];
        if (cnt <= data.length && offset + cnt <= data.length) {
            for (int i = 0; i < cnt; i++) {
                output[i] = data[offset + i];
            }
        }
        return output;
    }

    /**
     * HelperMethod to Convert int to byte[]
     *
     * @param input in
     * @return byte[]
     */
    public static byte[] toByte(int input, int count) {
        return ByteBuffer.allocate(count).putInt(input).array();
    }

    /**
     * Convert a string of bytes to a hex string
     *
     * @param bytes
     * @return "0fcafedeadbeef"
     */
    public static String asString(byte[] bytes) {
        int i;
        String ret = "";
        String tmp;
        for (i = 0; i < bytes.length; i++) {
            if (i > 0) {
                ret += "";
            }
            tmp = Integer.toHexString(U8.f(bytes[i]));
            if (tmp.length() == 1) {
                ret += "0";
            }
            ret += tmp;
        }
        return ret;
    }

    /**
     * Accepts an IPv4 address of the form xxx.xxx.xxx.xxx, ie 192.168.0.1 and
     * returns the corresponding byte array.
     *
     * @param ipAddress The IP address in the form xx.xxx.xxx.xxx.
     * @return The IP address separated into bytes
     */
    public static byte[] toIPv4AddressBytes(String ipAddress) {
        String[] octets = ipAddress.split("\\.");
        if (octets.length != 4) {
            throw new IllegalArgumentException("Specified IPv4 address must"
                    + "contain 4 sets of numerical digits separated by periods");
        }

        byte[] result = new byte[4];
        for (int i = 0; i < 4; ++i) {
            result[i] = Integer.valueOf(octets[i]).byteValue();
        }
        return result;
    }

    /**
     * Accepts an IPv4 address in a byte array and returns the corresponding
     * 32-bit integer value.
     *
     * @param ipAddress
     * @return
     */
    public static int toIPv4Address(byte[] ipAddress) {
        int ip = 0;
        for (int i = 0; i < 4; i++) {
            int t = (ipAddress[i] & 0xff) << ((3 - i) * 8);
            ip |= t;
        }
        return ip;
    }

    /**
     * Accepts an IPv4 address and returns of string of the form xxx.xxx.xxx.xxx
     * ie 192.168.0.1
     *
     * @param ipAddress
     * @return
     */
    public static String fromIPv4Address(int ipAddress) {
        StringBuffer sb = new StringBuffer();
        int result = 0;
        for (int i = 0; i < 4; ++i) {
            result = (ipAddress >> ((3 - i) * 8)) & 0xff;
            sb.append(Integer.valueOf(result).toString());
            if (i != 3) {
                sb.append(".");
            }
        }
        return sb.toString();
    }

    /**
     * Get IP String from byte[]-IP
     *
     * @param ipAddress the byte[]-IP
     * @return IP as String
     */
    public static String fromIPvAddressBytes(byte[] ipAddress) {
        return Util.fromIPv4Address(Util.toIPv4Address(ipAddress));
    }

    /**
     * Overriden equals Method for OFMatch
     *
     * @param matchOne first match
     * @param matchTwo second match
     * @return do they match? *schenkelklopf*
     */
    public static boolean equalsFlow(OFMatch matchOne, OFMatch matchTwo) {

        int wildcardsOne = matchOne.getWildcards();

        int wildcardsTwo = matchTwo.getWildcards();

        // l1
        if ((wildcardsOne & OFMatch.OFPFW_IN_PORT) == 0
                && (wildcardsTwo & OFMatch.OFPFW_IN_PORT) == 0) {

            if (matchOne.getInputPort() != matchTwo.getInputPort()) {
                return false;
            }
        }

        return complementaryFlow(matchOne, matchTwo);
    }

    /**
     * equalsFlow stuff continued
     *
     * @param matchOne the first match
     * @param matchTwo the second match
     * @return do they match
     */
    private static boolean complementaryFlow(OFMatch matchOne, OFMatch matchTwo) {

        int wildcards = matchOne.getWildcards();

        int wildcardsTwo = matchTwo.getWildcards();
        // l2
        if ((wildcards & OFMatch.OFPFW_DL_DST) == 0
                && (wildcardsTwo & OFMatch.OFPFW_DL_DST) == 0) {

            if (!Arrays.equals(matchOne.getDataLayerDestination(),
                    matchTwo.getDataLayerDestination())) {
                return false;
            }
        }
        if ((wildcards & OFMatch.OFPFW_DL_SRC) == 0
                && (wildcardsTwo & OFMatch.OFPFW_DL_SRC) == 0) {

            if (!Arrays.equals(matchOne.getDataLayerSource(),
                    matchTwo.getDataLayerSource())) {
                return false;
            }
        }
        if ((wildcards & OFMatch.OFPFW_DL_TYPE) == 0
                && (wildcardsTwo & OFMatch.OFPFW_DL_TYPE) == 0) {

            if (matchOne.getDataLayerType() != matchTwo.getDataLayerType()) {
                return false;
            }
        }
        if ((wildcards & OFMatch.OFPFW_DL_VLAN) == 0
                && (wildcardsTwo & OFMatch.OFPFW_DL_VLAN) == 0) {

            if (matchOne.getDataLayerVirtualLan() != matchTwo
                    .getDataLayerVirtualLan()) {
                return false;
            }
        }
        if ((wildcards & OFMatch.OFPFW_DL_VLAN_PCP) == 0
                && (wildcardsTwo & OFMatch.OFPFW_DL_VLAN_PCP) == 0) {

            if (matchOne.getDataLayerVirtualLanPriorityCodePoint() != matchTwo
                    .getDataLayerVirtualLanPriorityCodePoint()) {
                return false;
            }
        }
        // l3
        if (matchOne.getNetworkDestinationMaskLen() > 0
                && matchTwo.getNetworkDestinationMaskLen() > 0) {

            if (matchOne.getNetworkDestination() != matchTwo
                    .getNetworkDestination()) {
                return false;
            }
        }
        if (matchOne.getNetworkSourceMaskLen() > 0
                && matchTwo.getNetworkSourceMaskLen() > 0) {

            if (matchOne.getNetworkSource() != matchTwo.getNetworkSource()) {
                return false;
            }
        }
        if ((wildcards & OFMatch.OFPFW_NW_PROTO) == 0
                && (wildcardsTwo & OFMatch.OFPFW_NW_PROTO) == 0) {

            if (matchOne.getNetworkProtocol() != matchTwo.getNetworkProtocol()) {
                return false;
            }
        }
        if ((wildcards & OFMatch.OFPFW_NW_TOS) == 0
                && (wildcardsTwo & OFMatch.OFPFW_NW_TOS) == 0) {

            if (matchOne.getNetworkTypeOfService() != matchTwo
                    .getNetworkTypeOfService()) {
                return false;
            }
        }
        // l4
        if ((wildcards & OFMatch.OFPFW_TP_DST) == 0
                && (wildcardsTwo & OFMatch.OFPFW_TP_DST) == 0) {

            if (matchOne.getTransportDestination() != matchTwo
                    .getTransportDestination()) {
                return false;
            }
        }
        if ((wildcards & OFMatch.OFPFW_TP_SRC) == 0
                && (wildcardsTwo & OFMatch.OFPFW_TP_SRC) == 0) {

            if (matchOne.getTransportSource() != matchTwo.getTransportSource()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Computes the seconds between two Dates needed for Statistics Methods to
     * determine where to put processsed Packet
     *
     * @param start StartDate
     * @param now NowDate
     * @return Intevall
     */
    public static int getIntervall(Date start, Date now) {
        long startL = start.getTime();
        long nowL = now.getTime();

        long diffi = nowL - startL;
        int inti = (int) Math.floor((diffi / 1000));
        inti--;

        return inti;
    }

    /**
     * Ensure that a ArrayList has the provided size Needed for enhance
     * ArrayList.add(index, item)
     *
     * @param list the List
     * @param size the TargetSize
     */
    public static void ensureSize(ArrayList<Integer> list, int size) {
        // Prevent excessive copying while we're adding
        list.ensureCapacity(size);
        while (list.size() < size) {
            list.add(0);
        }
    }

    public static void listSizeCheck(ArrayList<Integer> list, int intervall) {
        list.ensureCapacity(intervall + 1);
        while (list.size() < intervall + 1) {
            list.add(0);
        }

    }

    /**
     * See ensureSize
     *
     * @param list the List
     * @param size the TargetSize
     */
    public static void ensureSizeDouble(ArrayList<Double> list, int size) {
        list.ensureCapacity(size);
        while (list.size() < size) {
            list.add(0.0);
        }
    }
}
