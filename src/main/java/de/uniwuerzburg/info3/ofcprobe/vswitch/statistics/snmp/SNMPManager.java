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
package de.uniwuerzburg.info3.ofcprobe.vswitch.statistics.snmp;

import java.io.IOException;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

/**
 * SNMPManager. Requests provided SNMP OID from Host.
 *
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public class SNMPManager {

    private Snmp snmp = null;
    private String ip = null;

    /**
     * Standard Port for SNMP is 161. MUST BE PROVIDED!
     *
     * @param ip Host IP with port ip/Port eg.: 192.168.0.1/161
     */
    public SNMPManager(String ip) {
        this.ip = ip;
    }

    /**
     * Startet die SNMP Seassion.
     */
    public void start() {
        try {
            // Start SNMP Seassion
            TransportMapping<?> transport;
            transport = new DefaultUdpTransportMapping();
            this.snmp = new Snmp(transport);
            // Synchronisation
            transport.listen();
        } catch (IOException e) {
            throw new RuntimeException("Can't start SNMPManager!");
        }
    }

    /**
     * Returns the resonponse of the request
     *
     * @param oid OID.
     * @return Request.
     */
    public String getAsString(OID oid) {

        ResponseEvent event;
        event = get(new OID[]{oid});
        return event.getResponse().get(0).getVariable().toString();
    }

    /**
     * Request multiple OIDs
     *
     * @param oids OIDS
     * @return ResponseEvent
     */
    public ResponseEvent get(OID oids[]) {

        try {
            PDU pdu = new PDU();
            for (OID oid : oids) {
                pdu.add(new VariableBinding(oid));
            }
            pdu.setType(PDU.GET);
            ResponseEvent event;
            event = snmp.send(pdu, getTarget(), null);
            if (event != null) {
                return event;
            }
            throw new RuntimeException("Timeout");
        } catch (IOException e) {
            throw new RuntimeException("Problem by response the OID");
        }
    }

    /**
     * Gets Target which contains information on data request Currently, SNMP 2
     * protocol 2 is used
     *
     * @return .
     */
    private Target getTarget() {

        Address targetAddress = GenericAddress.parse(this.ip);
        CommunityTarget target = new CommunityTarget();
        // Community the client has access to
        target.setCommunity(new OctetString("public"));
        target.setAddress(targetAddress);
        target.setRetries(1);
//		 Timeout (10sec)
        target.setTimeout(10000);
        // Protocol version
        target.setVersion(SnmpConstants.version2c);
        return target;
    }
}
