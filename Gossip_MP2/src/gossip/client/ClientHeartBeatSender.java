package gossip.client;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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

public class ClientHeartBeatSender implements Runnable {

	private static final String IpAddressList = "/tmp/IpAddressList.txt";
	public static final int PORT = 5989;

	Log logger;

	/**
	 * Constructor
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	public ClientHeartBeatSender(Log logger) throws IOException {
		// joinGroup();
		this.logger = logger;
		// Adding self in local list
		HeartBeat selfHb = new HeartBeat(InetAddress.getLocalHost()
				.getHostAddress());
		Client.heartBeatList.add(selfHb);
		// Taking from txt file
		BufferedReader br = new BufferedReader(new FileReader(IpAddressList));
		String ipString = "";
		while ((ipString = br.readLine()) != null) {
			HeartBeat he = new HeartBeat(ipString);
			Client.heartBeatList.add(he);
		}
	}

	public void startGossip() throws UnknownHostException {
		while (true) {
			try {
				// Wait time should decrease with increasing list size
				if (Client.heartBeatList.size() != 0)
					Thread.sleep(Client.WAIT_TIME / Client.heartBeatList.size());
			} catch (InterruptedException e) {
				System.out.println("There was an error sleeping!");
				e.printStackTrace();
			}
			for (String i : selectMember()) {
				sendGossip(i);
			}
		}
	}

	public List<String> selectMember() throws UnknownHostException {
		// TODO: Add logic. Rite now selecting all
		List<String> memberList = new ArrayList<String>();
		for (HeartBeat hbLocal : Client.heartBeatList) {
			if (!hbLocal.getIpAddress().equals(
					InetAddress.getLocalHost().getHostAddress())) {
				memberList.add(hbLocal.getIpAddress());
			}
		}
		return memberList;

	}

	public void sendGossip(String ip) {
		synchronized (Client.heartBeatList) {

			DatagramSocket clientSocket;
			try {
				System.out
						.println("Sending Gossip to: " + ip
								+ ". Current List size: "
								+ Client.heartBeatList.size());
				clientSocket = new DatagramSocket();
				InetAddress ipAddress = InetAddress.getByName(ip);
				byte[] sendData = new byte[1024];
				// Update self heart beat counter before sending the packet
				for (HeartBeat hb : Client.heartBeatList) {
					if (hb.getIpAddress().equals(
							InetAddress.getLocalHost().getHostAddress())) {
						this.updateSelf(hb);
					}
					ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
					ObjectOutputStream objectStream = new ObjectOutputStream(
							byteStream);

					objectStream.writeObject(Client.heartBeatList);
					sendData = byteStream.toByteArray();
					// Send Gossip
					DatagramPacket sendPacket = new DatagramPacket(sendData,
							sendData.length, ipAddress, PORT);
					clientSocket.send(sendPacket);
				}
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}
		}
	}

	/**
	 * update own counter
	 */
	public void updateSelf(HeartBeat hb) {
		hb.setAndCompareHeartBeatCounter(hb.getHeartBeatCounter() + 1);
	}

	public void run() {
		try {
			startGossip();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
