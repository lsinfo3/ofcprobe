/**
 * 
 */
package ofcprobe.vswitch.trafficgen.portgen;


import ofcprobe.util.Util;

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
