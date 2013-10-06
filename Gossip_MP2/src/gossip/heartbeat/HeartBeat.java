package gossip.heartbeat;

import java.io.Serializable;
import java.sql.Timestamp;

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
	public long getHeartBeatCounter() {
		return heartBeatCounter;
	}

	/**
	 * gets the IP address that this heart beat belongs to
	 * 
	 * @return
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * Updates the heart beat counter only if the other heart beat is greater
	 * 
	 * @param otherHeartBeat
	 */
	public void setAndCompareHeartBeatCounter(long otherHeartBeat) {
		if (otherHeartBeat > this.heartBeatCounter) {
			this.heartBeatCounter = otherHeartBeat;
		}
	}

	/**
	 * Increases the incarnation number by 1
	 * 
	 * @return incarnation number
	 */
	public Timestamp setIncarnationTimeStamp() {
		return this.timesStamp = new Timestamp(System.currentTimeMillis());
	}

	/**
	 * Gets the timestamp when this heart beat object was created
	 * 
	 * @return
	 */
	public Timestamp getTimeStamp(){
		return this.timesStamp;
	}
}
