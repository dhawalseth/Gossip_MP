package gossip.client;

import java.io.Serializable;

public class HeartBeat implements Serializable {

	/**
	 * Serial Version ID
	 */
	private static final long serialVersionUID = 1L;

	private String ipAddress;
	private long heartBeatCounter;

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
			System.out.println("Update Count for: "+this.ipAddress);
			this.heartBeatCounter = otherHeartBeat;
		}
	}

}
