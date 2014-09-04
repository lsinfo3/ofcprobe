#OFCProbe
![OFCProbe](http://www3.informatik.uni-wuerzburg.de/research/ngn/ofcprobe/ofcprobe-logo.png)

We present the platform-independent and flexible OpenFlow Controller Analysis Tool OFCProbe on this website. It features 
a scalable and modular architecture that allows a granular and deep analysis of the controllers behaviors and 
characteristics. OpenFlow controllers can be pushed to their limit and the bottle-necks can be investigated.
The tool allows the emulation of virtual switches that each provide sophisticated statistics about the controller 
behavior.

## Table of Contents
- [Building](#building)
- [Tutorial](#tut)
  - [Requirements](#req)
  - [Demo](#demo)
  - [Preperations](#prep)
  - [Simulation Sequence](#simseq)
  - [Topology Testing](#topotest)
  - [Random IAT Values after Distribution](#radomdistri)
- [Statistics Content](#statcontent)
- [More Information](#infos)

## <a name="building"></a>Building OFCProbe

- Download and install Maven
- Make sure that your Maven setup allows new repositories
- Execute `mvn package` to download dependencies and compile OFCProbe.
- The result can be found in `target/ofcprobe-*-one-jar.jar`.


## <a name="tut"></a>Tutorial

### <a name="req"></a>Requirements:

- Java 7
- OpenFlow Controller
- Preferably a Linux System (Shell Scripts + Screen Usage in Scripts provided)
- Passwordless SSH-Connection between OF Controller Host and OFCProbe Host for User openflow
- Optional: SNMP Server on Controller Host (for CPU and RAM Utilization of OF Controller Host)
- Build it as described above or download [OFCProbe](https://github.com/lsinfo3/ofcprobe/releases)

### <a name="demo"></a>Demo:

- Optional: Change Controller IP/Port in demo.ini (default: localhost:6633)
- To Start a sample Setup execute `java -jar target/ofcprobe-*-one-jar.jar demo.ini`

### <a name="prep"></a>Preperations:

see [PREPERATIONS](https://github.com/lsinfo3/ofcprobe/blob/master/PREPERATIONS.md)

### <a name="simseq"></a>Simulation Sequence

see [BEST_EFFORT](https://github.com/lsinfo3/ofcprobe/blob/master/BEST_EFFORT.md)


### <a name="topotest"></a>Topology Testing

see [TOPOLOGY_EMU](https://github.com/lsinfo3/ofcprobe/blob/master/TOPOLOGY_EMU.md)

#### <a name="randomdistri"></a>Random IAT Values after Distribution

Example for NormalDistribution, mean = 10, stdev=5:

* trafficGenConfig.iatType = 1
* trafficGenConfig.iatDistribution = Normal
* trafficGenConfig.iatDistributionParamter1 = 10
* trafficGenConfig.iatDistributionParamter2 = 5

Individual Settings for Switches

* Set config.checkForIndividualSwitchSettings = true in config.ini on OFCProbeHost
* Check ofSwitch.ini and change it


### <a name="statcontent"></a>Statistics Content

see [STATISTICS_CONTENT](https://github.com/lsinfo3/ofcprobe/blob/master/STATISTICS_CONTENT.md)

### <a name="infos"></a>More Infos
see [OFCProbe@LS3](http://www3.informatik.uni-wuerzburg.de/research/ngn/ofcprobe/ofcprobe_instructions)