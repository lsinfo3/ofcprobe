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

import de.uniwuerzburg.info3.ofcprobe.util.AddressPositions;
import de.uniwuerzburg.info3.ofcprobe.util.Util;

/**
 * A MetaPacket for storing Packets in the Queue
 *
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public class QueuedPacket {

    /**
     * The Payload
     */
    private byte[] payload;
    /**
     * The ingress port
     */
    private short port;
    /**
     * The SafeFlag
     */
    private boolean safeFlag;

    /**
     * Constructor
     *
     * @param payload packet as byte[]
     * @param port ingress Port
     * @param safeFlag safe it for arp/tcp ing?
     */
    public QueuedPacket(byte[] payload, short port, boolean safeFlag) {
        this.payload = payload;
        this.port = port;
        this.safeFlag = safeFlag;
    }

    /**
     * Get the Payload
     *
     * @return the Payload
     */
    public byte[] getPayload() {
        return this.payload;
    }

    /**
     * Get the Port
     *
     * @return the Port
     */
    public short getPort() {
        return this.port;
    }

    /**
     * Get Safe Flag
     *
     * @return the flag
     */
    public boolean getSafeFlag() {
        return this.safeFlag;
    }

    @Override
    public String toString() {
        return Util.fromIPvAddressBytes(Util.getBytes(this.payload, AddressPositions.ARP_IP_DST, 4));
    }
}
