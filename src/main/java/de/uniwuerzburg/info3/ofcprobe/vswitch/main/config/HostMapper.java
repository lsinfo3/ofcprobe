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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openflow.util.HexString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniwuerzburg.info3.ofcprobe.util.Util;
import de.uniwuerzburg.info3.ofcprobe.vswitch.connection.IOFConnection;
import de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.arping.Device;
import de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.ipgen.IIpGenerator;
import de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.ipgen.SerialIPGenerator;
import de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.macgen.IMacGen;
import de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.macgen.SerialMacGen;

import com.google.common.collect.HashBiMap;

/**
 * This Class tries to Map every free SwitchPort to a Device
 *
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public class HostMapper {

    /**
     * Debugger
     */
    private static final Logger logger = LoggerFactory.getLogger(HostMapper.class);

    /**
     * Connected IPGenerator
     */
    private IIpGenerator ipGen;
    /**
     * Connected MACGenerator
     */
    private IMacGen macGen;
    /**
     * MAP Device->IP
     */
    private Map<Device, List<String>> deviceToIP;
    /**
     * MAP IP->Device
     */
    private Map<String, Device> ipToDevice;
    /**
     * MAP Device->MAC
     */
    private Map<Device, List<String>> deviceToMac;
    /**
     * MAP MAC->Device
     */
    private Map<String, Device> macToDevice;
    /**
     * MAP MAC<->IP
     */
    private HashBiMap<String, String> macIPmap;
    /**
     * Saves which TargetIp has been used last for this IpSrc, if only some
     * TCPSyns are requested per Poll/Send Cyclus
     */
    private Map<String, Integer> ipCounter = new HashMap<>();

    /**
     * Config
     */
    private Config config;

    /**
     * Constructor
     *
     * @param config Overallconfiguration
     * @param ofSwitches
     */
    public HostMapper(Config config, List<IOFConnection> ofSwitches) {
        this.config = config;
        this.deviceToIP = new HashMap<>();
        this.ipToDevice = new HashMap<>();

        this.deviceToMac = new HashMap<>();
        this.macToDevice = new HashMap<>();

        this.ipCounter = new HashMap<>();

        this.macIPmap = HashBiMap.create();

        this.macGen = new SerialMacGen();
        this.ipGen = new SerialIPGenerator();

        logger.debug("Hostmapper initialized, setting up Stuff...");
        setupStuff(ofSwitches);
        logger.debug("Arping set up!");
    }

    /**
     * Fills the MAPs with Items, so every ofSwitch with freePorts gets an
     * IP/MAC for a faked "HOST"
     *
     * @param ofSwitches the ofSwitches
     */
    public void setupStuff(List<IOFConnection> ofSwitches) {
        List<Device> devices = new ArrayList<>();
        for (IOFConnection ofSwitch : ofSwitches) {
            List<Short> freePorts;
            if (this.config.getTrafficGenConfig().getOnlyOneHostPerSwitch()) {
                freePorts = this.config.getTopology().getFreePorts(ofSwitch.getDpid(), 1);
            } else {
                freePorts = this.config.getTopology().getFreePorts(ofSwitch.getDpid(), -1);
            }
            logger.trace("Switch " + String.valueOf(ofSwitch.getDpid()) + "   free Ports: " + String.valueOf(freePorts.size()));

            int initializedHosts = 0;
            while (initializedHosts < this.config.getTrafficGenConfig().getHostsPerSwitch()) {
                for (short port : freePorts) {
                    Device portSwitch = new Device(ofSwitch, port);
                    byte[] macByte = this.macGen.getMac();
                    String mac = HexString.toHexString(macByte);
                    byte[] ipByte = this.ipGen.getIp();
                    String ip = Util.fromIPv4Address(Util.toIPv4Address((ipByte)));
                    devices.add(portSwitch);

                    List<String> macs = this.deviceToMac.get(portSwitch);
                    if (macs == null) {
                        macs = new ArrayList<>();
                    }
                    macs.add(mac);
                    this.deviceToMac.put(portSwitch, macs);
                    this.macToDevice.put(mac, portSwitch);

                    List<String> ips = this.deviceToIP.get(portSwitch);
                    if (ips == null) {
                        ips = new ArrayList<>();
                    }
                    ips.add(ip);
                    this.deviceToIP.put(portSwitch, ips);
                    this.ipToDevice.put(ip, portSwitch);

                    this.macIPmap.put(mac, ip);
                    initializedHosts += freePorts.size();
                }

            }
        }

        logger.trace("#Devices: " + devices.size());
        logger.trace("DeviceToIPMap: {}", deviceToIP.toString());
        logger.trace("IPtoDeviceMap: {}", ipToDevice.toString());
        logger.trace("DevicetoMacMap: {}", deviceToMac.toString());
        logger.trace("MacToDeviceMap: {}", macToDevice.toString());
        logger.trace("Mac<->IP BiMap: {} ", macIPmap.toString());
    }

    /**
     * Gets IP hanging on the Device
     *
     * @param device the Device
     * @return the IP
     */
    public List<String> getIpsToDevice(Device device) {
        return this.deviceToIP.get(device);
    }

    /**
     * Gets the Device on which IP is hanging on
     *
     * @param ip the IP
     * @return the Device
     */
    public Device getDeviceToIp(String ip) {
        return this.ipToDevice.get(ip);
    }

    /**
     * Gets the MAC hanging on the Device
     *
     * @param device the Device
     * @return the MAC
     */
    public List<String> getMacsToDevice(Device device) {
        return this.deviceToMac.get(device);
    }

    /**
     * Gets the Device on which MAC is hanging on
     *
     * @param mac the MAC
     * @return the Device
     */
    public Device getDeviceToMac(String mac) {
        return this.macToDevice.get(mac);
    }

    /**
     * Gets the Mac to a IP
     *
     * @param ip the IP
     * @return the Mac
     */
    public String getMacToIp(String ip) {
        return this.macIPmap.inverse().get(ip);
    }

    /**
     * Gets the IP to a MAC
     *
     * @param mac the MAC
     * @return the IP
     */
    public String getIpToMac(String mac) {
        return this.macIPmap.get(mac);
    }

    /**
     * Gets possible ARP Targets for a IP
     *
     * @param srcIP the IP
     * @param count number of TargetIps
     * @return the Targets
     */
    public List<String> getTargetsForIP(String srcIP, int count) {
        List<String> targetIPs = new ArrayList<>(this.macIPmap.inverse().keySet());
        targetIPs.remove(srcIP);

        List<String> output = new ArrayList<>(targetIPs);

        if (count < targetIPs.size()) {
            Integer counter = this.ipCounter.get(srcIP);
            if (counter == null) {
                counter = 0;
            }

            for (int i = 0; i < count; i++) {
                int modIndex = (counter + i) % targetIPs.size();
                output.add(targetIPs.get(modIndex));
                counter++;
            }

            //store updated counter
            this.ipCounter.put(srcIP, counter);

        } else {
            output = targetIPs;
        }

        return output;
    }

}
