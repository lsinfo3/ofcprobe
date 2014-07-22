#OFCProbe


We present the platform-independent and flexible OpenFlow Controller Analysis Tool OFCProbe on this website. It features 
a scalable and modular architecture that allows a granular and deep analysis of the controllers behaviors and 
characteristics. OpenFlow controllers can be pushed to their limit and the bottle-necks can be investigated.
The tool allows the emulation of virtual switches that each provide sophisticated statistics about the controller 
behavior.

## Tutorial


###Requirements:

- Java
- OpenFlow Controller
- Preferably a Linux System (Shell Scripts + Screen Usage in Scripts provided)
- Passwordless SSH-Connection between OF Controller Host and OFCProbe Host for User openflow
- Download of [OFCProbe](https://github.com/lsinfo3/ofcprobe/releases)


###Preperations On OFCProbe Host


1. Copy OFCProbe.java and your configurations files to a directory
2. Copy On OFCProbe Host\ssh_redirector.sh to home dir of user openflow
3. Open ssh_redirector.sh and change the path in Line#3 to directory of 1. (and change config-file name in Line4)

###Preperations On OF Controller Host


1. Install Controller(s) according to their installation instructions
2. Copy Contents of On Controller Host to directory of your choosing on OF Controller Host
3. You will Find Folders for each Controller, For each Controller you want to operate, do the following:
    
    I. Go to Directory according to your Controller (e.g. Floodlight->>On Controller Host\flood)

    II. Open start.sh
    
    III. Change Directory in Line#2
4. Open launch_config.sh
5. Change IP(s) at the beginning

###Simulation Sequence


####Best Effort Testing
 

> 1 Host

On OFCProbe Host

1. Use provided config.be.ini (dont forget renaming config file to config.ini)

On OF Controller Host

1. Ensure that NO controller is running
2. Go To Directory of launch_config.sh
3. Start Testing:

        ./launch_config.sh 5 flood_be

    This will start Best Effort Testing with 5 Runs on 1 OFCProbe Host emulating [1,5:5:100] Switches with 8 Threads. 
    After the Simlation has finished, you will find a directory on < OFCProbeDirectory>\flood_be\. 
    All statistics Files are found there.

> Multiple Hosts
On OFCProbe Host

1. Use provided config.be.ini (dont forget renaming config file to config.ini)
2. Do not Forget to Change config.startDpid --> Over all OFCProbeHosts, the DPIDs must not overlap
3. Example Scenario with 4 Hosts:
* 100 Switches in Total --> 25 Switches in Total per Host
* Host1: config.startDpid = 1
* Host2: config.startDpid = 100
* Host3: config.startDpid = 200
* Host4: config.startDpid = 300

On OF Controller Host

1. Ensure that NO controller is running. 
2. Go To Directory of launch_config.sh.
3. Check IPs and that none of the IPs is commented.
4. Start Testing:

        ./launch_config.sh 5 flood_mh_be

    This will start Best Effort Testing with 5 Runs on Multiple OFCProbe Host emulating [1,5:5:100] Switches with 8 Threads.
    After the Simlation has finished, you will find a directory on < OFCProbeDirectory>\flood_mh_be\.
    All statistics Files are found there.

####One Host - Topology Testing
On OFCProbe Host

1.  Use provided config.top.ini (dont forget renaming config file to config.ini)
2. Ensure that topology.ini file is in OFCProbe Directory (This File Emulates a FatTree Topology with 20 Switches)

On OF Controller Host

1. Ensure that NO controller is running
2. Go To Directory of launch_config.sh and change the Following:
    * maxswitchnum=100 --> maxswitchnum=20 (for the example topology.ini)
3. Start Testing:

        ./launch_config_top.sh 5 flood_top

    This will start Topology with 5 Runs on 1 OFCProbe Host emulating 20 Switches with 8 Threads (in case you are using provided topology.ini). 
    After the Simlation has finished, you will find a directory on < OFCProbeDirectory>\flood_top\. 
    All statistics Files are found there. 

####GraphML Import
OFCProbe offers the option to import a network topology in the GraphML format. GraphML is a XML-based file format which describes the structural properties of a graph. A lot of GraphML files of real-world network topologies can be found on the [website](http://www.topology-zoo.org/index.html) of the  Internet Topology Zoo project,which has collected data of  over 250 networks.

On OFCProbe Host
1.  Use provided config.top.ini (dont forget renaming config file to config.ini)
2. Copy the GraphML file of the desired network topology into the OFCProbe Directory 
3. Ensure that the parameters config.hasTopolgy and config.hasGraphml are set true
4. Enter the name of the GraphML file for the parameter config.graphml, e.g.
        config.grapml = Aarnet.graphml

On OF Controller Host

1. Ensure that NO controller is running
2. Start Testing:

         ./launch_config_top.sh 5 flood_aarnet

This will start a topology emulation of the choosen GraphlML file. The nodes and edges are imported and a corresponding topology.ini file is automatically created. After the Simlation has finished, you will find a directory on < OFCProbeDirectory>\flood_aarnet\. 
   All statistics Files are found there. 

####Random IAT Values after Distribution

Example for NormalDistribution, mean = 10, stdev=5:

* trafficGenConfig.iatType = 1
* trafficGenConfig.iatDistribution = Normal
* trafficGenConfig.iatDistributionParamter1 = 10
* trafficGenConfig.iatDistributionParamter2 = 5

Individual Settings for Switches

* Set config.checkForIndividualSwitchSettings = true in config.ini on OFCProbeHost
* Check ofSwitch.ini and change it


### Statistics Format

The following files are written as statistics output. The exact meaning of the output is described as follows:

cpu.xxx.txt

- CPU Usage values of Controller Host in Percent measured per SNMP by Switch #xxx
- Default: Measurements each 20Seconds
- Precision: Double
  - Line#1: Measurement Values
  - Line#2: Mean of (Line#1)

pps.xxx.txt

- PacketsPerSeconds of Switch #xxx
- Default: 1 Second = 1 Interval
- Precision: Values in Integer, Mean Values in Double
  - Line#1: Throughput Per Intervall;...
  - Line#2: [Mean Througput over all Intervalls(Mean of Line#1)];[Mean outgoing Packets/Intervall(Generated OFPACKET_IN)];[Mean incoming Packets/Intervall(Received OFPACKET_OUT)]
  - Line#3: Outgoing Packets (OF_PACKET_IN) per Intervall;...
  - Line#4: Incoming Packets (OF_PACKET_OUT) per Intervall;...

ram.xxx.txt

- RAM values in MegaByte of Controller Host measured per SNMP by Switch #xxx 
- Default: Measurement each 20Seconds
- Precision: Double
  - Line#1: Measurement Values of Used RAM on Controller Host
  - Line#2: Mean of (Line#1)
  - Line#3: Measurement Values of Free RAM on Controller Host
  - Line#4: Mean of (Line#2)

rtt.xxx.txt

- RoundTripTime values in MilliSeconds of Switch #xxx
- Precision: Values in Integer, Mean in Double
  - Line#1: RTT of Packet#1; RTT of Packet#2; ...
  - Line#2: Mean (Line#1)

tsl.xxx.txt

- TimeStampValues in MilliSeconds of Switch #xxx
- Default: 1 Second = 1 Intervall
- Precision: Mean in Double, Values in Integer
  - Line#1: OutgoingMeanIATperIntervall (OF_PACKET_IN);
  - Line#2: IncomingMeanIATperIntervall (OF_PACKET_OUT);
  - Line#3: OutgingTimeStamp(OF_PACKET_IN);
  - Line#4: IncomingTimeStamps(OF_PACKET_OUT);
