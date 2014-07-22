/**
 * 
 */
package de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.macgen;

import de.uniwuerzburg.info3.ofcprobe.util.Util;


/**
 * Random Mac Generator .
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public class RandomMacGen implements IMacGen {
	
//	private List<Long> usedMacs;
	/**
	 * The GeneratorType
	 */
	private MACGeneratorType type;

	/**
	 * Constructor.
	 */
	public RandomMacGen(){
		this.type = MACGeneratorType.RANDOM;
//		this.usedMacs = new ArrayList<Long>();
	}

	/* (non-Javadoc)
	 * @see ofcprobe.vswitch.trafficgen.macgen.IMacGen#getMac()
	 */
	@Override
	public byte[] getMac() {
		int MACAddress_WIDTH = 6; 
		long mac = (long)(0xffffffffffffL * Math.random());
		
		
//		while(this.usedMacs.contains(mac)) {
//			mac = (long)(0xffffffffffffL * Math.random());
//		}
//		this.usedMacs.add(mac);
		
		byte[] output = new byte[6];
	    Util.insertLong(output, mac, 0, MACAddress_WIDTH);
		
		return output;
	}

	@Override
	public MACGeneratorType getType() {
		return this.type;
	}

}
