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
package de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.portgen;

import de.uniwuerzburg.info3.ofcprobe.util.Util;

/**
 * Random Port Generator
 *
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public class RandomPortGen implements IPortGen {

    /**
     * Number of well-known (aka privileged) ports.
     */
    private static final int IPPort_LIMIT_PRIVILEGED = 1024;
    /**
     * IP port mask.
     */
    private static final long IPPort_MASK = 0xffff;

    /**
     * The GeneratorType
     */
    private PortGeneratorType type;

    /**
     * Constructor
     */
    public RandomPortGen() {
        this.type = PortGeneratorType.RANDOM;
    }
    /* (non-Javadoc)
     * @see ofcprobe.vswitch.trafficgen.portgen.IPortGen#getPort()
     */

    @Override
    public byte[] getPort() {
        return Util.toByte((int) (Math.random() * IPPort_MASK), 4);
    }

    /* (non-Javadoc)
     * @see ofcprobe.vswitch.trafficgen.portgen.IPortGen#getPrivilegdedPort()
     */
    @Override
    public byte[] getPrivilegdedPort() {
        return Util.toByte((int) (Math.random() * IPPort_LIMIT_PRIVILEGED), 4);
    }

    @Override
    public PortGeneratorType getType() {
        return this.type;
    }

}
