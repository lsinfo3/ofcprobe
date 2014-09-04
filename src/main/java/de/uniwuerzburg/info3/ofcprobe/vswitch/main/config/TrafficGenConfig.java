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

import de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.ipgen.IPGeneratorType;
import de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.macgen.MACGeneratorType;
import de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.portgen.PortGeneratorType;

/**
 * Configuration of a ofcprobe.vswitch.trafficgen.TrafficGen Module
 *
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public class TrafficGenConfig {

    /**
     * The time between two RefillEvents
     */
    private int iat;
    /**
     * Use a static Payload?
     */
    private boolean staticPayload;
    /**
     * Fill every queue up to this threshold
     */
    private int threshold;
    /**
     * Number of Payloads generated per Event
     */
    private int countPerEvent;
    private String scenario;
    private boolean switchHasSettings;
    private boolean arpEnabled;
    private MACGeneratorType macGenType;
    private IPGeneratorType ipGenType;
    private PortGeneratorType portGenType;
    private int iatType;
    private String distriType;
    private Double distriPara1;
    private Double distriPara2;
    private boolean onlyTopoPayloads;

    /**
     * Constructor with default values.
     */
    public TrafficGenConfig() {
        this.iat = 100;
        this.staticPayload = false;
        this.threshold = 100;
        this.countPerEvent = 1;
        this.scenario = "TCPSYN";
        this.switchHasSettings = false;
        this.arpEnabled = false;
        this.macGenType = MACGeneratorType.SERIAL;
        this.ipGenType = IPGeneratorType.SERIAL;
        this.portGenType = PortGeneratorType.SERIAL;
        this.iatType = 0;
        this.distriType = "none";
        this.distriPara1 = 0.0;
        this.distriPara2 = 0.0;
        this.onlyTopoPayloads = false;
    }

    /**
     * Set the time between two refill-events
     *
     * @param iat the time between two refill-events
     */
    public void setIAT(int iat) {
        this.iat = iat;
    }

    /**
     * Get the time between two refill-events
     *
     * @return the time between two refill-events
     */
    public int getIAT() {
        return this.iat;
    }

    /**
     * Set Send static Payload
     *
     * @return true -> always queueing the same (once generated) Payload
     */
    public boolean getStaticPayloadFlag() {
        return this.staticPayload;
    }

    /**
     * Get Send static Payload?
     *
     * @param statFlag true -> always queueing the same (once generated) Payload
     */
    public void setStaticPayloadFlag(boolean statFlag) {
        this.staticPayload = statFlag;
    }

    /**
     * Sets: Fill every queue up to this threshold
     *
     * @param thres Fill every queue up to this threshold
     */
    public void setFillThreshold(int thres) {
        this.threshold = thres;
    }

    /**
     * Gets: Fill every queue up to this threshold
     *
     * @return Fill every queue up to this threshold
     */
    public int getFillThreshold() {
        return this.threshold;
    }

    /**
     * Sets: Number of Payloads generated per Event
     *
     * @param cnt Number of Payloads generated per Event
     */
    public void setCountPerEvent(int cnt) {
        this.countPerEvent = cnt;
    }

    /**
     * Gets: Number of Payloads generated per Event
     *
     * @return Number of Payloads generated per Event
     */
    public int getCountPerEvent() {
        return this.countPerEvent;
    }

    /**
     * Gets the Scenario
     *
     * @return
     */
    public String getScenario() {
        return this.scenario;
    }

    /**
     * Sets the Scenario
     *
     * @param scenario the String
     */
    public void setScenario(String scenario) {
        this.scenario = scenario;
    }

    /**
     * Sets whether switches have individual IAT/FillThreshold Settings
     *
     * @param flag the Flag
     */
    public void setSwitchHasIndividualSetting(boolean flag) {
        this.switchHasSettings = flag;
    }

    /**
     * Gets whether switches have individual IAT/FillThreshold Settings
     *
     * @return
     */
    public boolean getSwitchHasIndividualSetting() {
        return this.switchHasSettings;
    }

    /**
     * Sets arp enabled flag
     *
     * @param flag
     */
    public void setArpEnabled(boolean flag) {
        this.arpEnabled = flag;
    }

    /**
     * Gets ARP enabled flag
     *
     * @return
     */
    public boolean getArpFlag() {
        return this.arpEnabled;
    }

    /**
     * Gets MACGeneratorType
     *
     * @return the MACGeneratorType
     */
    public MACGeneratorType getMacGenType() {
        return this.macGenType;
    }

    /**
     * Gets the IPGeneratorType
     *
     * @return the IPGeneratorType
     */
    public IPGeneratorType getIPGenType() {
        return this.ipGenType;
    }

    /**
     * Gets the PortGeneratorType
     *
     * @return the PortGeneratorType
     */
    public PortGeneratorType getPortGenType() {
        return this.portGenType;
    }

    /**
     * Sets the MACGeneratorType
     *
     * @param type the MACGeneratorType
     */
    public void setMACGeneratorType(MACGeneratorType type) {
        if (type != null) {
            this.macGenType = type;
        }
    }

    /**
     * Sets the IPGeneratorType
     *
     * @param type the IPGeneratorType
     */
    public void setIPGeneratorType(IPGeneratorType type) {
        if (type != null) {
            this.ipGenType = type;
        }
    }

    /**
     * Sets the PortGeneratorType
     *
     * @param type the PortGeneratorType
     */
    public void setPortGeneratorType(PortGeneratorType type) {
        if (type != null) {
            this.portGenType = type;
        }
    }

    /**
     * String representation
     *
     * @return
     */
    @Override
    public String toString() {
        double targetPackets = (1000.0 / this.iat) * this.threshold * this.countPerEvent;
        String output = "TrafficGenConfig: Scenario=" + this.scenario + "; Arping=" + this.arpEnabled + "; fillThreshold=" + this.threshold
                + "; IAT=" + this.iat + "; countPerEvent=" + this.countPerEvent + "; TargetGeneratedPackets=" + targetPackets
                + "; Static Payload=" + this.staticPayload + "; individualSwitchSettings=" + this.switchHasSettings
                + "; MACGenType=" + this.macGenType + "; IPGenType=" + this.ipGenType + "; PortGenType=" + this.portGenType;
        return output;
    }

    public void setIatType(int type) {
        this.iatType = type;
    }

    public void setDistribution(String distri) {
        this.distriType = distri;
    }

    public void setDistributionPara1(Double para1) {
        this.distriPara1 = para1;
    }

    public void setDistributionPara2(Double para2) {
        this.distriPara2 = para2;
    }

    public int getIatType() {
        return this.iatType;
    }

    public String getDistribution() {
        return this.distriType;
    }

    public double getDistributionPara1() {
        return this.distriPara1;
    }

    public double getDistributionPara2() {
        return this.distriPara2;
    }

    public void setOnlyTopoPayloads(boolean flag) {
        this.onlyTopoPayloads = flag;
    }

    public boolean getOnlyTopoPayloads() {
        return this.onlyTopoPayloads;
    }
}
