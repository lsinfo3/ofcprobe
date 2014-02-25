#/bin/bash
cd ~/controller/floodlight/target/
#cd ~/controller/latest_floodlight/target/
screen -AmdS floodlight sudo java -jar floodlight.jar
