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
package de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.macgen;

import de.uniwuerzburg.info3.ofcprobe.util.Util;

/**
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public class SerialMacGen implements IMacGen {

	private long lastMAC = 0xeeeeeeeeeeeeL;
	/**
	 * The GeneratorType
	 */
	private MACGeneratorType type;
	private static final int MACAddress_WIDTH = 6; 

	public SerialMacGen(){
		this.type = MACGeneratorType.SERIAL;
	}
	/* (non-Javadoc)
	 * @see ofcprobe.vswitch.trafficgen.macgen.IMacGen#getMac()
	 */
	@Override
	public byte[] getMac() {
		if (this.lastMAC  == 0x000000000000L) {
			this.lastMAC = 0xeeeeeeeeeeeeL;
		}
		byte[] output = new byte[6];
	    Util.insertLong(output, this.lastMAC--, 0, MACAddress_WIDTH);
		
		return output;
	}

	@Override
	public MACGeneratorType getType() {
		return this.type;
	}

}
