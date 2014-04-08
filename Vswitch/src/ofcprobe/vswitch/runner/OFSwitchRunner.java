
package ofcprobe.vswitch.runner;

import java.io.IOException;
import java.net.ConnectException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.openflow.protocol.OFHello;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ofcprobe.vswitch.connection.IOFConnection;
import ofcprobe.vswitch.connection.OFConnection1_zero;
import ofcprobe.vswitch.main.Main;
import ofcprobe.vswitch.main.config.Config;
import ofcprobe.vswitch.main.config.RunnerConfig;

/**
 * This Class reasambles a Thread who has controll over a defined count of ofSwitch objects.
 * @author Christopher Metter
 */
public class OFSwitchRunner implements Runnable{
		
	/**
	 * The debug instance for this object
	 */
	private static final Logger logger = LoggerFactory.getLogger(OFSwitchRunner.class);
	/**
	 * List of 'connected' ofSwitches
	 */
	private List<IOFConnection> switches;
	/**
	 * the DPID of the first ofSwitch
	 */
	private int startdpid;
	/**
	 * the Amount of ofSwitches that have to be initiated
	 */
	private int countswitches;

	/**
	 * The Selector for the connectionhandling
	 */
	private Selector selector;
	/**
	 * the OpenFlow Protocol Version used in this Run
	 */
	private int openflow_version;
	/**
	 * When has this Instance been Initiated?
	 */
	private Date initialized;
	/**
	 * The Configuration for this Session
	 */
	private Config config;
	/**
	 * Has the BenchingSession already ended?
	 */
	private boolean sessionEnded;
	/**
	 * Already one ofSwitch connected ?
	 */
	private boolean selectorInitialized;
	/**
	 * The corresponding starting Thread
	 */
	private Main main;
	
	
	/**
	 * Creates a new OFSwitchRunner with the Parameters of the provided config-object.
	 * @param config the config-object
	 */
	public OFSwitchRunner(Config config, Main main) {
		this.main = main;
		//initialize Lists
		this.switches = new ArrayList<IOFConnection>();
		this.sessionEnded = false;
		this.selectorInitialized = false;
		
		this.config = config;
		RunnerConfig runConfig = config.getRunnerConfig();
		
		this.startdpid = runConfig.getStartDpid();
		this.countswitches = runConfig.getCountswitches();
		
		this.openflow_version = config.getOFVersion();
		if (this.openflow_version == 1) {
			init1_zero();
		}
	}

	/**
	 * Is switch connected?
	 * @param switchNo SwitchNo 1 -> index 0!
	 * @return true-> this switch is Connected, false-> this switch is not connected!
	 */
	public boolean isConnected(int switchNo){
		if (switchNo-1 < this.switches.size()) {
			return this.switches.get(switchNo-1).getChannel().isConnected();
		} else {
			return false;
		}
	}
	
	/**
	 * All Switches ready for input?
	 * @return true if All ofSwitches Ready
	 */
	public boolean isReady(){
		Date now = new Date();
		boolean allConnected = true;
		for (IOFConnection ofSwitch : this.switches) {
			if (ofSwitch.getChannel() != null) {
				if (!ofSwitch.getChannel().isConnected()){
					allConnected = false;
				}
			}
		}
		
		if (!allConnected || now.getTime() - this.initialized.getTime() < 10000){
			return false;
		} else {
			return true;
		}

	}
	
	/**
	 * Init Switches for OpenFlow Protocol version 1.0
	 */
	private void init1_zero(){
		this.initialized = new Date();
		
		
		// For loop that instantiates the ofSwitch Objects
		for (int i = 0; i < this.countswitches; i++) {
			int dpid = i + this.startdpid;
			
			// Method names should speak for whats happening
			this.config.getSwitchConfig().setDpid(dpid);
			IOFConnection ofcon = new OFConnection1_zero(this, this.config);
			
			// add ofSwitch to our Collection
			this.switches.add(ofcon);
			
		}
		
		logger.info("All {} Switches have been initiated!", this.countswitches);
		logger.info("Controller-Address: {}", this.config.getSwitchConfig().getContAddress().toString());
	}
	
	/**
	 * Configure this Channel.
	 * @param chan the Channel to Configure
	 * @throws IOException is processed by calling Method
	 */
	private SocketChannel configureChannel(SocketChannel chan) throws IOException{
		chan.configureBlocking(false);
		if (this.config.getSwitchConfig().disableNagle()) {
			// Disable Nagle's Alogrithm
			logger.debug("Nagle Algorithm Disabled!");
			chan.socket().setTcpNoDelay(true);
		}
		return chan;
	}
	
