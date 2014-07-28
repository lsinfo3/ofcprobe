# OFCPROBE Best Effort Testing
 
##1 Host

On OFCProbe Host:

1. Use provided config.ini

On OF Controller Host

1. Ensure that NO controller is running
2. Go To Directory of launch_config.sh
3. Start Testing:

        ./launch_config.sh 5 flood_be

    This will start Best Effort Testing with 5 Runs on 1 OFCProbe Host emulating [1,5:5:100] Switches with 8 Threads. 
    After the Simlation has finished, you will find a directory on < OFCProbeDirectory>\flood_be\. 
    All statistics Files are found there.

##Multiple Hosts
On OFCProbe Host

1. Use provided config.ini
2. Do not Forget to Change config.startDpid --> Over all OFCProbeHosts, the DPIDs must not overlap
3. Example Scenario with 4 Hosts:
4. * 100 Switches in Total --> 25 Switches in Total per Host
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
