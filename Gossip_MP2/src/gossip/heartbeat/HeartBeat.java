package gossip.heartbeat;

import java.io.Serializable;

public class HeartBeat implements Serializable {

	/**
	 * Serial Version ID
	 */
	private static final long serialVersionUID = 1L;

	private String ipAddress;
	private long heartBeatCounter;
	private int incarnationNumber;

	/**
	 * Constructor
	 * 
	 * @param ID
	 */
	public HeartBeat(String ip) {
		this.ipAddress = ip;
		heartBeatCounter = 0;
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
	public int increaseIncarnationNumber() {
		return ++this.incarnationNumber;
	}

	/**
	 * Gets the incarnation number - Every time a computer joins the incarnation
	 * number increases
	 * 
	 * @return
	 */
	public int getIncarnationNumber() {
		return this.incarnationNumber;
	}
}
