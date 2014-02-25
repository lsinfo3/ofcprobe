#/bin/bash
cd ~/controller/nox/build/src/
screen -AmdS nox bash -c './nox_core -t 4 -i ptcp: switch'
#screen -AmdS nox bash -c './nox_core -t 4 -i ptcp: openflow-manager'
