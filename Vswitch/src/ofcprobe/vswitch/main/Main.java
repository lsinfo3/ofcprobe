package ofcprobe.vswitch.main;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ofcprobe.vswitch.connection.IOFConnection;
import ofcprobe.vswitch.main.config.Config;
import ofcprobe.vswitch.runner.OFSwitchRunner;
import ofcprobe.vswitch.trafficgen.TrafficGen;

/**
 * This Class is actually executed first when starting OFCProbe.Vswitch from the cmd line.
 * It loads up a provided configuration file an instantiates the VirtualOFSwitch Objects.
 * After that the benching begins.  
 * In the whole Javadoc 'ofSwitch' is an instance of the 'IOFConnection' interface, so atm an instance of 'OFConnection1_zero'
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 * @version 1.0
 */
public class Main {

	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	private List<OFSwitchRunner> switchThreads;
	
	public void startStuff(String[] args){
		this.switchThreads = new ArrayList<OFSwitchRunner>();
		
		if (args.length < 1 || args.length > 2){
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
		
//		if (config != null) {
//			jnetPcapLibraryLoader();
//		}
		

		
		int ThreadCount = config.getThreadCount();
		int switchesPerThread = (int) (switchCount / ThreadCount);
		int rest= switchCount%ThreadCount;
		
		config.getSwitchConfig().setSession(switchCount);
		config.getRunnerConfig().setCountSwitches(switchesPerThread);
		
		List<OFSwitchRunner> switchRunners = new ArrayList<OFSwitchRunner>();
		List<Thread> switchThreads = new ArrayList<Thread>();
		Map<OFSwitchRunner,Thread> switchThreadMap = new HashMap<OFSwitchRunner, Thread>();
		TrafficGen trafficGen = new TrafficGen(config);
		

		int startDpid = config.getStartDpid();
		int initializedSwitches = 0;
		
		if (switchCount < ThreadCount) {
			ThreadCount = switchCount;
		}
		
		for (int i = 0; i < ThreadCount; i++) {
//			int switchesLeft = switchCount - initializedSwitches;
							
//			if (i == ThreadCount -1 ) {
//				if (switchesLeft < switchesPerThread){
//					config.getRunnerConfig().setCountSwitches(switchesLeft);
//				} 
//			}
			if (i < rest ) {			
				config.getRunnerConfig().setCountSwitches(switchesPerThread+1);
//				config.getRunnerConfig().setCountSwitches(switchesLeft+1);
				config.getRunnerConfig().setStartDpid(initializedSwitches + startDpid);
				initializedSwitches+=(switchesPerThread+1);
			
		} 
			else {
			config.getRunnerConfig().setCountSwitches(switchesPerThread);
			config.getRunnerConfig().setStartDpid(initializedSwitches + startDpid);
			initializedSwitches+=switchesPerThread;
		}
				
			

			OFSwitchRunner ofswitch = new OFSwitchRunner(config, this);
			Thread switchThread = new Thread(ofswitch, "switchThread#" + i);
			
			trafficGen.registerSwitchThread(ofswitch);
			switchThread.start();
			
			switchRunners.add(ofswitch);
			switchThreads.add(switchThread);
			this.switchThreads.add(ofswitch);
			switchThreadMap.put(ofswitch, switchThread);
//			initializedSwitches+=switchesPerThread;
			
		}
		
		Thread trafficThread = new Thread(trafficGen, "TrafficGen");
		
		
		// Start benching
		trafficThread.start();
		double targetPackets = (1000.0/config.getTrafficGenConfig().getIAT()) * config.getTrafficGenConfig().getFillThreshold() * config.getTrafficGenConfig().getCountPerEvent();
		try {
			Thread.sleep(2*config.getStartDelay());
			logger.info("Benchin now started! - Target Packets Generated per connected ofSwitch per Second: {}", targetPackets);
			logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			Thread.sleep(config.getSimTime()  + config.getStopDelay());
			logger.info("{} sec gone!", config.getSimTime()/1000);
		} catch (InterruptedException e) {
			// Auto-generated catch block
			e.printStackTrace();
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
		while(threadsStopped < ThreadCount || now.getTime() - benchinEnd.getTime() < 10000) {
			now = new Date();
			for (OFSwitchRunner ofswitchRunner : switchRunners) {
				if (now.getTime() - ofswitchRunner.lastPacketInTime() > WAITTIME){
					threadsStopped++;
					
//					Thread t = switchThreadMap.get(ofswitchRunner);
//					t.interrupt();
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
	 * args should become a string path to a configfile (tbd)
	 * @param args atm: 1.Argument: SwitchCount; 2.Argument: SwitchesPerThread; 3.Argument: StartingDpid
	 */
	public static void main(String[] args) {
		Main main = new Main();
		main.startStuff(args);
	}
	
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

	
	public void jnetPcapLibraryLoader(){
		logger.trace("Loading jnetPcap Libraries");
		String os = System.getProperty("os.name").toLowerCase();
		String arch = System.getProperty("sun.arch.data.model");
		String bin = "none";
		
		if (os.contains("win")) {
			logger.trace("Windows System detected");
			if (arch.equals("32")) {
				logger.trace("Windows 32Bit detected");
			}
			if (arch.equals("64")) {
				logger.trace("Windows 64Bit detected");
				bin = (".lib/jNetPcap/jnetpcap-1.3.0-win64/jnetpcap.jar");
			}	
		} else if (os.contains("linux")){
			logger.trace("Linux system detected");
			if (arch.equals("32")) {
				logger.trace("Linux 32Bit detected");
			}
			if (arch.equals("64")) {
				logger.trace("Linux 64Bit detected");
			}	
		}
		
		if (!bin.equals("none")) {
			System.loadLibrary(bin);
		}
	}


}
