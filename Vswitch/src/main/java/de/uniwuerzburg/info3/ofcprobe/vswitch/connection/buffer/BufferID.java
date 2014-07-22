/**
 * 
 */
package de.uniwuerzburg.info3.ofcprobe.vswitch.connection.buffer;

/**
 * The BufferId
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public class BufferID {
	
	/**
	 * The BufferId
	 */
	private int buffId;

	/**
	 * Constructor
	 * @param buffId the BuffId
	 */
	public BufferID(int buffId) {
		this.buffId = buffId;
	}
	
	/**
	 * Getter for BuffId
	 * @return the BuffId
	 */
	public int getBuffId() {
		return this.buffId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + buffId;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BufferID other = (BufferID) obj;
		if (buffId != other.buffId)
			return false;
		return true;
	}
	

}
