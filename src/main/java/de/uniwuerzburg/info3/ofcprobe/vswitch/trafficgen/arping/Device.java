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
package de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.arping;

import de.uniwuerzburg.info3.ofcprobe.vswitch.connection.IOFConnection;

/**
 * A Device reassembles the Tuple ofSwitch<->Port
 *
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public class Device {

    /**
     * The ofSwitch
     */
    private IOFConnection ofSwitch;
    /**
     * The Port
     */
    private short port;

    /**
     * Constructor
     *
     * @param ofSwitch the ofSwitch
     * @param port the Port
     */
    public Device(IOFConnection ofSwitch, short port) {
        this.ofSwitch = ofSwitch;
        this.port = port;
    }

    /**
     * @return the ofSwitch
     */
    public IOFConnection getOfSwitch() {
        return this.ofSwitch;
    }

    /**
     * @return the port
     */
    public short getPort() {
        return this.port;
    }

    /**
     * toString()
     *
     * @return
     */
    @Override
    public String toString() {
        return "Device(" + this.ofSwitch.toString() + ";" + this.port + ")";
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + port;
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Device other = (Device) obj;
        if (ofSwitch == null) {
            if (other.ofSwitch != null) {
                return false;
            }
        } else if (!ofSwitch.equals(other.ofSwitch)) {
            return false;
        }
        if (port != other.port) {
            return false;
        }
        return true;
    }
}
