package gossip.main;

import gossip.heartbeat.HeartBeat;
import gossip.heartbeat.HeartBeatTable;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientHeartBeatSender implements Runnable {

	private static final String IpAddressList = "/tmp/IpAddressList.txt";
	public static final int PORT = 5989;
	public HeartBeatTable table;
	private AtomicBoolean exit = new AtomicBoolean(false);

	/**
	 * Constructor
	 * 
	 * @throws IOException
	 */
	public ClientHeartBeatSender(HeartBeatTable table) {
		this.table = table;
		addAddressesFromText(table);

	}

	/**
	 * This function reads a text file of ip addresses and adds them to the
	 * heart beat table
	 * 
	 * @param table
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void addAddressesFromText(HeartBeatTable table) {
		// Taking from text file
		BufferedReader br = null;
		String ipString = "";

		try {
			br = new BufferedReader(new FileReader(IpAddressList));
		} catch (FileNotFoundException e) {
			System.out.println("There was an error opening the file");
			e.printStackTrace();
			return;
		}

		try {

			while ((ipString = br.readLine()) != null) {
				HeartBeat hb = new HeartBeat(ipString);
				table.updateTable(hb);

			}
			br.close();// clase the file reader here

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void startGossip() throws UnknownHostException {
		while (true) {
			try {
				// Wait time should decrease with increasing list size
				// TODO possible mistake in wait time
				Thread.sleep(HeartBeatTable.WAIT_TIME);

				// exit the program when user asks to leave
				if (this.exit.get()) {
					return;
				}
			} catch (InterruptedException e) {
				System.out.println("There was an error sleeping!");
				e.printStackTrace();
			}

			// maintain
			ArrayList<HeartBeat> sendList = table.maintain();
			List<String> listOfReceivers = table.selectMembers(sendList);
			// send
			for (String ip : listOfReceivers) {
				sendGossip(ip, sendList);
			}
		}
	}

	public void sendGossip(String ip, ArrayList<HeartBeat> sendList) {

		DatagramSocket clientSocket;
		try {
			clientSocket = new DatagramSocket();
			InetAddress ipAddress = InetAddress.getByName(ip);
			byte[] sendData = new byte[1024];

			// Update self heart beat counter before sending the packet
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
			objectStream.writeObject(sendList);
			sendData = byteStream.toByteArray();

			// Send Gossip
			DatagramPacket sendPacket = new DatagramPacket(sendData,
					sendData.length, ipAddress, PORT);
			clientSocket.send(sendPacket);

		} catch (SocketException e) {
			e.printStackTrace();

		} catch (UnknownHostException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();

		}

	}

	public void stopClient() {
		this.exit.set(true);
	}

	public void run() {
		try {
			startGossip();
		} catch (UnknownHostException e) {
			e.printStackTrace();

		}
	}

}
