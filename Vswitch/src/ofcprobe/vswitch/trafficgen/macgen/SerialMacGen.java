/**
 * 
 */
package ofcprobe.vswitch.trafficgen.macgen;

import ofcprobe.util.Util;

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
