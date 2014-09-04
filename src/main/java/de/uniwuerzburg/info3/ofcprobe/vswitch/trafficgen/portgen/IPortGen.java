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
package de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.portgen;

/**
 * Interface for PortGenerators
 *
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public interface IPortGen {

    /**
     * Generate a new Port
     *
     * @return Port
     */
    public byte[] getPort();

    /**
     * Generate a new Privileged Port. Privileged means that generated Port will
     * be one of the commonly known ports, e.g. Port 80 for HTTP
     *
     * @return Port
     */
    public byte[] getPrivilegdedPort();

    /**
     * Gets the Port Generator Type
     *
     * @return the Type
     */
    public PortGeneratorType getType();
}
