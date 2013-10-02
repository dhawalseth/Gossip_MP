package gossip.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * This class detects failures
 * 
 * @author etubil2
 * 
 */
public class Client {

	public HeartBeatTable table;

	public void start() throws IOException {
		HeartBeat own = new HeartBeat(InetAddress.getLocalHost()
				.getHostAddress());
		this.table = new HeartBeatTable(own);
		
		Thread listenerThread = new Thread(new ServerHeartBeatListener(table));
		listenerThread.start();
		Thread senderThread = new Thread(new ClientHeartBeatSender(table));
		senderThread.start();
	}

	public static void main(String args[]) {

		try {
			Client client = new Client();
			client.start();
		} catch (UnknownHostException e) {
			System.out.println("There was an error setting the host");
			e.printStackTrace();
			
		} catch (IOException e) {
			e.printStackTrace();
			
		}
	}
}