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
