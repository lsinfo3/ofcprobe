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
public class RandomIPv4Generator implements IIpGenerator {
	
	private final static int IPVERSION = 4;
//	private List<Integer> usedIps;
	/**
	 * The GeneratorType
	 */
	private IPGeneratorType type;
	
	public RandomIPv4Generator(){
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
		int ip = (int)(0xffffffffL * Math.random());
//		while(this.usedIps.contains(ip)){
//			ip = (int)(0xffffffffL * Math.random());
//		}
//		this.usedIps.add(ip);
		
		return Util.toByte(ip,4);
	}

	@Override
	public IPGeneratorType getType() {
		return this.type;
	}

}
