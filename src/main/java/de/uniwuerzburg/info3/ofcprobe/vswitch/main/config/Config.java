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

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import de.uniwuerzburg.info3.ofcprobe.vswitch.graphml.GraphmlParser;
import de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.ipgen.IPGeneratorType;
import de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.macgen.MACGeneratorType;
import de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.portgen.PortGeneratorType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a Meta-Config. It holds the Configuration for the ofSwitchRunner,
 * ofSwitches and concording StatisticModules and finally the TrafficGenerator.
 *
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public class Config {

    /**
     * The debug instance for this object
     */
    private static final Logger debug = LoggerFactory.getLogger(Config.class);

    /**
     * The OpenFlow Version Used
     */
    private int openflow_version;
    /**
     * The Topology
     */
    private Topology topology;
    /**
     * Configuration for the OFSwitchRunner
     */
    private RunnerConfig runConfig;
    /**
     * Configuration for the ofSwitches
     */
    private SwitchConfig switchConfig;
    /**
     * Configuration for the Statistic Modules
     */
    private StatConfig statConfig;
    /**
     * Configuration for the TrafficGen Modules
     */
    private TrafficGenConfig trafficGenConfig;
    /**
     * Length of a BenchingSession
     */
    private int simTime;
    /**
     * Delay between Connection establishment and First Sent PacketIN Msgs
     */
    private long startDelay;
    /**
     * Maximum Wait for incoming PacketIns msgs after Sessionend
     */
    private long stopDelay;
    /**
     * Flag if each Switch should check for Indivdual Settings
     */
    private boolean checkForIndividualSettings;
    /**
     * Flag if Topology Emulation File should be loaded
     */
    private boolean hasTopology;
    /**
     * Starting DPID of first initialized Switch
     */
    private int startDpid;
    /**
     * Count of to Switches to initialize
     */
    private int countSwitches;
    /**
     * Count of started SwitchThreads
     */
    private int threadCount;

    private String graphml_file;

    private GraphmlParser graphmlparser;

    private boolean hasGraphml;

    private String individualSwitchSettingsFile;

    private boolean isOnosControlled;

    /**
     * Constructor
     *
     * @param filename full path to the Configuration-File
     * @param switchCount override switchCount from commandLine argument
     */
    public Config(String filename, int switchCount) {
        // Initialize Configurations with Default Values
        this.runConfig = new RunnerConfig();
        this.switchConfig = new SwitchConfig();
        this.statConfig = new StatConfig();
        this.trafficGenConfig = new TrafficGenConfig();
        this.individualSwitchSettingsFile = "ofSwitch.ini";
        // Check for correct filename
        if (filename.equals("")) {
            // no filename -> load Default
            debug.info("Loading Default Settings");
            loadDefaults();
        } else {
            // correct filename -> load Config from File
            loadConfig(filename);
        }
        if (switchCount != -1) {
            this.countSwitches = switchCount;
        }
        if (this.hasGraphml && graphml_file != "") {
            debug.info("Topolgy File: {}", graphml_file);
            this.graphmlparser = new GraphmlParser(graphml_file);
            graphmlparser.readNodes();
            graphmlparser.readEdges();
            graphmlparser.writeToTopologyFile();
            this.countSwitches = graphmlparser.getNodeCount();
            debug.info("Switch Count in current Topology: {}", this.countSwitches);
        }

        if (this.hasTopology) {
            this.topology = new Topology(this);
            this.topology.loadTopoFromFile();
        }

    }

    /**
     * Loads vSwitch Config from provided File
     *
     * @param configfile File with vSwitch configuration in it- XML Style
     */
    private void loadConfig(String configfile) {
        Properties props = new Properties();
        try {
            props.load(new BufferedInputStream(new FileInputStream(configfile)));
            // Overall Configuration Options
            this.openflow_version = Integer.parseInt(props.getProperty("config.openflow_version", "1"));
            this.simTime = Integer.parseInt(props.getProperty("config.simTime", "60500"));
            this.startDelay = Long.parseLong(props.getProperty("config.startDelay", "6000"));
            this.stopDelay = Long.parseLong(props.getProperty("config.stopDelay", "6000"));
            this.hasTopology = Boolean.parseBoolean(props.getProperty("config.hasTopology", "false"));
            this.hasGraphml = Boolean.parseBoolean(props.getProperty("config.hasGraphml", "false"));
            this.graphml_file = props.getProperty("config.graphml", "");
            this.checkForIndividualSettings = Boolean.parseBoolean(props.getProperty("config.checkForIndividualSwitchSettings", "false"));
            this.individualSwitchSettingsFile = props.getProperty("config.individualFileName", "ofSwitch.ini");
            this.isOnosControlled = Boolean.parseBoolean(props.getProperty("config.onosControlled", "false"));

            String controllerAddress = props.getProperty("config.controllerAddress", "127.0.0.1");
            int controllerPort = Integer.parseInt(props.getProperty("config.controllerPort", "6633"));
            this.switchConfig.setContAddress(new InetSocketAddress(controllerAddress, controllerPort));
            this.startDpid = (Integer.parseInt(props.getProperty("config.startDpid", "1")));
            this.countSwitches = (Integer.parseInt(props.getProperty("config.switchCount", "1")));
            this.threadCount = (Integer.parseInt(props.getProperty("config.threadCount", "1")));

            // Switch Specific Configuration Options
            this.switchConfig.setPortCountperSwitch(Integer.parseInt(props.getProperty("switchConfig.portCountPerSwitch", "4")));
            this.switchConfig.setBuffersPerSwitch(Integer.parseInt(props.getProperty("switchConfig.buffersPerSwitch", "256")));
            this.switchConfig.setSession(this.countSwitches);
            this.switchConfig.setSendFlag(Boolean.parseBoolean(props.getProperty("switchConfig.sendFlag", "true")));
            this.switchConfig.setDisableNagle(Boolean.parseBoolean(props.getProperty("switchConfig.disableNagle", "true")));
            this.switchConfig.setBatchSending(Boolean.parseBoolean(props.getProperty("switchConfig.batchSending", "true")));
            this.switchConfig.setFlowTableSize(Integer.parseInt(props.getProperty("switchConfig.flowTableSize", "128")));
            this.switchConfig.setRandomizeStats(Boolean.parseBoolean(props.getProperty("switchConfig.randomizeStats", "true")));

            // TrafficGenerator Specific Configuration Options
            this.trafficGenConfig.setScenario(props.getProperty("trafficGenConfig.scenario", "TCPSYN"));
            if (this.hasTopology) {
                this.trafficGenConfig.setArpEnabled(Boolean.parseBoolean(props.getProperty("trafficGenConfig.arpEnabled", "false")));
            }
            this.trafficGenConfig.setFillThreshold(Integer.parseInt(props.getProperty("trafficGenConfig.fillThreshold", "500")));
            this.trafficGenConfig.setIAT(Integer.parseInt(props.getProperty("trafficGenConfig.IAT", "100")));
            this.trafficGenConfig.setCountPerEvent(Integer.parseInt(props.getProperty("trafficGenConfig.countPerEvent", "1")));
            this.trafficGenConfig.setStaticPayloadFlag(Boolean.parseBoolean(props.getProperty("trafficGenConfig.staticPayload", "false")));
            this.trafficGenConfig.setOnlyTopoPayloads(Boolean.parseBoolean(props.getProperty("trafficGenConfig.onlyTopologyPayloads", "false")));
            this.trafficGenConfig.setOnlyOneHostPerSwitch(Boolean.parseBoolean(props.getProperty("trafficGenConfig.onlyOneHostPerSwitch", "false")));
            this.trafficGenConfig.setSwitchHasIndividualSetting(this.checkForIndividualSettings);
            this.trafficGenConfig.setMACGeneratorType(parseMACGenType(props.getProperty("trafficGenConfig.generatorTypeMAC", "SERIAL")));
            this.trafficGenConfig.setIPGeneratorType(parseIPGenType(props.getProperty("trafficGenConfig.generatorTypeIP", "SERIAL")));
            this.trafficGenConfig.setPortGeneratorType(parsePortGenType(props.getProperty("trafficGenConfig.generatorTypePort", "SERIAL")));
            this.trafficGenConfig.setIatType(Integer.parseInt(props.getProperty("trafficGenConfig.iatType", "0")));
            this.trafficGenConfig.setDistribution(props.getProperty("trafficGenConfig.iatDistribution", "Normal"));
            this.trafficGenConfig.setDistributionPara1(Double.parseDouble(props.getProperty("trafficGenConfig.iatDistributionParamter1", "0.0")));
            this.trafficGenConfig.setDistributionPara2(Double.parseDouble(props.getProperty("trafficGenConfig.iatDistributionParamter2", "0.0")));

            // StatisticModules
            this.statConfig.setMonitorAddress(new InetSocketAddress(controllerAddress, controllerPort));
            String[] statModules = props.getProperty("statsConfig.modules", "PPS,RTT,CPU,RAM,TSL").split(",");
            this.statConfig.setStatModules(Arrays.asList(statModules));

        } catch (FileNotFoundException e) {
            debug.error("Could not find Config File! {}", e.getMessage());
            System.exit(-1);
        } catch (NumberFormatException e) {
            debug.error("Wrong ConfigFile Format! {}", e.getMessage());
            System.exit(-1);
        } catch (IOException e) {
            // Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Parses a MACGeneratorType
     *
     * @param property the String
     * @return the MACGeneratorType
     */
    private MACGeneratorType parseMACGenType(String property) {
        if (property.equals("SERIAL")) {
            return MACGeneratorType.SERIAL;
        }
        if (property.equals("RANDOM")) {
            return MACGeneratorType.RANDOM;
        }
        return null;
    }

    /**
     * Parses a MACGeneratorType
     *
     * @param property the String
     * @return the MACGeneratorType
     */
    private IPGeneratorType parseIPGenType(String property) {
        if (property.equals("SERIAL")) {
            return IPGeneratorType.SERIAL;
        }
        if (property.equals("RANDOM")) {
            return IPGeneratorType.RANDOM;
        }
        return null;
    }

    /**
     * Parses a MACGeneratorType
     *
     * @param property the String
     * @return the MACGeneratorType
     */
    private PortGeneratorType parsePortGenType(String property) {
        if (property.equals("SERIAL")) {
            return PortGeneratorType.SERIAL;
        }
        if (property.equals("RANDOM")) {
            return PortGeneratorType.RANDOM;
        }
        return null;
    }

    /**
     * Sets default Values
     */
    private void loadDefaults() {
        this.openflow_version = 1;
        // 60,5 sec
        this.simTime = 1000 * 60 + 500;
        // 6 sec
        this.startDelay = 6000;
        this.stopDelay = 500;
        this.startDpid = 1;
        this.threadCount = 1;
        this.countSwitches = 25;
        this.hasGraphml = false;

        this.hasTopology = false;
        this.checkForIndividualSettings = false;
        // Localhost
        InetSocketAddress contAddress = new InetSocketAddress("127.0.0.1", 6633);

        this.runConfig.setStartDpid(1);
        this.runConfig.setCountSwitches(5);

        this.switchConfig.setContAddress(contAddress);
        this.switchConfig.setPortCountperSwitch(4);
        this.switchConfig.setBuffersPerSwitch(256);
        this.switchConfig.setSession(25);
        this.switchConfig.setDpid(1);
        this.switchConfig.setBatchSending(true);
        this.switchConfig.setDisableNagle(true);
        this.switchConfig.setFlowTableSize(128);
        this.switchConfig.setRandomizeStats(true);

        this.trafficGenConfig.setScenario("TCPSYN");
        this.trafficGenConfig.setFillThreshold(500);
        this.trafficGenConfig.setIAT(100);
        this.trafficGenConfig.setCountPerEvent(1);
        this.trafficGenConfig.setSwitchHasIndividualSetting(false);
        this.trafficGenConfig.setArpEnabled(false);
        this.trafficGenConfig.setMACGeneratorType(MACGeneratorType.SERIAL);
        this.trafficGenConfig.setIPGeneratorType(IPGeneratorType.SERIAL);
        this.trafficGenConfig.setPortGeneratorType(PortGeneratorType.SERIAL);
        this.trafficGenConfig.setIatType(0);
        this.trafficGenConfig.setOnlyTopoPayloads(false);

        List<String> stats = new ArrayList<>();
        stats.add("PPS");
        stats.add("RTT");
        stats.add("TSL");
        this.statConfig.setStatModules(stats);
        this.statConfig.setMonitorAddress(contAddress);
    }

    /**
     * OpenFlow Protocol Version Used
     *
     * @return used OFProtocol Version
     */
    public int getOFVersion() {
        return this.openflow_version;
    }

    /**
     * Session Time in [msec]
     *
     * @return benchingSession length in [msec]
     */
    public long getSimTime() {
        return this.simTime;
    }

    /**
     * Delay between Connection establishment/Handshake and First Sent PacketIN
     * Msgs
     *
     * @return the time
     */
    public long getStartDelay() {
        return this.startDelay;
    }

    /**
     * Maximum Waiting time for incoming PacketIns after SessionEnd
     *
     * @return the time
     */
    public long getStopDelay() {
        return this.stopDelay;
    }

    /**
     * Is extra Topology Emulation File Provided
     *
     * @return true or false
     */
    public boolean hasTopology() {
        return this.hasTopology;
    }

    /**
     * Is extra Topology Emulation File Provided
     *
     * @return true or false
     */
    public void setHasTopology(boolean flag) {
        this.hasTopology = flag;
    }

    /**
     * Has each Switch Individual Settings
     *
     * @return true -> each Switch should check for individual Settings
     */
    public boolean checkForIndividualSettings() {
        return this.checkForIndividualSettings;
    }

    public String getIndividualSwitchSettingsFileName() {
        return this.individualSwitchSettingsFile;
    }

    /**
     * Get DPID of first initialized Switches
     *
     * @return First used DPID
     */
    public int getStartDpid() {
        return this.startDpid;
    }

    /**
     * Get Count of Switches
     *
     * @return count of Switches to initialize
     */
    public int getCountSwitches() {
        return this.countSwitches;
    }

    /**
     * Get Count of Threads
     *
     * @return count of used SwitchThreads
     */
    public int getThreadCount() {
        return this.threadCount;
    }

    /**
     * The OFSwitchRunner Configuration
     *
     * @return the ofSwitchRunnerConfig
     */
    public RunnerConfig getRunnerConfig() {
        return this.runConfig;
    }

    /**
     * The StatisticModules Configuration
     *
     * @return the StatisticModuleConfig
     */
    public StatConfig getStatConfig() {
        return this.statConfig;
    }

    /**
     * The ofSwitch Configuration
     *
     * @return the ofSwitchConfig
     */
    public SwitchConfig getSwitchConfig() {
        return this.switchConfig;
    }

    /**
     * The TrafficGen Module Configuration
     *
     * @return the TrafficGenConfig
     */
    public TrafficGenConfig getTrafficGenConfig() {
        return this.trafficGenConfig;
    }

    /**
     * The Topology
     *
     * @return the Topology
     */
    public Topology getTopology() {
        return this.topology;
    }

    /**
     * String representataion
     *
     * @return the String
     */
    @Override
    public String toString() {
        String output = "Config: " + completeConfigToString();
        return output;
    }

    /**
     * String representation of all Config items including SubConfigs
     *
     * @return the String
     */
    public String completeConfigToString() {
        String output = toString() + "\n";
        output += this.runConfig.toString() + "\n";
        output += this.switchConfig + "\n";
        output += this.statConfig + "\n";
        output += this.trafficGenConfig + "\n";
        output += this.topology.toString() + "\n";
        return output;
    }

    public boolean getOnosControlled() {
        return this.isOnosControlled;
    }
}
