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
