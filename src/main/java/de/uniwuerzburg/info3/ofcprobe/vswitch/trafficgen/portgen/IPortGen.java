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
