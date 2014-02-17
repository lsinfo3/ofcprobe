/**
 * 
 */
package ofcprobe.vswitch.trafficgen.portgen;

/**
 * Interface for PortGenerators
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public interface IPortGen {
	
	/**
	 * Generate a new Port
	 * @return Port
	 */
	public byte[] getPort();
	
	/**
	 * Generate a new Privileged Port.
	 * @return Port
	 */
	public byte[] getPrivilegdedPort();
	
	
	/**
	 * Gets the Port Generator Type
	 * @return the Type
	 */
	public PortGeneratorType getType();
}
