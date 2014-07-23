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
package de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.portgen;


import de.uniwuerzburg.info3.ofcprobe.util.Util;

/**
 * Random Port Generator
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
	public RandomPortGen(){
		this.type = PortGeneratorType.RANDOM;
	}
	/* (non-Javadoc)
	 * @see ofcprobe.vswitch.trafficgen.portgen.IPortGen#getPort()
	 */
	@Override
	public byte[] getPort() {
		return Util.toByte((int)(Math.random() * IPPort_MASK),4);
	}

	/* (non-Javadoc)
	 * @see ofcprobe.vswitch.trafficgen.portgen.IPortGen#getPrivilegdedPort()
	 */
	@Override
	public byte[] getPrivilegdedPort() {
		return Util.toByte((int)(Math.random() * IPPort_LIMIT_PRIVILEGED),4);
	}

	@Override
	public PortGeneratorType getType() {
		return this.type;
	}

}