	/**
	 * Initializes the Selector
	 * @return the initialized Selector 
	 * @throws IOException handled by calling Method
	 */
	private Selector initSelector() throws IOException {
	    // Create a new selector
	    Selector socketSelector = SelectorProvider.provider().openSelector();

	    return socketSelector;
	  }
	
	
	/**
	 * Connection handling happens here.
	 */
	@Override
	public void run() {
		while (!Thread.interrupted()) {
			try {
				// check if handshakes done (10sek after initialization)
				if (isReady()) {
					// and then start sending the generated and queued packets
					queueProcessing();
				}
				
				// the actual connection handling
				if (this.selector != null) {
					this.selector.selectNow();
					Iterator<SelectionKey> selectedKeys = this.selector.selectedKeys().iterator();
					// Process all keys
			    	while (selectedKeys.hasNext()) {
			    		SelectionKey key = (SelectionKey) selectedKeys.next();
			    		selectedKeys.remove();
			          
			    		// Check whether key is a valid one
			    		if (!key.isValid()) {
			    			continue;
			    		}
			          
			    		// Try to process key
			    		try {
			    			processKey(key);
			    		} catch (ConnectException e) {
			    			logger.error("Cannot connect to Controller! Is it started yet?");
			    			logger.error("Exiting ...");
			    			System.exit(1);
			    		} catch (IOException e) {
			    			logger.error("{}",e);
			    		}
			    	}
				}
			} catch (ClosedSelectorException e) {
				logger.error("Selector has been closed while operating!");
			} catch (IOException e) {
				logger.error("{}",e);
			}
		}

	}
	
	/**
	 * Process PacketInQueue of every ofSwitch
	 */
	private void queueProcessing() {
		boolean packetOutQueueEmpty = false;
		while (!packetOutQueueEmpty) {
			if (this.sessionEnded) {
				break;
			}
			packetOutQueueEmpty = true;
			
			for (IOFConnection ofSwitch : this.switches) {
				if (!outQueue(ofSwitch)) {
					packetOutQueueEmpty = false;
				}
	    	}
			for (IOFConnection ofSwitch : this.switches) {
				ofSwitch.receive();
			}
		}
	}
	
	/**
	 * Process outGoing Queue of a ofSwitch
	 * @param ofSwitch will be processed
	 * @return flag
	 */
	private boolean outQueue(IOFConnection ofSwitch) {
		boolean packetOutQueueEmpty;
		if (ofSwitch.hasPacketInQueued()){
			
			packetOutQueueEmpty = false;
			ofSwitch.sendPacketIn();
		} else {
			packetOutQueueEmpty = true;
		}
		return packetOutQueueEmpty;
	}
	
	/**
	 * KeyProcessor.
	 * @param key SelectionKey
	 * @throws IOException
	 */
	private void processKey(SelectionKey key) throws IOException {
		try{
			//Get the Channel of this Key
			SocketChannel chan = (SocketChannel)key.channel();
	
			if (key.isReadable() && chan.isOpen() && key.isValid()) {
				// Get the IOFConnection/the Switch for this Channel
				IOFConnection toRead = (IOFConnection) key.attachment();
				if (toRead != null) {
					// Process Incoming Message
					toRead.receive();
				}
			}
			
			if (key.isWritable() && chan.isOpen() && key.isValid()) {
				//write queuedMsgs(this.packetQueues) to channel
				write(chan, key);
			}
			
			if (key.isConnectable()) {
				// connect selected Channel
				connect(chan, key);
			}
		} catch (CancelledKeyException e) {
			logger.error("Selector has been closed while operating!");
		}
	}
	
	/**
	 * Method which writes queued Payloads from queuePacketInEvent(..) to the Socket of the corresponding ofSwitch
	 * @param chan the channel (and with it connected: the ofSwitch)
	 * @param key SelectionKey
	 */
	private void write(SocketChannel chan, SelectionKey key){

	}
	
	/**
	 * Finish Connection for SocketChannel
	 * @param chan
	 * @param key
	 * @throws IOException
	 */
	private void connect(SocketChannel chan, SelectionKey key) throws IOException {
		if (chan.finishConnect()) {
			key.interestOps(key.interestOps() ^ SelectionKey.OP_CONNECT);
			key.interestOps(key.interestOps() | SelectionKey.OP_READ);
		}
		
	}
	

	/**
	 * Get Switches of this OFSwitchRunner
	 * @return List containing ofSwitches of this OFswitchRunner
	 */
	public List<IOFConnection> getSwitches(){
		return this.switches;
	}
	
	/**
	 * Send to all connected ofSwitches the evaluate command 
	 */
	public void evaluate(){
		for (IOFConnection ofswitch : this.switches) {
			ofswitch.evaluate();
		}	
	}
	
	/**
	 * Send all connected ofSwitches the report command
	 */
	public void report(){
		for (IOFConnection ofswitch : this.switches) {
			ofswitch.report();
		}
	}
	
