package gossip.heartbeat;

import java.io.Serializable;
import java.sql.Timestamp;
/**
 * Heart Beat Class -this is sent over the network
 * @author etubil2
 *
 */
public class HeartBeat implements Serializable {

	/**
	 * Serial Version ID
	 */
	private static final long serialVersionUID = 1L;

	private String ipAddress;
	private long heartBeatCounter;
	private Timestamp timesStamp;
	

	/**
	 * Constructor
	 * 
	 * @param ID
	 */
	public HeartBeat(String ip, boolean setTimeStamp) {
		this.ipAddress = ip;
		heartBeatCounter = 0;
		if(setTimeStamp)
		this.setIncarnationTimeStamp();
	}

	/**
	 * gets the heart beat counter
	 * 
	 * @return
	 */
	public synchronized long getHeartBeatCounter() {
		return heartBeatCounter;
	}

	/**
	 * gets the IP address that this heart beat belongs to
	 * 
	 * @return
	 */
	public synchronized String getIpAddress() {
		return ipAddress;
	}

	/**
	 * Updates the heart beat counter only if the other heart beat is greater
	 * 
	 * @param otherHeartBeat
	 * @return 
	 */
	public synchronized void setAndCompareHeartBeatCounter(long otherHeartBeat) {
		if (otherHeartBeat > this.heartBeatCounter) {
			this.heartBeatCounter = otherHeartBeat;
		}
	}

	/**
	 * Increases the incarnation number by 1
	 * 
	 * @return incarnation number
	 */
	public synchronized Timestamp setIncarnationTimeStamp() {
		return this.timesStamp = new Timestamp(System.currentTimeMillis());
	}

	/**
	 * Gets the timestamp when this heart beat object was created
	 * 
	 * @return
	 */
	public synchronized Timestamp getTimeStamp(){
		return this.timesStamp;
	}
}
