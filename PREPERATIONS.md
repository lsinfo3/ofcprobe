#OFCPROBE Preperations:

##Preperations On OFCProbe Host

1. Copy OFCProbe.java and your configurations files to a directory
2. Copy On OFCProbe Host\ssh_redirector.sh to home dir of user openflow
3. Open ssh_redirector.sh and change the path in Line#3 to directory of 1. (and change config-file name in Line4)

##Preperations On OF Controller Host

1. Install Controller(s) according to their installation instructions
2. Copy Contents of On Controller Host to directory of your choosing on OF Controller Host
3. You will Find Folders for each Controller, For each Controller you want to operate, do the following: 
  1. Go to Directory according to your Controller (e.g. Floodlight->>On Controller Host\flood)
  2. Open start.sh
  3. Change Directory in Line#2
4. Open launch_config.sh
5. Change IP(s) at the beginning