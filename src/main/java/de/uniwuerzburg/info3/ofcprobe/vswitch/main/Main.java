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
 * @version 1.0.3
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