	/**
	 * Set flag in all switches so that packets comming in/going out are now processed by the Statistic modules.
	 */
	public void startBenching(){
		for (IOFConnection ofswitch : this.switches) {
			ofswitch.startSession();
		}
	}
	
	
	/**
	 * Closes Connection between OpenFlowController and vSwitch
	 */
	public void endSession() {
		this.sessionEnded = true;
		for(IOFConnection ofswitch : this.switches) {
			ofswitch.stopSession();
		}
	}

	/**
	 * The Selector for this Thread.
	 * @return the selector
	 */
	public Selector getSelector() {
		return selector;
	}

	/**
	 * Last Time a Packet needed for Benching from a controller came in
	 * @return time in millis as long
	 */
	public long lastPacketInTime() {
		long lastPacketInTime = new Date().getTime();
		
		for (IOFConnection ofswitch : this.switches) {
			if (lastPacketInTime > ofswitch.lastPacketInTime()) {
				lastPacketInTime = ofswitch.lastPacketInTime();
			}
		}
		return lastPacketInTime;
	}

	/**
	 * Have all instantiated ofSwitches had OpenFlow Messages?
	 * Needed e.g. for NOX: after successfull TCP Session, NOX does NOTHING, so each 'switch' has to send a OFHello for Handshake
	 * with Floodlight sending a OFHello at any time follows an immediate Disconnect from the Controller.
	 */
	public void alrdyOpenFlowed() {
		for (IOFConnection ofSwitch : this.switches) {
			alrdyOpenFlowed(ofSwitch);
			
		}
	}
	
	/**
	 * Has provided ofSwitch had OpenFlow Messages?
	 * Needed e.g. for NOX: after successfull TCP Session, NOX does NOTHING, so each 'switch' has to send a OFHello for Handshake
	 * with Floodlight sending a OFHello at any time follows an immediate Disconnect from the Controller.
	 */
	public void alrdyOpenFlowed(IOFConnection ofSwitch) {
			if (!ofSwitch.hadOFComm()){
				ofSwitch.send(new OFHello());
			}
	}
	
	/**
	 * Connects Provided ofSwitch and then adds it to the Selector
	 * @param ofSwitch ofSwitch to connect
	 */
	public void initOFSwitchConnections(IOFConnection ofSwitch){
		if (!this.selectorInitialized) {
			try {
				this.selector = initSelector();
				this.selectorInitialized = true;
			} catch (IOException e) {
				logger.error("Selectorinit Failed: {}", e);
			}
		}
		
			 // Create a new non-blocking socket channel
			SocketChannel chan;
			try {
				chan = SocketChannel.open();
				chan = configureChannel(chan);
				// Set this Channel to an OfSwitch -> all Communication of this ofSwitch will happen over this channel 
				ofSwitch.setChannel(chan);
				
				// Connect Socket;
				chan.connect(this.config.getSwitchConfig().getContAddress());

				// Register the socket channel, indicating an interest in 
			    // accepting new connections and attaching the ofSwitch object
				chan.register(this.selector, SelectionKey.OP_CONNECT, ofSwitch);
			} catch (IOException e) {
				// Auto-generated catch block
				logger.error("ofSwitch Connect Failed: {}", e);
			}
	}
	
	/**
	 * Gets the Main Method
	 * @return
	 */
	public Main getMain(){
		return this.main;
	}

	/**
	 * Gets you the ofSwitch with DPID=dpid
	 * @param dpid the DPID 
	 * @return the ofSwitch
	 */
	public IOFConnection getOfSwitch(long dpid) {
		Iterator<IOFConnection> iter = this.switches.iterator();
		while (iter.hasNext()) {
			IOFConnection ofSwitch = iter.next();
			if (ofSwitch.getDpid() == dpid) {
				return ofSwitch;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + countswitches;
		result = prime * result
				+ ((initialized == null) ? 0 : initialized.hashCode());
		result = prime * result + ((main == null) ? 0 : main.hashCode());
		result = prime * result + openflow_version;
		result = prime * result + (selectorInitialized ? 1231 : 1237);
		result = prime * result + (sessionEnded ? 1231 : 1237);
		result = prime * result + startdpid;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OFSwitchRunner other = (OFSwitchRunner) obj;
		if (countswitches != other.countswitches)
			return false;
		if (initialized == null) {
			if (other.initialized != null)
				return false;
		} else if (!initialized.equals(other.initialized))
			return false;
		if (main == null) {
			if (other.main != null)
				return false;
		} else if (!main.equals(other.main))
			return false;
		if (openflow_version != other.openflow_version)
			return false;
		if (selectorInitialized != other.selectorInitialized)
			return false;
		if (sessionEnded != other.sessionEnded)
			return false;
		if (startdpid != other.startdpid)
			return false;
		return true;
	}
	
	


}
