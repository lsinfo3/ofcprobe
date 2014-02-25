#/bin/bash
cd ~/controller/pox/
screen -AmdS pox bash -c './pox.py forwarding.l2_learning'
#screen -AmdS pox bash -c './pox.py forwarding.l2_learning topology openflow.discovery openflow.topology'
