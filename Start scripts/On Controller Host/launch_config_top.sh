#/bin/bash!
#BenchmarkingHosts:
IP="192.168.42.21"
#IP="192.168.42.22"
#IP="192.168.42.31"
#IP="192.168.42.32"

EXPECTED_ARGS=2
if [ $# -ne $EXPECTED_ARGS ]
then
	echo "Usage: 'launch.sh runCount Controllername'"
	exit -1
fi

#scripts starting the controller
if [[ $2 == *"flood"* ]]
then
	controllerstart=flood/start.sh
fi

if [[ $2 == *"nox"* ]]
then
	controllerstart=nox/start.sh
fi

if [[ $2 == *"beacon"* ]]
then
        controllerstart=beacon/start.sh
fi

if [[ $2 == *"pox"* ]]
then
        controllerstart=pox/start.sh
fi

controller=$2
echo ">>Killing Controller"
stop_controller.sh
sleep 3s



for i in $(seq 1 1 $1)
do
	
		echo ">Run #$i"
		echo ">>Starting Controller"
		$controllerstart
		
		echo ">>Kill all alive BenchingTools"
		ssh openflow@${IP} killall -9 java
		
		sleep 8s
	
		echo ">>Start BenchingTools"
		ssh openflow@${IP} "~/ssh_redirector.sh"
		sleep 111s
		
		echo ">>Kill Controller"
		stop_controller.sh
		sleep 5s
		
		echo ">>Moving Logfiles"
		ssh openflow@${IP} mv "/home/openflow/ofcprobe/MyLog.log" "/home/openflow/ofcprobe/statistics/"
		
	
	
	echo ">Moving statistics to statistics_$i"
	ssh openflow@${IP} mkdir "/home/openflow/ofcprobe/statistics_$i/"
	ssh openflow@${IP}  mv "/home/openflow/ofcprobe/statistics/" "/home/openflow/ofcprobe/statistics_$i/"
	
done

echo "Moving Statistics_* into new Dir and taring"
ssh openflow@${IP}  mkdir "/home/openflow/ofcprobe/$controller"
ssh openflow@${IP}  mv "/home/openflow/ofcprobe/statistics_*" "/home/openflow/ofcprobe/$controller/"
#ssh openflow@${ip}  tar czf "/home/openflow/ofcprobe/$controller.tar.gz" "/home/openflow/ofcprobe/$controller/" &

