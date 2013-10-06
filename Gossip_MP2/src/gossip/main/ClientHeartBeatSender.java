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
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientHeartBeatSender implements Runnable {

	private static final String IpAddressList = "/tmp/IpAddressList.txt";
	public static final int PORT = 5989;
	// Packet loss rate: 1%, 5%, 15%, and 50%.
	public AtomicInteger percentPacketloss = new AtomicInteger(0);
	// Change this to true for enabling packetLoss
	public AtomicBoolean packetLoss = new AtomicBoolean(false);
	public static final int PACKETSIZE = 1024;
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
	 * heart beat table -they are the contact node(s)
	 * 
	 * @param table
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void addAddressesFromText(HeartBeatTable table) {
		BufferedReader br = null;
		String ipString = "";
		// Taking from text file
		try {
			br = new BufferedReader(new FileReader(IpAddressList));
		} catch (FileNotFoundException e) {
			System.out.println("There was an error opening the file");
			e.printStackTrace();
			return;
		}

		try {
			while ((ipString = br.readLine()) != null) {
				HeartBeat hb = new HeartBeat(ipString,false);
				table.updateTable(hb);
			}
			br.close();// close the file reader here

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
			// Select members
			List<String> listOfReceivers = table.selectMembers(sendList);
			// send
			for (String ip : listOfReceivers) {
				// Splitting if larger than 20 modes
				if (sendList.size() > 20) {
					System.out.println("Packet too large. Splitting it!");
					ArrayList<HeartBeat> splitSendList = new ArrayList<HeartBeat>();
					// split the list if the list is large
					for (int i = 0; i < sendList.size() / 20; i++) {
						splitSendList.addAll(sendList.subList(i * 20,
								(i + 1) * 20));
						System.out.println("Sending Split Packet:" + i + 1);
						sendGossip(ip, splitSendList);
					}
				} else {
					sendGossip(ip, sendList);
				}
			}
		}
	}

	public void sendGossip(String ip, ArrayList<HeartBeat> sendList) {

		DatagramSocket clientSocket;
		try {
			clientSocket = new DatagramSocket();
			InetAddress ipAddress = InetAddress.getByName(ip);
			byte[] sendData = new byte[PACKETSIZE];

			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
			objectStream.writeObject(sendList);
			sendData = byteStream.toByteArray();

			boolean shouldDrop = shouldDrop();
			if(shouldDrop){
				return;
			}
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

	private boolean shouldDrop() {
		// Simulating packet loss rate: 1%, 5%, 15%, and 50%.
		if (packetLoss.get()) {
			Random random = new Random();
			int sendRate = random.nextInt(100);
			if (sendRate <= percentPacketloss.get()) {
				System.out.println("packet dropped!!!");
				return true;
			}
		}
		return false;
	}

	public void setPacketLoss(boolean setPacketLoss, int packetLossPercent){
		this.packetLoss.set(setPacketLoss);
		this.percentPacketloss.set(packetLossPercent);
	}
	
	
	public void stopClient() {
		System.out.println("Stopping Client");
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
