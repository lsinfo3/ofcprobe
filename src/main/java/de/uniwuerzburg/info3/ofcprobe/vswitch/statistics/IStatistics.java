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
package de.uniwuerzburg.info3.ofcprobe.vswitch.statistics;

import org.openflow.protocol.OFMessage;

/**
 * Interface for Statistic Modules, every written Module has to have these Methods.
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public interface IStatistics {
	
	/**
	 * Defines the targetFile
	 * @param file outputfile
	 */
	public void setReportFile(String file);
	
	/**
	 * Incoming Packet FROM the Controller
	 * @param in OFMessage from Controller
	 */
	public void packetIn(OFMessage in);
	
	/**
	 * Outgoing Packet TO the Controller
	 * @param out OFMessage to Controller
	 */
	public void packetOut(OFMessage out);
	
	/**
	 * Evaluate Modules Statistics and produce results
	 */
	public void evaluate();
	
	/**
	 * Do tha reportin'
	 */
	public void report();

	/**
	 * Session now Started
	 */
	public void start();
	
	/**
	 * Session now over
	 */
	public void stop();
}
