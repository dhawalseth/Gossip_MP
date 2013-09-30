package gossip.client;

import java.util.ArrayList;

/**
 * This class detects failures
 * 
 * @author etubil2
 * 
 */
public class FailureDetector implements Runnable {

	private static long WAIT_TIME = 1000; // how long until a computer will be  marked as failed
	private static long CLEAN_UP_TIME; // how long to wait until we are able to remove computer from table
	ArrayList<HeartBeat> heartBeatTable;
	Log logger;
	private int id;
	/**
	 * Constructor
	 */
	public FailureDetector(ArrayList<HeartBeat> heartBeatTable, Log logger, int id) {
		CLEAN_UP_TIME = WAIT_TIME * 2;
		this.heartBeatTable = heartBeatTable;
		this.logger = logger;
		this.id = id;
	}

	/**
	 * Runs
	 */
	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(WAIT_TIME);
				
				long currentTime = System.currentTimeMillis();

				for (HeartBeat hb : heartBeatTable) {
					if(hb.getID()==this.id){
						this.updateSelf(hb);
					} else {
						checkForFailure(hb, currentTime);
						if (hb.getFailed()) {
							cleanUp(hb, currentTime);
						}
					}
				}

			} catch (InterruptedException e) {
				this.logger
						.writeLogMessage("There was an error for the failure detector thread while sleeping");
				e.printStackTrace();
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
			this.heartBeatTable.remove(hb);
		}

	}

	/**
	 * update own counter
	 */
	public void updateSelf(HeartBeat hb){
		hb.setAndCompareHeartBetCounter(hb.getHeartBeatCounter()+1);
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
			logger.writeLogMessage("");
		}

	}

}
