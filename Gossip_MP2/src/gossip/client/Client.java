package gossip.client;

import java.util.ArrayList;

public class Client {
	public static ArrayList<HeartBeat> heartBeatList = new ArrayList<HeartBeat>();

	public static void main(String[] args) {
		Thread t2 = new Thread(new ServerHeartBeatListener());
		t2.start();
		Thread t = new Thread(new ClientHeartBeatSender());
		t.start();
	}
	
	public static void updateCounter(ArrayList<HeartBeat> hbList) {
		// TODO 

	}
}