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
package de.uniwuerzburg.info3.ofcprobe.vswitch.statistics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import de.uniwuerzburg.info3.ofcprobe.vswitch.main.config.Config;
import de.uniwuerzburg.info3.ofcprobe.vswitch.statistics.snmp.SNMPManager;
import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.OID;

/**
 * Controller CPU and RAM Monitor per SNMP
 *
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public class ControllerCPUnRAMMonitor implements IStatistics {

    /**
     * Outputfile for CPU
     */
    private String cpuFile;
    /**
     * Outputfile for RAM
     */
    private String ramFile;
    /**
     * List of CPU Values
     */
    private List<Double> cpuValues;
    /**
     * List of Used Ram Values
     */
    private List<Double> ramUsedValues;
    /**
     * List of Free Ram Values
     */
    private List<Double> ramFreeValues;
    /**
     * The Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(ControllerCPUnRAMMonitor.class);
    /**
     * Bool for Session Running
     */
    private boolean sessionRunning;
    /**
     * Date of Last Query
     */
    private Date lastValue;
    /**
     * IAT of Polls
     */
    private long pollTime;
    /**
     * The SNMP Manager
     */
    private SNMPManager snmp;
    /**
     * The LastResponse
     */
    private ResponseEvent lastResponse;
    /**
     * List of OIDs to request
     */
    private OID[] oids;
    /**
     * The DPID
     */
    private String dpid;
    /**
     * Bool for CPUQueries
     */
    private boolean queryCPU;
    /**
     * Bool for RAMQueries
     */
    private boolean queryRAM;
    /**
     * Positon of ramFreeValues in OID Response
     */
    private int ramFreePos;
    /**
     * Positon of ramTotalValues in OID Response
     */
    private int ramTotalPos;
    /**
     * *
     * Got Error from SNMP?
     */
    private boolean snmpError;

    /**
     * Constructor
     *
     * @param config the Config File
     */
    public ControllerCPUnRAMMonitor(Config config) {
        this.cpuValues = new ArrayList<>();
        this.ramUsedValues = new ArrayList<>();
        this.ramFreeValues = new ArrayList<>();
        this.sessionRunning = false;
        NumberFormat formatter = new DecimalFormat("#000");
        this.dpid = formatter.format(config.getSwitchConfig().getDpid());
        this.pollTime = 10000;

        checkForSNMP(config.getStatConfig().getMonitorAddress().getHostString());

        if (!this.snmpError) {
            this.snmp = new SNMPManager("udp:" + config.getStatConfig().getMonitorAddress().getHostString() + "/161");
            this.snmp.start();

            List<String> modules = config.getStatConfig().getStatModules();

            // Build correct OID Set
            if (modules.contains("CPU") && modules.contains("RAM")) {
                this.queryCPU = true;
                this.queryRAM = true;
                /*
                 * rawUser = .1.3.6.1.4.1.2021.11.50.0
                 * rawNice = .1.3.6.1.4.1.2021.11.51.0
                 * rawSystem = .1.3.6.1.4.1.2021.11.52.0
                 * rawIDL = .1.3.6.1.4.1.2021.11.53.0
                 * rawWait = .1.3.6.1.4.1.2021.11.54.0
                 * rawKernel = .1.3.6.1.4.1.2021.11.55.0
                 * rawInterrupt = .1.3.6.1.4.1.2021.11.56.0
                 * ramFree  = .1.3.6.1.4.1.2021.4.6.0
                 * ramTotal = .1.3.6.1.4.1.2021.4.5.0
                 */
                this.oids = new OID[]{
                    new OID(".1.3.6.1.4.1.2021.11.50.0"),
                    new OID(".1.3.6.1.4.1.2021.11.51.0"),
                    new OID(".1.3.6.1.4.1.2021.11.52.0"),
                    new OID(".1.3.6.1.4.1.2021.11.53.0"),
                    new OID(".1.3.6.1.4.1.2021.4.6.0"),
                    new OID(".1.3.6.1.4.1.2021.4.5.0")};
                this.ramFreePos = 4;
                this.ramTotalPos = 5;
            }

            if (modules.contains("CPU") && !modules.contains("RAM")) {
                this.queryCPU = true;
                this.queryRAM = false;
                /*
                 * rawUser = .1.3.6.1.4.1.2021.11.50.0
                 * rawNice = .1.3.6.1.4.1.2021.11.51.0
                 * rawSystem = .1.3.6.1.4.1.2021.11.52.0
                 * rawIDL = .1.3.6.1.4.1.2021.11.53.0
                 * rawWait = .1.3.6.1.4.1.2021.11.54.0
                 * rawKernel = .1.3.6.1.4.1.2021.11.55.0
                 * rawInterrupt = .1.3.6.1.4.1.2021.11.56.0
                 */
                this.oids = new OID[]{
                    new OID(".1.3.6.1.4.1.2021.11.50.0"),
                    new OID(".1.3.6.1.4.1.2021.11.51.0"),
                    new OID(".1.3.6.1.4.1.2021.11.52.0"),
                    new OID(".1.3.6.1.4.1.2021.11.53.0")};
            }

            if (!modules.contains("CPU") && modules.contains("RAM")) {
                this.queryCPU = false;
                this.queryRAM = true;
                /*
                 * ramFree  = .1.3.6.1.4.1.2021.4.6.0
                 * ramTotal = .1.3.6.1.4.1.2021.4.5.0
                 */
                this.oids = new OID[]{
                    new OID(".1.3.6.1.4.1.2021.4.6.0"),
                    new OID(".1.3.6.1.4.1.2021.4.5.0")};
                this.ramFreePos = 0;
                this.ramTotalPos = 1;
            }
        }

    }

    /**
     * Method thought to check if there is SNMP Server running on provided IP
     *
     * @param ip the IP Address
     */
    private void checkForSNMP(String ip) {
//		SocketChannel chan;
//		try {
//			chan = SocketChannel.open();
//			chan.connect(new InetSocketAddress(ip, 161));
//			chan.close();
//		} catch (IOException e) {
//			logger.error("Cannot reach SNMPD - Is SNMPD started?");
//			this.snmpError = true;
//		}

    }

    @Override
    public void setReportFile(String file) {
        if (file.contains("cpu")) {
            this.cpuFile = file;
        }
        if (file.contains("ram")) {
            this.ramFile = file;
        }

    }

    @Override
    public void packetIn(OFMessage in) {
        if (!this.snmpError && this.sessionRunning && in.getType() == OFType.PACKET_OUT) {
            Date now = new Date();
            if (now.getTime() - this.lastValue.getTime() > this.pollTime) {
                this.lastValue = now;
                queryNextValues();
            }
        }
    }

    @Override
    public void packetOut(OFMessage out) {
        // Doing nothing
    }

    @Override
    public void evaluate() {

    }

    @Override
    public void report() {
        if (!this.snmpError) {
            writeToFile();
        }
    }

    @Override
    public void start() {
        this.lastValue = new Date();
        if (!this.snmpError) {
            this.sessionRunning = true;
            queryNextValues();
            this.cpuValues.clear();
            this.ramFreeValues.clear();

        }
    }

    @Override
    public void stop() {
        this.sessionRunning = false;
    }

    private void queryNextValues() {
        double cpuValue = 0.0;
        double ramValue = 0.0;
        double ramFree = 0.0;

        if (this.lastResponse == null) {
            this.lastResponse = this.snmp.get(this.oids);
        }

        ResponseEvent respNow = this.snmp.get(this.oids);

        if (this.queryCPU) {
            for (int i = 0; i < 5; i++) {
                cpuValue += calculateCPUValue(this.lastResponse, respNow);
            }
            cpuValue = cpuValue / 5;

            this.lastResponse = respNow;
            logger.trace("[Switch#{}]: New CPU Value: {}", this.dpid, cpuValue);
            this.cpuValues.add(cpuValue);
        }

        if (this.queryRAM) {
            ramFree = queryFreeRAMValue(respNow);
            ramValue = calculateUsedRAMValue(respNow);
            logger.trace("[Switch#{}]: New Used RAM Value: {} - New Free RAM Value: {}", this.dpid, ramValue, ramFree);
            this.ramUsedValues.add(ramValue);
            this.ramFreeValues.add(ramFree);
        }

    }

    /**
     * Write Results to File
     */
    private void writeToFile() {
        try {
            if (this.queryCPU) {
                File filou = new File(this.cpuFile);
                if (!filou.getParentFile().exists()) {
                    filou.getParentFile().mkdirs();
                }
                PrintWriter out = new PrintWriter(this.cpuFile);

                // CPU
                double cpuMean = 0.0;
                if (!this.cpuValues.isEmpty()) {
                    for (double cpu : this.cpuValues) {
                        out.print(cpu + ";");
                        cpuMean += cpu;
                    }
                } else {
                    out.print("0;");
                }
                out.print("\n");

                cpuMean = cpuMean / this.cpuValues.size();
                if (Double.isNaN(cpuMean)) {
                    cpuMean = 0;
                }
                logger.info("[Switch#{}]: CPUMean in this Session: {}", this.dpid, cpuMean);
                out.println(cpuMean);
                out.close();
            }

            if (this.queryRAM) {
                File filou = new File(this.cpuFile);
                if (!filou.getParentFile().exists()) {
                    filou.getParentFile().mkdirs();
                }
                PrintWriter out = new PrintWriter(this.ramFile);
                // RAM Used
                double ramUsedMean = 0.0;
                if (!this.ramUsedValues.isEmpty()) {
                    for (double ram : this.ramUsedValues) {
                        out.print(ram + ";");
                        ramUsedMean += ram;
                    }
                } else {
                    out.print("0;");
                }
                out.print("\n");

                ramUsedMean = ramUsedMean / this.ramUsedValues.size();
                if (Double.isNaN(ramUsedMean)) {
                    ramUsedMean = 0;
                }
                logger.info("[Switch#{}]: RAM Used Mean in this Session: {}", this.dpid, ramUsedMean);

                out.println(ramUsedMean);

                // RAM Free
                double ramFreeMean = 0.0;
                if (!this.ramFreeValues.isEmpty()) {
                    for (double ram : this.ramFreeValues) {
                        out.print(ram + ";");
                        ramFreeMean += ram;
                    }
                } else {
                    out.print("0;");
                }
                out.print("\n");

                ramFreeMean = ramFreeMean / this.ramFreeValues.size();
                if (Double.isNaN(ramFreeMean)) {
                    ramFreeMean = 0;
                }
                logger.info("[Switch#{}]: RAM Free Mean in this Session: {}", this.dpid, ramFreeMean);

                out.println(ramFreeMean);

                out.close();
            }
        } catch (FileNotFoundException e) {
            logger.error("[Switch#{}]: {}", this.dpid, e);
        }
    }

    /**
     * Calculate the CPU load
     *
     * @param respLast first Response
     * @param respNow second Response
     * @return double Value
     */
    private double calculateCPUValue(ResponseEvent respLast, ResponseEvent respNow) {
        try {
            if (respLast != null && respNow != null && respLast.getResponse() != null && respNow.getResponse() != null) {
                int rawUserOne = Integer.valueOf(respLast.getResponse().get(0).toValueString());
                int rawNiceOne = Integer.valueOf(respLast.getResponse().get(1).toValueString());
                int rawSystemOne = Integer.valueOf(respLast.getResponse().get(2).toValueString());
                int rawIDLOne = Integer.valueOf(respLast.getResponse().get(3).toValueString());
                int rawUserTwo = Integer.valueOf(respNow.getResponse().get(0).toValueString());
                int rawNiceTwo = Integer.valueOf(respNow.getResponse().get(1).toValueString());
                int rawSystemTwo = Integer.valueOf(respNow.getResponse().get(2).toValueString());
                int rawIDLTwo = Integer.valueOf(respNow.getResponse().get(3).toValueString());

                int rawUserDiff = rawUserTwo - rawUserOne;
                int rawNiceDiff = rawNiceTwo - rawNiceOne;
                int rawSystemDiff = rawSystemTwo - rawSystemOne;
                int rawIDLDiff = rawIDLTwo - rawIDLOne;
                int sum = rawUserDiff + rawNiceDiff + rawSystemDiff + rawIDLDiff;
                if (sum != 0) {
                    return (double) (100 - (((double) rawIDLDiff * 100) / ((double) sum)));
                }
            } else {
                logger.error("SNMP Response was NULL - Assuming SNMPD is offline. Exiting now!");
                this.snmpError = true;
                stop();
                System.exit(-1);
            }
            return 0;
        } catch (NullPointerException e) {
            logger.error("SNMP CPU Values threw Nullpointer - Assuming SNMPD is offline. Exiting now!");
            this.snmpError = true;
            stop();
            System.exit(-1);
            return 0;
        }
    }

    /**
     * Calculate the RAM load
     *
     * @param respNow Response
     * @return double Value
     */
    private double queryFreeRAMValue(ResponseEvent respNow) {
        try {
            if (respNow != null && respNow.getResponse() != null) {
                double ramFree = Double.parseDouble(respNow.getResponse().get(this.ramFreePos).toValueString());
                if (ramFree != 0) {
                    ramFree = ramFree / 1024;
                }
                return ramFree;
            } else {
                logger.error("SNMP Response was NULL - Assuming SNMPD is offline. Exiting now!");
                this.snmpError = true;
                stop();
                System.exit(-1);
            }
            return 0;
        } catch (NullPointerException e) {
            logger.error("SNMP RAM Values threw Nullpointer - Assuming SNMPD is offline. Exiting now!");
            this.snmpError = true;
            stop();
            System.exit(-1);
            return 0;
        }
    }

    /**
     * Calculate the RAM load
     *
     * @param respNow Response
     * @return double Value
     */
    private double calculateUsedRAMValue(ResponseEvent respNow) {
        try {
            if (respNow != null && respNow.getResponse() != null) {
                double ramFree = Double.valueOf(respNow.getResponse().get(this.ramFreePos).toValueString());
                double ramTotal = Double.valueOf(respNow.getResponse().get(this.ramTotalPos).toValueString());

                double ramUsed = ramTotal - ramFree;
                ramUsed = ramUsed / 1024;

                return ramUsed;
            }
            return 0;
        } catch (NullPointerException e) {
            return 0;
        }
    }

}
