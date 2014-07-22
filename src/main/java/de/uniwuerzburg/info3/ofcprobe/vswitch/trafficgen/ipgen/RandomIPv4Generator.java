/**
 * 
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
