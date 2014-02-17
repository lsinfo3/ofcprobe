/**
 * 
 */
package ofcprobe.vswitch.trafficgen.ipgen;

/**
 * Interface for IPGenerators
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public interface IIpGenerator {
	
	/**
	 * The IP Version used by this Generator
	 * @return IPversion Used
	 */
	public int getIpVersion();
	
	/**
	 * Get Yourself a new IP
	 * @return ip as byte[]
	 */
	public byte[] getIp();
	
	/**
	 * Gets the Type
	 * @return
	 */
	public IPGeneratorType getType();
}
