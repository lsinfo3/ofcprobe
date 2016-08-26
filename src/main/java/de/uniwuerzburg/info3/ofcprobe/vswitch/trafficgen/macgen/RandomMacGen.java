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
package de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.macgen;

import de.uniwuerzburg.info3.ofcprobe.util.Util;

/**
 * Random Mac Generator .
 *
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public class RandomMacGen implements IMacGen {
//	private List<Long> usedMacs;

    /**
     * The GeneratorType
     */
    private MACGeneratorType type;

    /**
     * Constructor.
     */
    public RandomMacGen() {
        this.type = MACGeneratorType.RANDOM;
//		this.usedMacs = new ArrayList<Long>();
    }

    /* (non-Javadoc)
     * @see ofcprobe.vswitch.trafficgen.macgen.IMacGen#getMac()
     */
    @Override
    public byte[] getMac() {
        int MACAddress_WIDTH = 6;
        long mac = (long) (0xffffffffffffL * Math.random());

//		while(this.usedMacs.contains(mac)) {
//			mac = (long)(0xffffffffffffL * Math.random());
//		}
//		this.usedMacs.add(mac);
        byte[] output = new byte[6];
        Util.insertLong(output, mac, 0, MACAddress_WIDTH);

        return output;
    }

    @Override
    public MACGeneratorType getType() {
        return this.type;
    }

}
