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

/**
 * Configuration for the ofcprobe.vswitch.runner.OFSwitchRunner
 *
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public class RunnerConfig {

    /**
     * DPID to start with
     */
    private int startdpid;
    /**
     * Number of Switches for this instance
     */
    private int countswitches;

    /**
     * Constructor with Default Values
     */
    public RunnerConfig() {
        this.countswitches = 1;
        this.startdpid = 1;
    }

    /**
     * Get DPID of first initiated ofSwitch
     *
     * @return DPID of first ofSwitch
     */
    public int getStartDpid() {
        return this.startdpid;
    }

    /**
     * Get Number of ofSwitches to instantiate
     *
     * @return the nuber
     */
    public int getCountswitches() {
        return this.countswitches;
    }

    /**
     * Set the DPID of first initiated ofSwitch
     *
     * @param startDpid
     */
    public void setStartDpid(int startDpid) {
        this.startdpid = startDpid;
    }

    /**
     * Set the Number of ofSwitches to instantiate
     *
     * @param count the number
     */
    public void setCountSwitches(int count) {
        this.countswitches = count;
    }

    @Override
    public String toString() {
        String output = "RunnerConfig: StartDPID=" + this.startdpid + "; CountSwitches=" + this.countswitches;
        return output;
    }

}
