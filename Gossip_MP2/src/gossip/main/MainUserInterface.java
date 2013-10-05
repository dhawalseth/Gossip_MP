package gossip.main;

import gossip.heartbeat.HeartBeat;
import gossip.heartbeat.HeartBeatTable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainUserInterface {
	public HeartBeatTable table;
	private ClientHeartBeatSender hbSender;
	private ServerHeartBeatListener hbListener;
	Log logger;
	Thread server;
	Thread client;
	public final static String LEAVE = "leave";
	public final static String JOIN = "join";

	public enum State {
		Connected, Disconnected
	};

	private State currentState;

	public MainUserInterface() {
		try {
			this.logger = new Log("/tmp/machine."
					+ InetAddress.getLocalHost().getHostAddress() + ".log");
		} catch (UnknownHostException e) {
			e.printStackTrace();

		}
	}

	/**
	 * Sets the connected/disconnected state
	 * 
	 * @param state
	 */
	public void setState(State state) {
		this.currentState = state;
	}

	/**
	 * Returns true if computer is connected
	 * 
	 * @return
	 */
	public boolean isConnected() {
		boolean retVal = this.currentState == State.Connected;
		return retVal;
	}

	/**
	 * Asks and gets user input -leave or join
	 * 
	 * @param port
	 * @param dbPassword
	 */
	public static String getMessageInput() {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String input = "";

		System.out.println("Note: You can type 'leave' or 'join': ");
		try {
			input = br.readLine();

		} catch (IOException e) {
			System.out.println("error reading");
			e.printStackTrace();

		}
		return input;
	}

	/**
	 * Gets input from the user and asks if the user wants to leave or join
	 * group
	 */
	public void interactWithUser() {
		String userInput = getMessageInput();
		userInput = userInput.trim().toLowerCase();

		if (userInput.equals(LEAVE) && this.isConnected()) {
			disconnectFromGroup();

		} else if (userInput.equals(JOIN) && !this.isConnected()) {
			connectToGroup();
		} else {
			String[] setArguments = userInput.split(" ");

			setPacketLoss(setArguments);
			setSessionName(setArguments);
			setNumConnections(setArguments);
		}
	}

	private void setNumConnections(String[] setArguments) {
		if(setArguments.length!=2)
			return;
		
		int setNumConnections = 2;
		if(setArguments[0].equals("session")){
			setNumConnections = Integer.parseInt(setArguments[1]);
		}
		this.table.setNumConnections(setNumConnections);
	}

	/**
	 * Sets the session name for easier logging
	 * 
	 * @param setArguments
	 */
	private  void setSessionName(String[] setArguments) {
		if(setArguments.length!=2)
			return;
		
		String sessionName = null;
		if(setArguments[0].equals("session")){
			sessionName = setArguments[1];
		}
		if(sessionName!=null){
			this.logger.setSessionName(sessionName);
		}

	}

	/**
	 * Set packet loss in percent (integer)
	 * 
	 * @param setArguments
	 */
	private void setPacketLoss(String[] setArguments) {
		if (setArguments.length < 2)
			return;
		
		boolean shouldDropPackets = false;
		int packetLossPercent = 0;
		
		if(setArguments[0].equals("drop")){
			if(setArguments[1].equals("true") && setArguments.length==3){
				shouldDropPackets = true;
				packetLossPercent = Integer.parseInt(setArguments[2]);
				
			} else {
				shouldDropPackets  = false;
			}
		}
		this.hbSender.setPacketLoss(shouldDropPackets, packetLossPercent);
	}

	/**
	 * Connects this computer to the group
	 */
	private void connectToGroup() {
		this.setState(State.Connected);
		this.table.reincarnate();
		this.hbSender = new ClientHeartBeatSender(table);
		this.client = new Thread(this.hbSender);
		this.client.start();
	}

	/**
	 * Disconnects this computer from the group
	 */
	private void disconnectFromGroup() {
		this.setState(State.Disconnected);
		this.hbSender.stopClient();
		try {
			this.client.join();

		} catch (InterruptedException e) {
			System.out
					.println("There was an error joining server/client threads");
			e.printStackTrace();
		}
	}

	/**
	 * Starts the client and server threads
	 * 
	 * @throws IOException
	 */
	public void startClientAndServer() throws IOException {
		HeartBeat own = new HeartBeat(InetAddress.getLocalHost()
				.getHostAddress());
		this.table = new HeartBeatTable(own, this.logger);

		this.hbListener = new ServerHeartBeatListener(table);
		this.hbSender = new ClientHeartBeatSender(table);
		this.server = new Thread(this.hbListener);
		this.server.start();
		this.client = new Thread(this.hbSender);
		this.client.start();
		this.currentState = State.Connected;
	}

	/**
	 * Main -starts the program
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		MainUserInterface mainUI = new MainUserInterface();
		try {
			mainUI.startClientAndServer();

		} catch (IOException e) {
			e.printStackTrace();

		}

		while (true) {
			mainUI.interactWithUser();
		}

	}

}
