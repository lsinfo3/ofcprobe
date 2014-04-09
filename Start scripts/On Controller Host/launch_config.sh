#/bin/bash!
#BenchmarkingHosts:
IPS[0]="192.168.42.21"
#IPS[1]="192.168.42.22"
#IPS[2]="192.168.42.31"
#IPS[3]="192.168.42.32"

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

maxswitchnum=100
if [ ${#IPS[@]} -eq 2 ]
then
	maxswitchnum=50
fi
if [ ${#IPS[@]} -eq 4 ]
then
        maxswitchnum=25
fi

runArray=$(seq 5 5 $maxswitchnum)
runArray=("1" "${runArray[@]}")


for i in $(seq 1 1 $1)
do
	#runs 1,5-maxswitchnum
	for j in ${runArray[@]}
	do
		echo ">Run #$i, Switch #$j"
		echo ">>Starting Controller"
		$controllerstart
		
		echo ">>Kill all alive BenchingTools"
		for ip in ${IPS[@]}
		do
			ssh openflow@${ip} killall -9 java
		done
		sleep 8s
	
		echo ">>Start BenchingTools"
		for ip in ${IPS[@]}
		do
			ssh openflow@${ip} "~/ssh_redirector.sh $j"
		done
		sleep 111s
		
		echo ">>Kill Controller"
		stop_controller.sh
		sleep 5s
		
		echo ">>Moving Logfiles"
		target=$(printf "%03d" $j)
		for ip in ${IPS[@]}
		do
			ssh openflow@${ip} mv "/home/openflow/ofcprobe/MyLog.log" "/home/openflow/ofcprobe/statistics/$target/"
		done
	done
	
	echo ">Moving statistics to statistics_$i"
	for ip in ${IPS[@]}
	do
		ssh openflow@${ip} mkdir "/home/openflow/ofcprobe/statistics_$i/"
		ssh openflow@${ip}  mv "/home/openflow/ofcprobe/statistics/" "/home/openflow/ofcprobe/statistics_$i/"
	done
	
done

echo "Moving Statistics_* into new Dir and taring"
for ip in ${IPS[@]}
do
	ssh openflow@${ip}  mkdir "/home/openflow/ofcprobe/$controller"
	ssh openflow@${ip}  mv "/home/openflow/ofcprobe/statistics_*" "/home/openflow/ofcprobe/$controller/"
#	ssh openflow@${ip}  tar czf "/home/openflow/ofcprobe/$controller.tar.gz" "/home/openflow/ofcprobe/$controller/" &
done
