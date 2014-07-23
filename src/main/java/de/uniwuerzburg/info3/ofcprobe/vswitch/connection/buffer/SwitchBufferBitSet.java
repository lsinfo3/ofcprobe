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
