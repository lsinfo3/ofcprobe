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
