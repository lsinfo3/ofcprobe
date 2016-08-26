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
package de.uniwuerzburg.info3.ofcprobe.util;

import org.openflow.protocol.OFMatch;

/**
 * MetaOFMatch which overrides OFMatch.equals()
 *
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public class OFMatsch extends OFMatch {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     */
    public OFMatsch() {
        super();
    }

    /**
     * Constructor from match
     *
     * @param match the Match to scan
     */
    public OFMatsch(OFMatch match) {
        fromMatch(match);
    }

    /**
     * Read Settings from match
     *
     * @param match the match
     */
    public void fromMatch(OFMatch match) {
        this.fromString(match.toString());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof OFMatch)) {
            return false;
        }
        OFMatch other = (OFMatch) obj;

        if (!Util.equalsFlow(this, other)) {
            System.err.println(this.toString() + ";" + other.toString() + "; false");
        }

        return Util.equalsFlow(this, other);

    }
}
