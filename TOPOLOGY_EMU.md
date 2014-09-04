# OFCPROBE Topology Testing

##One Host - Topology Testing
On OFCProbe Host

1. Use provided config.top.ini (dont forget renaming config file to config.ini)
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

##GraphML Import
OFCProbe offers the option to import a network topology in the GraphML format. GraphML is a XML-based file format which describes the structural properties of a graph. A lot of GraphML files of real-world network topologies can be found on the [website](http://www.topology-zoo.org/index.html) of the  Internet Topology Zoo project,which has collected data of  over 250 networks.

On OFCProbe Host

1.  Use provided config.top.ini (dont forget renaming config file to config.ini)
2. Copy the GraphML file of the desired network topology into the OFCProbe Directory 
3. Ensure that the parameters config.hasTopolgy and config.hasGraphml are set true
4. Enter the name of the GraphML file for the parameter config.graphml, e.g. `config.grapml = Aarnet.graphml`

On OF Controller Host

1. Ensure that NO controller is running
2. Start Testing:

         ./launch_config_top.sh 5 flood_aarnet

This will start a topology emulation of the chosen GraphML file. The nodes and edges are imported and a corresponding topology.ini file is automatically created. After the Simlation has finished, you will find a directory on <OFCProbeDirectory>\flood_aarnet\. 
All statistics Files are found there. 
