package gossip.client;

import java.util.ArrayList;

public class Client {
	public static ArrayList<HeartBeat> heartBeatList = new ArrayList<HeartBeat>();

	public static void main(String[] args) {
		int id = 1; //id would go here
		Log logger = new Log("machine"+id+".log");
		Thread t2 = new Thread(new ServerHeartBeatListener());
		t2.start();
		Thread t = new Thread(new ClientHeartBeatSender());
		t.start();
		Thread t3 = new Thread(new FailureDetector(Client.heartBeatList,logger, id));
		t3.start();
	}
	
	public static void updateCounter(ArrayList<HeartBeat> hbList) {
		// TODO 

	}
}