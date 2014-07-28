#OFCProbe


We present the platform-independent and flexible OpenFlow Controller Analysis Tool OFCProbe on this website. It features 
a scalable and modular architecture that allows a granular and deep analysis of the controllers behaviors and 
characteristics. OpenFlow controllers can be pushed to their limit and the bottle-necks can be investigated.
The tool allows the emulation of virtual switches that each provide sophisticated statistics about the controller 
behavior.

## Building OFCProbe

- Download and install Maven
- make sure that your maven setup allows new repositories
- Execute `mvn package` to download dependencies and compile OFCProbe.
- The result can be found in `target/ofcprobe-*-one-jar.jar`.


## Tutorial

###Requirements:

- Java
- OpenFlow Controller
- Preferably a Linux System (Shell Scripts + Screen Usage in Scripts provided)
- Passwordless SSH-Connection between OF Controller Host and OFCProbe Host for User openflow
- Optional: SNMP Server on Controller Host (for CPU and RAM Utilization of OF Controller Host)
- Build it as described above or download [OFCProbe](https://github.com/lsinfo3/ofcprobe/releases)

###Demo:

- Optional: Change Controller IP/Port in demo.ini (default: localhost:6633)
- To Start a sample Setup execute `java -jar target/ofcprobe-*-one-jar.jar demo.ini`

###Preperations:

see [PREPERATIONS.md](https://github.com/lsinfo3/ofcprobe/blob/master/PREPERATIONS.md)

###Simulation Sequence

see [BEST_EFFORT.md](https://github.com/lsinfo3/ofcprobe/blob/master/BEST_EFFORT.md)


### Topology Testing

see [TOPOLOGY_EMU.md](https://github.com/lsinfo3/ofcprobe/blob/master/TOPOLOGY_EMU.md)

####Random IAT Values after Distribution

Example for NormalDistribution, mean = 10, stdev=5:

* trafficGenConfig.iatType = 1
* trafficGenConfig.iatDistribution = Normal
* trafficGenConfig.iatDistributionParamter1 = 10
* trafficGenConfig.iatDistributionParamter2 = 5

Individual Settings for Switches

* Set config.checkForIndividualSwitchSettings = true in config.ini on OFCProbeHost
* Check ofSwitch.ini and change it


### Statistics Content

see [STATISTICS_CONTENT.md](https://github.com/lsinfo3/ofcprobe/blob/master/STATISTICS_CONTENT.md)