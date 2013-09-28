package gossip.client;

import java.io.Serializable;

public class HeartBeat implements Serializable {

	private String ipAddress;
	private int heartBeatCounter;
	private int localTime;

	public int getHeartBeatCounter() {
		return heartBeatCounter;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public int getLocalTime() {
		return localTime;
	}

}
