package ofcprobe.util;


import org.openflow.protocol.OFMatch;

/**
 * MetaOFMatch which overrides OFMatch.equals()
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public class OFMatsch extends OFMatch{

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
	 * @param match the Match to scan
	 */
	public OFMatsch(OFMatch match) {
		fromMatch(match);
	}
	
	/**
	 * Read Settings from match 
	 * @param match the match
	 */
	public void fromMatch(OFMatch match) {
		this.fromString(match.toString());
	}
	
	public boolean equals(Object obj){
		
//		System.err.println("IM NOT SERIOUS ANYMORE!");
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
        
        if(!Util.equalsFlow(this, other)) {
        		System.err.println(this.toString() + ";" + other.toString() + "; false");
        }
		
        return Util.equalsFlow(this, other);
		
	}
	
//	public int hashCode() {
//		System.err.println("some1 was once serious");
//		return 42; //gnihih
//	}

	
	
}
