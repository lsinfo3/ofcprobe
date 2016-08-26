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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import cn.seed.pcap.parser.PCAPPackageParser;
import de.uniwuerzburg.info3.ofcprobe.vswitch.connection.IOFConnection;
import cn.seed.pcap.parser.Package;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PreLoads a PCAP and delivers next PacketPayload from PCAP on request
 *
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public class PCAPLoader {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(PCAPLoader.class);
    /**
     * The PCAP to load
     */
    private String fileName;
    /**
     * List of Payloads
     */
    private List<byte[]> payloads;
    /**
     * Iterator
     */
    private Iterator<byte[]> iterator;

    /**
     * Constructor
     *
     * @param ofSwitch
     */
    PCAPLoader(IOFConnection ofSwitch) {
        this.fileName = ofSwitch.getPcapFileName();

        this.payloads = new ArrayList<>();
    }

    /**
     * Preload PCAP and safe Payloads in List
     */
    public void load() {
        logger.trace("Loading PCAP File ...");
        PCAPPackageParser parser = new PCAPPackageParser(this.fileName);
        Package packet = parser.getNextPackage();
        while (packet != null) {
            this.payloads.add(packet.data.raw_data);
            packet = parser.getNextPackage();
        }
        parser.close();
        this.iterator = this.payloads.iterator();
        logger.trace("PCAP with {} Payloads loaded!", this.payloads.size());
    }

    /**
     * Gets the Next Payload, if Last Payload reached, start at payload#1
     *
     * @return Payload
     */
    public byte[] nextPayload() {
        if (this.iterator.hasNext()) {
            return this.iterator.next();
        } else {
            this.iterator = this.payloads.iterator();
        }
        return this.iterator.next();
    }

}
