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
public class RandomIPv4Generator implements IIpGenerator {

    private final static int IPVERSION = 4;
//	private List<Integer> usedIps;
    /**
     * The GeneratorType
     */
    private IPGeneratorType type;

    public RandomIPv4Generator() {
        this.type = IPGeneratorType.RANDOM;
//		this.usedIps = new ArrayList<Integer>();
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
        int ip = (int) (0xffffffffL * Math.random());
//		while(this.usedIps.contains(ip)){
//			ip = (int)(0xffffffffL * Math.random());
//		}
//		this.usedIps.add(ip);

        return Util.toByte(ip, 4);
    }

    @Override
    public IPGeneratorType getType() {
        return this.type;
    }
}
