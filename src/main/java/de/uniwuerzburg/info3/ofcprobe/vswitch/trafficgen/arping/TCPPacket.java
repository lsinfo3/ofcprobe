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
package de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.arping;

import java.util.Arrays;

import de.uniwuerzburg.info3.ofcprobe.util.AddressPositions;
import de.uniwuerzburg.info3.ofcprobe.util.Util;

/**
 * An TCP Packet containing a Payload(the byte[]-code of the TCP) and a Port on
 * which it is coming in to an ofSwitch
 *
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public class TCPPacket {

    //The IOFConnection Port the ARP will be flooded out
    private Short port;
    //The Payload
    private byte[] payload;

    public TCPPacket(Short port, byte[] payload) {
        this.port = port;
        this.payload = payload.clone();
    }

    public Short getPort() {
        return this.port;
    }

    public byte[] getPayload() {
        return this.payload;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(payload);
        result = prime * result + ((port == null) ? 0 : port.hashCode());
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
        TCPPacket other = (TCPPacket) obj;
        if (!Arrays.equals(payload, other.payload)) {
            return false;
        }
        if (port == null) {
            if (other.port != null) {
                return false;
            }
        } else if (!port.equals(other.port)) {
            return false;
        }
        return true;
    }

    /**
     * Prints the IP Dst in the ARP Packet
     *
     * @return
     */
    public String IPDSTtoString() {
        return Util.fromIPvAddressBytes(Util.getBytes(this.payload, AddressPositions.ARP_IP_DST, 4));
    }

    /**
     * Prints the IP SRC in the ARP Packet
     *
     * @return
     */
    public String IPSRCtoString() {
        return Util.fromIPvAddressBytes(Util.getBytes(this.payload, AddressPositions.ARP_IP_SRC, 4));
    }
}
