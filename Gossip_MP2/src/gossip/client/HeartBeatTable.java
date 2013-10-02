package gossip.client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class HeartBeatTable {

	public final static long WAIT_TIME = 1000;
	public final static long CLEAN_UP = 1000;

	// The key will always be the ip address
	ConcurrentHashMap<String, HeartBeat> heartBeatMap;
	ConcurrentHashMap<String, Long> localTimeMap;
	ConcurrentHashMap<String, HeartBeat> hasFailedMap;
	Log logger;
	public HeartBeat own;

	/**
	 * Constructor
	 * 
	 * @param own
	 *            -Computer's own heart beat
	 */
	public HeartBeatTable(HeartBeat own) {
		this.heartBeatMap = new ConcurrentHashMap<String, HeartBeat>();
		this.own = own;
		this.localTimeMap = new ConcurrentHashMap<String, Long>();
		this.hasFailedMap = new ConcurrentHashMap<String, HeartBeat>();
		this.heartBeatMap.put(own.getIpAddress(), own);
		this.localTimeMap.put(own.getIpAddress(), System.currentTimeMillis());
		try {
			this.logger = new Log("/tmp/machine."
					+ InetAddress.getLocalHost().getHostAddress() + ".log");
		} catch (UnknownHostException e) {
			e.printStackTrace();

		}
	}

	/**
	 * update table -the server should call this everytime it receives a list
	 * 
	 * @param receivedTable
	 */
	public synchronized void updateTable(ArrayList<HeartBeat> receivedTable) {
		for (HeartBeat hb : receivedTable) {
			updateTable(hb);
		}
	}

	/**
	 * updates a single heart beat
	 * 
	 * @param hb
	 */
	public synchronized void updateTable(HeartBeat hb) {

		// we don't want to change our own heart beat
		if (hb.getIpAddress() == this.own.getIpAddress()) {
			return;
		}

		String key = hb.getIpAddress();
		HeartBeat value = hb;
		if (!this.heartBeatMap.containsKey(key)) {// check if new node
			this.heartBeatMap.put(key, value);
			this.localTimeMap.put(key, System.currentTimeMillis());
			logger.writeLogMessage("ADD "+ key);

		} else {
			HeartBeat old = this.heartBeatMap.get(key);// merge new values
			if (old.getHeartBeatCounter() < value.getHeartBeatCounter()) {
				this.heartBeatMap.put(key, value);
				this.localTimeMap.put(key, System.currentTimeMillis());
			}

		}
	}

	/**
	 * increases own heart beat
	 */
	public synchronized void increaseOwnHeartBeat() {
		long ownHeartBeat = own.getHeartBeatCounter();
		own.setAndCompareHeartBeatCounter(ownHeartBeat + 1);
		this.localTimeMap.put(own.getIpAddress(), System.currentTimeMillis());
	}

	/**
	 * removes heart beat from all the lists
	 * 
	 * @param hb
	 */
	public synchronized void removeHeartBeat(HeartBeat hb) {
		String key = hb.getIpAddress();
		this.heartBeatMap.remove(key);
		this.hasFailedMap.remove(key);
		this.localTimeMap.remove(key);
	}

	/**
	 * The client should call this every t seconds
	 * 
	 * @return ArrayList of heart beats
	 */
	public synchronized ArrayList<HeartBeat> maintain() {
		increaseOwnHeartBeat();
		checkForFailures();
		cleanUp();
		return getCurrentHeartBeatTable();
	}

	/**
	 * Checks for failures
	 */
	private void checkForFailures() {
		Collection<HeartBeat> collection = this.heartBeatMap.values();
		long currentTime = System.currentTimeMillis();
		for (HeartBeat hb : collection) {
			long localTime = this.localTimeMap.get(hb.getIpAddress());
			if (currentTime - localTime >= WAIT_TIME) {
				this.hasFailedMap.put(hb.getIpAddress(), hb);
				logger.writeLogMessage("Marked Fail " + hb.getIpAddress());
			}
		}

	}

	/**
	 * Cleans up heart beats from the table that are marked for falure
	 */
	private void cleanUp() {
		Collection<HeartBeat> collection = this.hasFailedMap.values();
		long currentTime = System.currentTimeMillis();
		for (HeartBeat hb : collection) {
			long localTime = this.localTimeMap.get(hb.getIpAddress());
			if (currentTime - localTime >= WAIT_TIME + CLEAN_UP) {
				this.removeHeartBeat(hb);
				logger.writeLogMessage("Cleanup" + hb.getIpAddress());
			}
		}

	}

	/**
	 * Gets the current heart beats in the table
	 * 
	 * @return
	 */
	private synchronized ArrayList<HeartBeat> getCurrentHeartBeatTable() {
		// return all heart beat values
		ArrayList<HeartBeat> retVal = new ArrayList<HeartBeat>();
		Collection<HeartBeat> collection = this.heartBeatMap.values();
		for (HeartBeat hb : collection) {
			retVal.add(hb);
		}
		return retVal;
	}

	/**
	 * Returns the size of the heart beat map
	 * 
	 * @return
	 */
	public int getSize() {
		return this.heartBeatMap.size();
	}
}
