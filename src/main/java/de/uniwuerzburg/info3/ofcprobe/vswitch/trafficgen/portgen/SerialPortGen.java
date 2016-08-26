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

import java.nio.ByteBuffer;

/**
 * Random Port Generator
 *
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public class SerialPortGen implements IPortGen {

    private ByteBuffer byteBuff = ByteBuffer.allocate(2);

    private short lastPort = 0;
    /**
     * The GeneratorType
     */
    private PortGeneratorType type;

    public SerialPortGen() {
        this.type = PortGeneratorType.SERIAL;
    }
    /* (non-Javadoc)
     * @see ofcprobe.vswitch.trafficgen.portgen.IPortGen#getPort()
     */

    @Override
    public byte[] getPort() {
        return toByte(this.lastPort++);
    }

    /* (non-Javadoc)
     * @see ofcprobe.vswitch.trafficgen.portgen.IPortGen#getPrivilegdedPort()
     */
    @Override
    public byte[] getPrivilegdedPort() {
        return getPort();
    }

    /**
     * HelperMethod to Convert short to byte[]
     *
     * @param input
     * @return byte[]
     */
    private byte[] toByte(short input) {
        this.byteBuff.clear();
        return this.byteBuff.putShort(input).array();

    }

    @Override
    public PortGeneratorType getType() {
        return this.type;
    }

}
