package gossip.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class StartClient {

	public static void main(String[] args) {
		try {
			Log logger = new Log("machine"
					+ InetAddress.getLocalHost().toString() + ".log");

			Client st = new Client(logger);
			st.start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
