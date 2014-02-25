#/bin/bash!
#path containing ofcprobe.jar
cd /home/openflow/ofcprobe/
screen -AmdS ofcprobe bash -c "java -jar ofcprobe.jar config.ini $1"
