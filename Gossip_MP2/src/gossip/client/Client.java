package gossip.client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * This class detects failures
 * 
 * @author etubil2
 * 
 */
public class Client {

	public static ArrayList<HeartBeat> heartBeatList = new ArrayList<HeartBeat>(); // Local
																					// membership
																					// list
	private static long WAIT_TIME = 1000; // how long until a computer will be
	// marked as failed
	private static long CLEAN_UP_TIME; // how long to wait until we are able to

	public Log logger;

	public Client(Log logger) {
		this.logger = logger;
	}

	public void start() throws UnknownHostException {
		Thread listenerThread = new Thread(new ServerHeartBeatListener(logger));
		listenerThread.start();
		Thread senderThread = new Thread(new ClientHeartBeatSender(logger));
		senderThread.start();
	}

	public void updateCounter(ArrayList<HeartBeat> ReceivedList)
			throws UnknownHostException {
		// TODOs
		synchronized (Client.heartBeatList) {

			long currentTime = System.currentTimeMillis();
			boolean nodeFound = false;
			for (HeartBeat receivedHb : ReceivedList) {
				// Nodes other than local host
				if (receivedHb.getIpAddress() != (InetAddress.getLocalHost()
						.toString())) {
					for (HeartBeat hbLocal : heartBeatList) {
						// Update Hb Counter if IPAddress matches local list
						if (receivedHb.getIpAddress().equals(
								hbLocal.getIpAddress())) {
							nodeFound = true;
							hbLocal.setAndCompareHeartBeatCounter(receivedHb
									.getHeartBeatCounter());
						}
					}
					if (!nodeFound) {
						// add new node to local list
						heartBeatList.add(receivedHb);
					}
				}
				checkForFailure(receivedHb, currentTime);
				if (receivedHb.getFailed()) {
					cleanUp(receivedHb, currentTime);
				}
			}
		}
	}

	/**
	 * Cleans up and removes the heart beat from the table
	 * 
	 * @param hb
	 */
	private void cleanUp(HeartBeat hb, long currentTime) {
		if (currentTime - hb.getLocalTime() > WAIT_TIME + CLEAN_UP_TIME) {
			heartBeatList.remove(hb);
			System.out.println(Thread.currentThread().getName() + ": "
					+ hb.getIpAddress() + " removed from list");
			logger.writeLogMessage("");
		}

	}

	/**
	 * Checks for failure
	 * 
	 * @param hb
	 * @param currentTime
	 */
	private void checkForFailure(HeartBeat hb, long currentTime) {
		if (currentTime - hb.getLocalTime() >= WAIT_TIME) {
			hb.setFailed();
			System.out.println(Thread.currentThread().getName() + ": "
					+ hb.getIpAddress() + " failed");
			logger.writeLogMessage("");
		}

	}
}