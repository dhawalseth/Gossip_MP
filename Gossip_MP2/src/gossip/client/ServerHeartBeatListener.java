package gossip.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

public class ServerHeartBeatListener implements Runnable {

	public static final int PORT = 5989;
	Log logger;

	/**
	 * Constructor
	 */
	public ServerHeartBeatListener(Log logger) {
		this.logger = logger;
	}

	@SuppressWarnings("unchecked")
	public void receiveGossip() {
		DatagramSocket serverSocket;
		try {
			serverSocket = new DatagramSocket(PORT);

			byte[] receiveData = new byte[1024];

			while (true) {
				DatagramPacket receivePacket = new DatagramPacket(receiveData,
						receiveData.length);
				serverSocket.receive(receivePacket);
				receivePacket.getData();
				// Get local list
				ObjectInputStream objectStream = new ObjectInputStream(
						new ByteArrayInputStream(receivePacket.getData()));
				Object readObject = objectStream.readObject();
				if (readObject instanceof ArrayList<?>) {
					ArrayList<HeartBeat> hbList = (ArrayList<HeartBeat>) readObject;
					System.out.println("Received list from: "
							+ receivePacket.getAddress().getHostAddress()
							+ ". List size: " + hbList.size());
					// Update local list
					Client clientObject = new Client(logger);
					clientObject.updateCounter(hbList);

				}
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		receiveGossip();
	}

}
