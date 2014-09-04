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
