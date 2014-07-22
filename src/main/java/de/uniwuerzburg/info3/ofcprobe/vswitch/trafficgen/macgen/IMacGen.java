/**
 * 
 */
package de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.macgen;

/**
 * Interface for Mac Generators.
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public interface IMacGen {
	
	/**
	 * Generates a new Mac Address
	 * @return the mac in a byte[] 
	 */
	public byte[] getMac();
	
	
	/**
	 * Gets the MAC Generator Type
	 * @return the Type
	 */
	public MACGeneratorType getType();
	

}
