package gossip.main;

import gossip.heartbeat.HeartBeat;
import gossip.heartbeat.HeartBeatTable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;

public class MainUserInterface {
	public HeartBeatTable table;
	private ClientHeartBeatSender hbSender;
	private ServerHeartBeatListener hbListener;
	Thread server;
	Thread client;
	public final static String LEAVE = "leave";
	public final static String JOIN = "join";

	public enum State {
		Connected, Disconnected
	};

	private State currentState;

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
		}
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
		this.table = new HeartBeatTable(own);

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
