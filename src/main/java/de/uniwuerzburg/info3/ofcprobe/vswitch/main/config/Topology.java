/*
 * Copyright (C) 2014 Christopher Metter
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.uniwuerzburg.info3.ofcprobe.vswitch.main.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Topology for our ofSwitches.
 *
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public class Topology {

    /**
     * The debug instance for this object
     */
    private static final Logger logger = LoggerFactory.getLogger(Topology.class);

    private int countSwitches;
    /**
     * Filename of topology.ini File
     */
    private String filename;
    /**
     * The Topology Matrix first Dimension: Source Switch Second Dimension:
     * Target Switch [1][2]=5 --> switch 1 is connected to switch2 on switch2's
     * port 5
     */
    private int[][] connections;

    private Config config;

    private boolean hasHostMapping;

    private HostMapper hostMapper;

    /**
     * Constructor
     *
     * @param config Configfile
     */
    public Topology(Config config) {
        this.filename = "topology.ini";
        this.config = config;
        this.countSwitches = this.config.getCountSwitches();
        this.connections = new int[this.countSwitches][this.countSwitches];
        this.hasHostMapping = false;
    }

    /**
     * Loads Topogoly File
     */
    public void loadTopoFromFile() {

        File filou = new File(this.filename);
        try {
            BufferedReader in = new BufferedReader(new FileReader(filou));
            String line = null;
            // Try line by line
            while ((line = in.readLine()) != null) {
                // Fields containing '#' are comment fields
                if (!line.contains("#")) {
                    int ofSwitch1 = -1;
                    int ofSwitch2 = -1;
                    int portOfSwitch1 = -1;
                    int portOfSwitch2 = -1;
                    // Split by '==' for Switch1 - Switch2
                    int index = line.indexOf("==");
                    String leftSwitch = line.substring(0, index);
                    String rightSwitch = line.substring(index + 2, line.length());

                    // Split by ':' for Switch - Port
                    String[] leftOfSwitch = leftSwitch.split(":");
                    if (leftOfSwitch.length == 2) {
                        ofSwitch1 = Integer.parseInt(leftOfSwitch[0]);
                        portOfSwitch1 = Integer.parseInt(leftOfSwitch[1]);
                    }

                    String[] rightOfSwitch = rightSwitch.split(":");
                    if (rightOfSwitch.length == 2) {
                        ofSwitch2 = Integer.parseInt(rightOfSwitch[0]);
                        portOfSwitch2 = Integer.parseInt(rightOfSwitch[1]);
                    }

                    // set Link
                    setLink(ofSwitch1, portOfSwitch1, ofSwitch2, portOfSwitch2);
                }

            }
            in.close();
            logger.info("Topology File has been successfully loaded!");
            logger.trace(this.toString());

        } catch (FileNotFoundException e) {
            logger.error("Cannot Find Topology File!");
            this.config.setHasTopology(false);
        } catch (IOException e) {
            logger.error("I/O Exception during Topology File Reading! {}", e);
            this.config.setHasTopology(false);
        }
    }

    /**
     * Sets Link between two ofSwitches
     *
     * @param ofSwitch1 ofSwitch1
     * @param portofSwitch1 Port of ofSwitch1
     * @param ofSwitch2 ofSwitch2
     * @param portofSwitch2 Port of ofSwitch2
     */
    public void setLink(int ofSwitch1, int portofSwitch1, int ofSwitch2, int portofSwitch2) {
        // Check if Parameters are legit
        if ((ofSwitch1 <= this.countSwitches && ofSwitch1 > 0) && (ofSwitch2 <= this.countSwitches && ofSwitch2 > 0)) {
            this.connections[ofSwitch1 - 1][ofSwitch2 - 1] = portofSwitch2;
            this.connections[ofSwitch2 - 1][ofSwitch1 - 1] = portofSwitch1;
            logger.trace("Connection {}:{}=={}:{} has been set.", ofSwitch1, portofSwitch1, ofSwitch2, portofSwitch2);
        } else {
            logger.error("Cannot Set Connection {}:{}=={}:{} due to Parameters out of Range!", ofSwitch1, portofSwitch1, ofSwitch2, portofSwitch2);
        }
    }

    /**
     * Checks if there is Link between two ofSwitches
     *
     * @param ofSwitch1 ofSwitch1
     * @param portofSwitch1 Port of ofSwitch1
     * @param ofSwitch2 ofSwitch2
     * @param portofSwitch2 Port of ofSwitch2
     * @return
     */
    public boolean isLink(int ofSwitch1, int portofSwitch1, int ofSwitch2, int portofSwitch2) {
        // Check if Parameters are legit
        if ((ofSwitch1 <= this.countSwitches && ofSwitch1 > 0) && (ofSwitch2 <= this.countSwitches && ofSwitch2 > 0)) {
            if (this.connections[ofSwitch1 - 1][ofSwitch2 - 1] == portofSwitch2 && this.connections[ofSwitch2 - 1][ofSwitch1 - 1] == portofSwitch1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the DPID of the connected Switch for the Connection dpid:localPort
     * -> TargetDPID
     *
     * @param dpid SourceDPID
     * @param localPort Port on sourceDPID
     * @return targetDPID
     */
    public Long getConnectedOfSwitch(long dpid, short localPort) {
        int ofSwitch = (int) dpid;
        if (ofSwitch <= this.countSwitches) {
            for (int i = 0; i < this.connections[ofSwitch - 1].length; i++) {
                if (this.connections[i][ofSwitch - 1] == localPort) {
                    return (long) (i + 1);
                }
            }
        }
        return (long) -1;
    }

    /**
     * Returns a List of DPIDs who are connected to this ofSwitch
     *
     * @param ofSwitchDpid
     * @return List of DPIDs
     */
    public List<Long> getConnectedOfSwitches(long ofSwitchDpid) {
        int ofSwitch = (int) ofSwitchDpid;
        List<Long> connectedOfSwitches = new ArrayList<>();
        if (ofSwitch <= this.countSwitches) {
            for (int j = 0; j < this.connections[ofSwitch - 1].length; j++) {
                if (this.connections[ofSwitch - 1][j] > 0) {
                    connectedOfSwitches.add((long) j + 1);
                }
            }
        }
        return connectedOfSwitches;
    }

    /**
     * Gets you the Port# of me for connection src-me
     *
     * @param me Target
     * @param src Source
     * @return the Port#
     */
    public short getInPort(long me, long src) {
        int ofSwitchMe = (int) me;
        int ofSwitchSrc = (int) src;
        if (ofSwitchMe <= this.countSwitches && ofSwitchSrc <= this.countSwitches) {
            return (short) this.connections[ofSwitchSrc - 1][ofSwitchMe - 1];
        }
        return -1;
    }

    /**
     * Gets you the Port# of src for conncetion src-me
     *
     * @param me Target
     * @param src Source
     * @return the Port#
     */
    public int getOutPort(long me, long src) {
        int ofSwitchMe = (int) me;
        int ofSwitchSrc = (int) src;
        if (ofSwitchMe <= this.countSwitches && ofSwitchSrc <= this.countSwitches) {
            return this.connections[ofSwitchMe - 1][ofSwitchSrc - 1];
        }
        return -1;
    }

    /**
     * Prints Topology to Nice Format: ofSwitch1:Port==ofSwitch2:Port
     *
     * @return
     */
    @Override
    public String toString() {
        String topologyString = "---[Topology]---\n";

        for (int i = 0; i < this.connections.length; i++) {
            String rightHand = new String();
            String leftHand = new String();
            for (int j = 0; j < this.connections[i].length; j++) {
                leftHand = (i + 1) + ":" + this.connections[j][i];
                rightHand = (j + 1) + ":" + this.connections[i][j];
                if ((this.connections[i][j] > 0 && this.connections[j][i] > 0) && (!topologyString.contains(leftHand) && !topologyString.contains(rightHand))) {
                    topologyString += (i + 1) + ":" + this.connections[j][i] + "==" + (j + 1) + ":" + this.connections[i][j];
                    topologyString += "\n";
                }
            }
        }

        return topologyString;
    }

    /**
     * String representation of Links of ofSwitch
     *
     * @param ofSwitch ofSwitch
     * @return String representation
     */
    public String printConnections(int ofSwitch) {
        NumberFormat formatter = new DecimalFormat("#000");
        String topologyString = "Connections for ofSwitch#" + formatter.format(ofSwitch) + "\n";
        String rightHand = new String();
        String leftHand = new String();
        for (int j = 0; j < this.connections[ofSwitch - 1].length; j++) {
            leftHand = (ofSwitch) + ":" + this.connections[j][ofSwitch - 1];
            rightHand = (j + 1) + ":" + this.connections[ofSwitch - 1][j];
            if ((this.connections[ofSwitch - 1][j] > 0 && this.connections[j][ofSwitch - 1] > 0) && (!topologyString.contains(leftHand) && !topologyString.contains(rightHand))) {
                topologyString += (ofSwitch) + ":" + this.connections[j][ofSwitch - 1] + "==" + (j + 1) + ":" + this.connections[ofSwitch - 1][j];
                topologyString += "\n";
            }
        }
        return topologyString;
    }

    /**
     * Gets the List of Portnumbers of Ports which are not connected toanother
     * Port of Switch with dpid
     *
     * @param dpid the switch with dpid
     * @param count maximum number of hosts per switch
     * @return List of Portnumbers
     */
    public List<Short> getFreePorts(long dpid, int count) {
        List<Short> output = new ArrayList<>();
        int ofSwitch = (int) dpid;
        if (ofSwitch <= this.connections.length) {
            int numPorts = this.config.getSwitchConfig().getPortCountperSwitch();
            for (int i = 0; i < numPorts; i++) {
                if (getConnectedOfSwitch(dpid, (short) (i + 1)) == -1) {
                    output.add((short) (i + 1));
                }
            }
        }
        if (count != -1 && output.size() > count) {
            output = output.subList(0, count);
        }
        return output;
    }

    public void setHasHostMapping(boolean flag) {
        this.hasHostMapping = flag;
    }

    public boolean hasHostMapping() {
        return this.hasHostMapping;
    }

    public HostMapper getHostMapping() {
        return this.hostMapper;
    }

    public void setHostMapper(HostMapper hostMap) {
        this.hostMapper = hostMap;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((config == null) ? 0 : config.hashCode());
        result = prime * result + Arrays.hashCode(connections);
        result = prime * result + countSwitches;
        result = prime * result
                + ((filename == null) ? 0 : filename.hashCode());
        result = prime * result + (hasHostMapping ? 1231 : 1237);
        result = prime * result
                + ((hostMapper == null) ? 0 : hostMapper.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Topology other = (Topology) obj;
        if (config == null) {
            if (other.config != null) {
                return false;
            }
        } else if (!config.equals(other.config)) {
            return false;
        }
        if (!Arrays.deepEquals(connections, other.connections)) {
            return false;
        }
        if (countSwitches != other.countSwitches) {
            return false;
        }
        if (filename == null) {
            if (other.filename != null) {
                return false;
            }
        } else if (!filename.equals(other.filename)) {
            return false;
        }
        if (hasHostMapping != other.hasHostMapping) {
            return false;
        }
        if (hostMapper == null) {
            if (other.hostMapper != null) {
                return false;
            }
        } else if (!hostMapper.equals(other.hostMapper)) {
            return false;
        }
        return true;
    }
}
