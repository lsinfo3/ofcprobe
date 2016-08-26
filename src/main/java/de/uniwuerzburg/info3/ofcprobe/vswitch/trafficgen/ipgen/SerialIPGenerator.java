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
package de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.ipgen;

import de.uniwuerzburg.info3.ofcprobe.util.Util;

/**
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public class SerialIPGenerator implements IIpGenerator {

    private final static int IPVERSION = 4;
    private long lastIP = 0xfffffffeL;
    /**
     * The GeneratorType
     */
    private IPGeneratorType type;

    public SerialIPGenerator() {
        this.type = IPGeneratorType.SERIAL;
    }

    /* (non-Javadoc)
     * @see ofcprobe.vswitch.trafficgen.ipgen.IIpGenerator#getIpVersion()
     */
    @Override
    public int getIpVersion() {
        return IPVERSION;
    }

    /* (non-Javadoc)
     * @see ofcprobe.vswitch.trafficgen.ipgen.IIpGenerator#getIp()
     */
    @Override
    public byte[] getIp() {
        if (this.lastIP == 0x00000000L) {
            this.lastIP = 0xfffffffeL;
        }
        return Util.toByte((int) this.lastIP--, 4);
    }

    @Override
    public IPGeneratorType getType() {
        return this.type;
    }

}
