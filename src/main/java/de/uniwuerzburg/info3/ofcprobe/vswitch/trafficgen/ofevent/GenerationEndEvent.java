/* 
 * Copyright 2016 christopher.metter.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.uniwuerzburg.info3.ofcprobe.vswitch.trafficgen.ofevent;

import de.uniwuerzburg.info3.ofcprobe.vswitch.connection.IOFConnection;

/**
 * Event describing the End of Traffic Generation
 *
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 */
public class GenerationEndEvent implements IOFEvent {

    private EventType type;
    private IOFConnection ofSwitch;

    public GenerationEndEvent(IOFConnection ofSwitch) {
        this.ofSwitch = ofSwitch;
        this.type = EventType.GENERATION_END;
    }

    @Override
    public EventType getType() {
        return this.type;
    }

    @Override
    public IOFConnection getCon() {
        return this.ofSwitch;
    }

    @Override
    public String toString() {
        String output = new String();
        output += "Type:" + this.type + ";Con:" + this.ofSwitch.toString();
        return output;
    }

}
