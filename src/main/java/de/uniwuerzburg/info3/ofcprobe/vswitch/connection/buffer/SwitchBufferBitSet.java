package de.uniwuerzburg.info3.ofcprobe.vswitch.connection.buffer;

import java.util.BitSet;

/**
 * SwitchBuffer using a java.util.BitSet.
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public class SwitchBufferBitSet {
	
	/**
	 * The Size of the Buffer
	 */
	private int bufferSize;
	/**
	 * The BitSet
	 */
	private BitSet buffer;
	/**
	 * Last used BitBuffer
	 */
	private int lastBuffer;
	
	/**
	 * Constructor
	 * @param bufferSize the size
	 */
	public SwitchBufferBitSet(int bufferSize){
		this.bufferSize = bufferSize;
		this.buffer = new BitSet(bufferSize);
		this.buffer.clear();
		this.lastBuffer = 0;
	}
	
	/**
	 * Maximum Size of the Buffer
	 * @return the Size
	 */
	public int getBufferSize(){
		return this.bufferSize;
	}
	
	/**
	 * Determines if this Buffer is full
	 * @return true -> full
	 */
	public boolean isFull(){
		return (this.lastBuffer >= this.bufferSize);
	}
	
	/**
	 * Next Free Buffer Id
	 * @return next ID
	 */
	public int getNextFreeBufferId(){
		this.lastBuffer = this.buffer.nextClearBit(0);
//		if (this.lastBuffer >= this.bufferSize) {
//			return 0;
//		}
		this.buffer.set(this.lastBuffer);
		return this.lastBuffer;
	}
	
	/**
	 * Frees the Buffer[bufferId]
	 * @param bufferId
	 */
	public void freeBuffer(int bufferId){
		if (bufferId < this.bufferSize && bufferId > 0){
			this.buffer.clear(bufferId);
		}
	}
	

}
