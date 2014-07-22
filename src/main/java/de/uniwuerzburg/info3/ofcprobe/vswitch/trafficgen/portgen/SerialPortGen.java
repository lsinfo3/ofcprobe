/**
 * 
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
