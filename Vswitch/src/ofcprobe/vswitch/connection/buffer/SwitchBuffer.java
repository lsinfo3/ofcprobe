package ofcprobe.vswitch.connection.buffer;

/**
 * SwitchBuffer using a java bool array
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public class SwitchBuffer {
	
	/**
	 * The Size of the Buffer
	 */
	private int bufferSize;
	/**
	 * The Bool Array
	 */
	private boolean[] buffer;
	/**
	 * Last used BitBuffer
	 */
	private int lastBuffer;
	
	/**
	 * Constructor
	 * @param bufferSize the size
	 */
	public SwitchBuffer(int bufferSize){
		this.bufferSize = bufferSize;
		this.buffer = new boolean[bufferSize];
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
		while (this.buffer[this.lastBuffer]) {
			this.lastBuffer++;
			if (this.lastBuffer >= this.bufferSize) {
				this.lastBuffer = 0;
			}
		}
		return this.lastBuffer;
	}
	
	/**
	 * Frees the Buffer[bufferId]
	 * @param bufferId
	 */
	public void freeBuffer(int bufferId){
		if (bufferId < this.bufferSize){
			this.buffer[bufferId] = false;
		}
	}
	

}
