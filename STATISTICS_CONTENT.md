# OFCPROBE Statistics Content

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
