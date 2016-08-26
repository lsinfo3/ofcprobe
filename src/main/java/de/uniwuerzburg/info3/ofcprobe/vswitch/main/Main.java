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
package de.uniwuerzburg.info3.ofcprobe.vswitch.main;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniwuerzburg.info3.ofcprobe.vswitch.connection.IOFConnection;
import de.uniwuerzburg.info3.ofcprobe.vswitch.main.config.Config;
import de.uniwuerzburg.info3.ofcprobe.vswitch.runner.OFSwitchRunner;
import de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.TrafficGen;

/**
 * This Class is actually executed first when starting OFCProbe.Vswitch from the
 * cmd line. It loads up a provided configuration file an instantiates the
 * VirtualOFSwitch Objects. After that the benching begins. In the whole Javadoc
 * 'ofSwitch' is an instance of the 'IOFConnection' interface, so atm an
 * instance of 'OFConnection1_zero'
 *
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 * @version 1.0.3-SNAPSHOT
 */
public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private List<OFSwitchRunner> switchThreads;

    public void startStuff(String[] args) {
        this.switchThreads = new ArrayList<>();

        if (args.length < 1 || args.length > 2) {
            logger.error("Error! Need at least one argument! May have 2 Arguments Maximum:");
            logger.error("1.Argument: ConfigFile; 2.Argument(optional): SwitchCount");
            throw new IllegalArgumentException();
        }

        String configFile = new String();
        if (args.length > 0) {
            configFile = args[0];
        }
        int switchCount = -1;
        if (args.length == 2) {
            switchCount = Integer.parseInt(args[1]);
        }
        Config config = new Config(configFile, switchCount);
        switchCount = config.getCountSwitches();

        int ThreadCount = config.getThreadCount();
        int switchesPerThread = (int) (switchCount / ThreadCount);
        int rest = switchCount % ThreadCount;

        config.getSwitchConfig().setSession(switchCount);
        config.getRunnerConfig().setCountSwitches(switchesPerThread);

        List<OFSwitchRunner> switchRunners = new ArrayList<>();
        List<Thread> switchThreads = new ArrayList<>();
        Map<OFSwitchRunner, Thread> switchThreadMap = new HashMap<>();
        TrafficGen trafficGen = new TrafficGen(config);

        int startDpid = config.getStartDpid();
        int initializedSwitches = 0;

        if (switchCount < ThreadCount) {
            ThreadCount = switchCount;
        }

        for (int i = 0; i < ThreadCount; i++) {
            if (i < rest) {
                config.getRunnerConfig().setCountSwitches(switchesPerThread + 1);
                config.getRunnerConfig().setStartDpid(initializedSwitches + startDpid);
                initializedSwitches += (switchesPerThread + 1);

            } else {
                config.getRunnerConfig().setCountSwitches(switchesPerThread);
                config.getRunnerConfig().setStartDpid(initializedSwitches + startDpid);
                initializedSwitches += switchesPerThread;
            }

            OFSwitchRunner ofswitch = new OFSwitchRunner(config, this);
            Thread switchThread = new Thread(ofswitch, "switchThread#" + i);

            trafficGen.registerSwitchThread(ofswitch);
            switchThread.start();

            switchRunners.add(ofswitch);
            switchThreads.add(switchThread);
            this.switchThreads.add(ofswitch);
            switchThreadMap.put(ofswitch, switchThread);
        }

        Thread trafficThread = new Thread(trafficGen, "TrafficGen");

        // Start benching
        trafficThread.start();
        double targetPackets = (1000.0 / config.getTrafficGenConfig().getIAT()) * config.getTrafficGenConfig().getFillThreshold() * config.getTrafficGenConfig().getCountPerEvent();
        try {
            Thread.sleep(2 * config.getStartDelay());
            logger.info("Benchin now started! - Target Packets Generated per connected ofSwitch per Second: {}", targetPackets);
            logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            Thread.sleep(config.getSimTime() + config.getStopDelay());
            logger.info("{} sec gone!", config.getSimTime() / 1000);
        } catch (InterruptedException e) {
            // Auto-generated catch block
            logger.debug(e.getLocalizedMessage());
        }

        // Benching done, now stop TrafficGen
        trafficThread.interrupt();

        for (OFSwitchRunner ofswitchRunner : switchRunners) {
            ofswitchRunner.endSession();
        }

        long WAITTIME = config.getStopDelay();
        int threadsStopped = 0;
        Date benchinEnd = new Date();
        Date now = new Date();
        while (threadsStopped < ThreadCount || now.getTime() - benchinEnd.getTime() < 10000) {
            now = new Date();
            for (OFSwitchRunner ofswitchRunner : switchRunners) {
                if (now.getTime() - ofswitchRunner.lastPacketInTime() > WAITTIME) {
                    threadsStopped++;
                }
            }
        }

        // Stop all SwitchThreads
        for (Thread t : switchThreads) {
            t.interrupt();
        }

        logger.info("Interrupted, now ending");

        // Now end session, evaluate and do the reporting
        for (OFSwitchRunner ofswitchRunner : switchRunners) {
            ofswitchRunner.getSelector().wakeup();
            ofswitchRunner.evaluate();
            ofswitchRunner.report();
        }

        logger.info("Session ended!");
        logger.info("~~~~~~~~~~~~~~");
        System.out.println();
        System.exit(0);
    }

    /**
     * Main Method
     *
     * @param args atm: 1.Argument: Configfile; 2.Argument: SwitchCount;
     */
    public static void main(String[] args) {
        Main main = new Main();
        main.startStuff(args);
    }

    /**
     * Returns the IOFConnection for a given DPID
     *
     * @param dpid the DPID
     * @return the corresponding IOFConnection
     */
    public IOFConnection getIOFConByDpid(long dpid) {
        Iterator<OFSwitchRunner> iter = this.switchThreads.iterator();
        while (iter.hasNext()) {
            OFSwitchRunner runner = iter.next();
            IOFConnection ofSwitch = runner.getOfSwitch(dpid);
            if (ofSwitch != null) {
                return ofSwitch;
            }
        }
        return null;
    }

}
