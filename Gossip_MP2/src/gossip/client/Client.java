package gossip.client;

import java.io.IOException;
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
	public static long WAIT_TIME = 1000; // how long until a computer will be
	// marked as failed
	private static long CLEAN_UP_TIME; // how long to wait until we are able to

	public Log logger;

	public Client(Log logger) {
		this.logger = logger;
	}

	public void start() throws IOException {
		Thread listenerThread = new Thread(new ServerHeartBeatListener(logger));
		listenerThread.start();
		Thread senderThread = new Thread(new ClientHeartBeatSender(logger));
		senderThread.start();
	}

	/**
	 * Updates the counters in the heart beat list when given the received list
	 * @param ReceivedList
	 * @throws UnknownHostException
	 */
	public void updateCounter(ArrayList<HeartBeat> ReceivedList)
			throws UnknownHostException {
		// TODOs
		synchronized (Client.heartBeatList) {

			for (HeartBeat hbLocal : heartBeatList) {
				// Nodes other than local host
				if (hbLocal.getIpAddress() != (InetAddress.getLocalHost()
						.getHostAddress())) {
					for (HeartBeat receivedHb : ReceivedList) {
						// Update Hb Counter if IPAddress matches local list
						_updateAndCompareHeartBeats(hbLocal, receivedHb);
					}
				}
			}
			_findNewNodes(ReceivedList);
		}
	}

	/**
	 * Given a local heart beat and received heart beat, this function updates
	 * the value if they refer to the same computer.
	 * 
	 * @param hbLocal
	 * @param receivedHb
	 */
	private void _updateAndCompareHeartBeats(HeartBeat hbLocal,
			HeartBeat receivedHb) {
		if (receivedHb.getIpAddress().equals(hbLocal.getIpAddress())) {
			hbLocal.setAndCompareHeartBeatCounter(receivedHb
					.getHeartBeatCounter());
			// Check for failures if HeartBeat has not changed
			if (!hbLocal.hasCounterValueChanged()) {
				System.out
						.println("Old HeartBeat received. Checking for failures.");
				long currentTime = System.currentTimeMillis();
				checkForFailure(hbLocal, currentTime);
				if (hbLocal.getFailed()) {
					cleanUp(hbLocal, currentTime);
				}
			}
		}
	}

	/**
	 * Finds new nodes and adds it to the heart beat list
	 * 
	 * @param ReceivedList
	 */
	private void _findNewNodes(ArrayList<HeartBeat> ReceivedList) {
		System.out.println("Checking for new nodes");
		boolean nodeFound = false;
		for (HeartBeat receivedHb : ReceivedList) {
			for (HeartBeat hbLocal : heartBeatList) {
				if (receivedHb.getIpAddress().equals(hbLocal.getIpAddress())) {
					nodeFound = true;
				}
			}
			if (!nodeFound) {
				// add new node to local list
				System.out.println("New Node has joined: "
						+ receivedHb.getIpAddress());
				receivedHb.updateLocalTime();
				heartBeatList.add(receivedHb);

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
		System.out.println("Checking for Failure");
		if (currentTime - hb.getLocalTime() >= WAIT_TIME) {
			hb.setFailed();
			System.out.println(Thread.currentThread().getName() + ": "
					+ hb.getIpAddress() + " failed");
			logger.writeLogMessage("");
		}

	}

	public static void main(String args[]) {
		
		try {
			Log logger = new Log("../machine."
					+ InetAddress.getLocalHost().getHostAddress() + ".log");
			Client client = new Client(logger);
			client.start();
		} catch (UnknownHostException e) {
			System.out.println("There was an error setting the host");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}