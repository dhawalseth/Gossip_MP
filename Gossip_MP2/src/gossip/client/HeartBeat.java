package gossip.client;

import java.io.Serializable;
import java.sql.Time;

public class HeartBeat implements Serializable {

	/**
	 * Serial Version ID
	 */
	private static final long serialVersionUID = 1L;

	private String ipAddress;
	private long heartBeatCounter;
	private long localTime;
	private boolean failed = false;
	private int id = 0;

	/**
	 * Constructor
	 * 
	 * @param ID
	 */
	public HeartBeat(String ip) {
		this.ipAddress = ip;
		heartBeatCounter = 0;
		localTime = System.currentTimeMillis();
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
	 * Sets the computer as failed
	 */
	public void setFailed() {
		failed = true;
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
	 * Gets the local time
	 * 
	 * @return
	 */
	public long getLocalTime() {
		return localTime;
	}

	/**
	 * Updates the local time
	 */
	public void updateLocalTime() {
		this.localTime = System.currentTimeMillis();
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
	 * Gets the status of failure
	 */
	public boolean getFailed() {
		return this.failed;
	}

	/**
	 * Gets the ID
	 */
	public int getID() {
		return this.id;
	}
}
