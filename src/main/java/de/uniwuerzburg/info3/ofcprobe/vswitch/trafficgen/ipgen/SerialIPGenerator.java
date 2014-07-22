/**
 * 
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
	
	public SerialIPGenerator(){
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
		return Util.toByte((int)this.lastIP--,4);
	}

	@Override
	public IPGeneratorType getType() {
		return this.type;
	}
	

	
}
