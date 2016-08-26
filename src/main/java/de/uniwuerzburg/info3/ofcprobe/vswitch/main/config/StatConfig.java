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

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * The Configuration for a ofcprobe.vswitch.statistics.IStatistics Module
 *
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
    public StatConfig() {
        this.snmpAddress = new InetSocketAddress("127.0.0.1", 6633);
        this.stats = new ArrayList<>();
    }

    /**
     * Sets the Monitoringaddress
     *
     * @param address the address
     */
    public void setMonitorAddress(InetSocketAddress address) {
        this.snmpAddress = address;
    }

    /**
     * Gets the MonitoringAddress
     *
     * @return the address
     */
    public InetSocketAddress getMonitorAddress() {
        return this.snmpAddress;
    }

    /**
     * Set the StatModules
     *
     * @param stats the StatModulesStringList
     */
    public void setStatModules(List<String> stats) {
        if (stats != null) {
            this.stats = stats;
        }
    }

    /**
     * Get StatModulesStringList
     *
     * @return the List
     */
    public List<String> getStatModules() {
        return this.stats;
    }

    /**
     * String Representation
     *
     * @return
     */
    @Override
    public String toString() {
        String output = "StatConfig: MonitoringAddress=" + this.snmpAddress.toString() + "; StatModules=" + this.stats.toString();
        return output;
    }

}
