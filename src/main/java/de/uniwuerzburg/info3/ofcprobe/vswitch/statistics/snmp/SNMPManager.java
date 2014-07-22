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
 * SNMPManager.
 * Stellt eine Request an den gewuenschten Host mit entsprechender OID.
 * Gibt die Response als String wieder.
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public class SNMPManager {

    private Snmp snmp = null;
    private String ip = null;

    /**
     * Konstruktor
     * Standartport fuer SNMP ist 161. Muss angefuegt werden!
     * @param ip IP des Hosts mit port ip/Port Bsp.: 192.168.0.1/161
     */
    public SNMPManager(String ip) {

        this.ip = ip;
    }

    /**
     * Startet die SNMP Seassion.
     */
    public void start() {
        try {
            // Starten der SNMP Seassion
            TransportMapping<?> transport;
            transport = new DefaultUdpTransportMapping();
            this.snmp = new Snmp(transport);
            // Synchronisation WICHTIG!!!
            transport.listen();
        } catch (IOException e) { 
		    throw new RuntimeException("Can't start SNMPManager!");
        }		
    }

    /**
     * Liefert die Ausgabe der Abfrage.
     * @param oid OID.
     * @return Request.
     */
    public String getAsString(OID oid) {

        ResponseEvent event;
        event = get(new OID[] { oid });
        return event.getResponse().get(0).getVariable().toString();
    }

    /**
     * Fuer mehrere OID's
     * @param oids OIDS die abgefragt werden sollen.
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
            if(event != null) {
                return event;
            }
            throw new RuntimeException("Timeout");
        } catch (IOException e) {
            throw new RuntimeException("Problem by response the OID");
        }
    }

    /**
     * Liefert ein Target dass infos enthaelt wie die Daten abgerufen werden.
     * Momentan wird das SNMP 2 protokoll benutzt!!!
     * @return .
     */
    private Target getTarget() {

        Address targetAddress = GenericAddress.parse(this.ip);
        CommunityTarget target = new CommunityTarget();
        // Community auf die der client zugriff hat
        target.setCommunity(new OctetString("public"));
        // IP Adresse des Hosts.
        target.setAddress(targetAddress);
        // anzahl der Versuche.
        target.setRetries(1);
//		 Timeout (5sec)
        target.setTimeout(10000);
        // Protokoll version
        target.setVersion(SnmpConstants.version2c);
        return target;
    }
}
