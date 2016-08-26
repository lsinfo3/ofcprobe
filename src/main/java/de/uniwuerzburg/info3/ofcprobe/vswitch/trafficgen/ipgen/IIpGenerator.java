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
package de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.ipgen;

/**
 * Interface for IPGenerators
 *
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public interface IIpGenerator {

    /**
     * The IP Version used by this Generator
     *
     * @return IPversion Used
     */
    public int getIpVersion();

    /**
     * Get Yourself a new IP
     *
     * @return ip as byte[]
     */
    public byte[] getIp();

    /**
     * Gets the Type
     *
     * @return
     */
    public IPGeneratorType getType();
}
