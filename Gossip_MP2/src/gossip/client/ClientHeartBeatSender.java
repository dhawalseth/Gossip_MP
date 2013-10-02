package gossip.client;

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

public class ClientHeartBeatSender implements Runnable {

	private static final String IpAddressList = "/tmp/IpAddressList.txt";
	public static final int PORT = 5989;
	private HeartBeatTable table;
	private final static int NUM_CONNECTIONS = 2;

	/**
	 * Constructor
	 * 
	 * @throws IOException
	 */
	public ClientHeartBeatSender(HeartBeatTable table)
			throws IOException {
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
	private void addAddressesFromText(HeartBeatTable table)
			throws FileNotFoundException, IOException {
		// Taking from text file
		BufferedReader br = new BufferedReader(new FileReader(IpAddressList));
		String ipString = "";

		while ((ipString = br.readLine()) != null) {
			HeartBeat hb = new HeartBeat(ipString);
			table.updateTable(hb);
		}
		br.close();
	}

	public void startGossip() throws UnknownHostException {
		while (true) {
			try {
				// Wait time should decrease with increasing list size
				// TODO possible mistake in wait time
				Thread.sleep(HeartBeatTable.WAIT_TIME);

			} catch (InterruptedException e) {
				System.out.println("There was an error sleeping!");
				e.printStackTrace();
			}

			// maintain
			ArrayList<HeartBeat> sendList = this.table.maintain();
			List<String> listOfReceivers = selectMember(sendList);
			// send
			for (String ip : listOfReceivers) {
				sendGossip(ip, sendList);
			}
		}
	}

	/**
	 * Get a list of random people to send to
	 * 
	 * @param sendList
	 * @return
	 * @throws UnknownHostException
	 */
	public List<String> selectMember(ArrayList<HeartBeat> sendList)
			throws UnknownHostException {
		List<String> randomMembers = new ArrayList<String>();
		Random randomGenerator = new Random();

		while (randomMembers.size() < NUM_CONNECTIONS
				&& randomMembers.size() < sendList.size()-1) {
			int randIndex = randomGenerator.nextInt(sendList.size());
			HeartBeat toBeAddedHB = sendList.get(randIndex);
			String nextAddress = toBeAddedHB.getIpAddress();

			if (nextAddress != table.own.getIpAddress()) {// not own address
				if (!randomMembers.contains(nextAddress)) {// not already
															// selected
					randomMembers.add(nextAddress);
				}
			}
		}
		return randomMembers;
	}

	public void sendGossip(String ip, ArrayList<HeartBeat> sendList) {

		DatagramSocket clientSocket;
		try {
			System.out.println("Sending Gossip to: " + ip
					+ ". Current List size: " + sendList.size());

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

	public void run() {
		try {
			startGossip();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			
		}
	}

}
