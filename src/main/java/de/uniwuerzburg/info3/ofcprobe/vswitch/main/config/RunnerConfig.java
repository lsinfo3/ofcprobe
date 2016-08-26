/* 
 * Copyright 2016 christopher.metter.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
