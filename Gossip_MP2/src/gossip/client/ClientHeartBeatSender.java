package gossip.client;

import java.io.*;
import java.net.*;
import java.util.List;

public class ClientHeartBeatSender implements Runnable {

	public static final String FIXEDNODE = "10.0.0.13";
	public static final int PORT = 5989;

	Log logger;

	/**
	 * Constructor
	 * 
	 * @throws UnknownHostException
	 */
	public ClientHeartBeatSender(Log logger) throws UnknownHostException {
		// joinGroup();
		this.logger = logger;
		HeartBeat he = new HeartBeat(InetAddress.getLocalHost().toString());
		Client.heartBeatList.add(he);
	}

	public void startGossip() {
		for (String i : selectMember()) {
			sendGossip(i);
		}
	}

	public List<String> selectMember() {
		// TODO: Add logic
		List<String> memberList = null;
		for (HeartBeat hb : Client.heartBeatList) {
			memberList.add(hb.getIpAddress());
		}
		return memberList;

	}

	// public void joinGroup() {
	// sendGossip(FIXEDNODE);
	// }

	public void sendGossip(String ip) {
		synchronized (Client.heartBeatList) {

			DatagramSocket clientSocket;
			try {
				clientSocket = new DatagramSocket();

				InetAddress ipAddress = InetAddress.getByName(ip);
				byte[] sendData = new byte[1024];
				ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
				ObjectOutputStream objectStream = new ObjectOutputStream(
						byteStream);

				objectStream.writeObject(Client.heartBeatList);
				sendData = byteStream.toByteArray();
				// Update self heart beat counter before sending the packet
				for (HeartBeat hb : Client.heartBeatList) {
					if (hb.getIpAddress().equals(
							InetAddress.getLocalHost().toString())) {
						this.updateSelf(hb);
					}
				}
				// Send Gossip
				DatagramPacket sendPacket = new DatagramPacket(sendData,
						sendData.length, ipAddress, PORT);
				clientSocket.send(sendPacket);

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

	@Override
	public void run() {
		startGossip();
	}

}
