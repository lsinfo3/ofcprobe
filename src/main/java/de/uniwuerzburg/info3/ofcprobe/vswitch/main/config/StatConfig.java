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
package de.uniwuerzburg.info3.ofcprobe.vswitch.main.config;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * The Configuration for a ofcprobe.vswitch.statistics.IStatistics Module
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public class StatConfig {
	
	/**
	 * The Address to monitor per SNMP
	 */
	private InetSocketAddress snmpAddress;
	/**
	 * String Array with eath line being a StatModule to be initialized
	 */
	private List<String> stats;
	
	/**
	 * Constructor with default values.
	 */
	public StatConfig(){
		this.snmpAddress = new InetSocketAddress("127.0.0.1", 6633);
		this.stats = new ArrayList<String>();
	}
	
	/**
	 * Sets the Monitoringaddress
	 * @param address the address
	 */
	public void setMonitorAddress(InetSocketAddress address) {
		this.snmpAddress = address;
	}
	
	/**
	 * Gets the MonitoringAddress
	 * @return the address
	 */
	public InetSocketAddress getMonitorAddress(){
		return this.snmpAddress;
	}
	
	/**
	 * Set the StatModules
	 * @param stats the StatModulesStringList
	 */
	public void setStatModules(List<String> stats){
		if (stats != null) {
			this.stats=stats;
		}
	}
	
	/**
	 * Get StatModulesStringList
	 * @return the List
	 */
	public List<String> getStatModules(){
		return this.stats;
	}
	
	/**
	 * String Representation
	 */
	public String toString(){
		String output = "StatConfig: MonitoringAddress=" + this.snmpAddress.toString() + "; StatModules=" + this.stats.toString();
		return output;
	}

}
