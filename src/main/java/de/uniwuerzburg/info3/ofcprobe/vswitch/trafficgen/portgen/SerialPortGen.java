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

import java.nio.ByteBuffer;


/**
 * Random Port Generator
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

	
	public SerialPortGen(){
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
     * @param input
     * @return byte[]
     */
    private byte[] toByte(short input){
    	this.byteBuff.clear();
    	return this.byteBuff.putShort(input).array();
    			
    }
	@Override
	public PortGeneratorType getType() {
		return this.type;
	}
    
}
