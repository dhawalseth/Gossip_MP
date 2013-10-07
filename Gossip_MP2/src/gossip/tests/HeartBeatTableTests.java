package gossip.tests;

import static org.junit.Assert.assertTrue;
import gossip.heartbeat.HeartBeat;
import gossip.heartbeat.HeartBeatTable;

import java.util.ArrayList;

import org.junit.Test;

public class HeartBeatTableTests {
	
	@Test
	public void testIncreaseOwnHeartBeat() {
		HeartBeat own = new HeartBeat("0",true);
		HeartBeatTable table = new HeartBeatTable(own,null);
		table.maintain();
		assertTrue(own.getHeartBeatCounter()==1);
	}

	@Test
	public void testServerMergeAdd() {
		HeartBeat own = new HeartBeat("0",true);
		HeartBeatTable table = new HeartBeatTable(own,null);

		ArrayList<HeartBeat> currentTable = setupTable(own,table);
		
		int otherHBCount = 0;
		for(HeartBeat hb : currentTable){
			if(hb.getIpAddress()==own.getIpAddress()){
				assertTrue(hb.getHeartBeatCounter()==1);
			} else {
				assertTrue(hb.getHeartBeatCounter()==0);
				otherHBCount++;
			}
		}
		assertTrue(otherHBCount==2);
	}

	private ArrayList<HeartBeat> setupTable(HeartBeat own, HeartBeatTable table) {
		HeartBeat hb1 = new HeartBeat("1",true);
		HeartBeat hb2 = new HeartBeat("2",true);
		
		
		ArrayList<HeartBeat> receivedList = new ArrayList<HeartBeat>();
		receivedList.add(own);
		receivedList.add(hb1);
		receivedList.add(hb2);
		

		table.updateTable(receivedList);
		ArrayList<HeartBeat> currentTable = table.maintain();
		return currentTable;
	}
	
	public void testServerMerge(){
		HeartBeat own = new HeartBeat("0",true);
		HeartBeatTable table = new HeartBeatTable(own,null);
		ArrayList<HeartBeat> currentTable = setupTable(own,table);
	
		table.updateTable(currentTable);
		
		HeartBeat hb1replacement = new HeartBeat("1",true);
		HeartBeat hb2replacement = new HeartBeat("2",true);
		hb1replacement.setAndCompareHeartBeatCounter(1);
		hb2replacement.setAndCompareHeartBeatCounter(2);
		
		ArrayList<HeartBeat> receivedList = new ArrayList<HeartBeat>();
		receivedList.add(hb1replacement);
		receivedList.add(hb2replacement);
		
		table.updateTable(receivedList);
		ArrayList<HeartBeat> testList = table.maintain();
		
		for(HeartBeat hb : testList){
			String hbAddress = hb.getIpAddress();
			if(hbAddress.equals("0")){
				assertTrue(hb.getHeartBeatCounter()==2);//Maintenance is called twice
			} else if(hbAddress.equals("1")){
				assertTrue(hb.getHeartBeatCounter()==1);
			} else if(hbAddress.equals("2")){
				assertTrue(hb.getHeartBeatCounter()==2);
			}
		}
	}
	
	
	@Test
	public void testClientUpdateNoFailures() throws InterruptedException{
		HeartBeat own = new HeartBeat("0",true);
		HeartBeatTable table = new HeartBeatTable(own,null);
		ArrayList<HeartBeat> currentTable = setupTable(own,table);
		table.updateTable(currentTable);
		Thread.sleep(HeartBeatTable.FAIL_TIME/2);
		ArrayList<HeartBeat> testList = table.maintain();
		
		for(HeartBeat hb : testList){
			String hbAddress = hb.getIpAddress();
			if(hbAddress.equals("0")){
				assertTrue(hb.getHeartBeatCounter()==2);//Maintenance is called twice
			} else {
				assertTrue(hb.getHeartBeatCounter()==0);
			}
		}
		
	}

	@Test
	public void testClientUpdateWithFailures() throws InterruptedException{
		HeartBeat own = new HeartBeat("0",true);
		HeartBeatTable table = new HeartBeatTable(own,null);
		ArrayList<HeartBeat> currentTable = setupTable(own,table);
		table.updateTable(currentTable);
		Thread.sleep(HeartBeatTable.FAIL_TIME);
		
		
		//Failures
		ArrayList<HeartBeat> testList = table.maintain();
		int size = testList.size();
		assertTrue(size==3);
		
		
		//Cleanup
		Thread.sleep(HeartBeatTable.CLEAN_UP);
		testList = table.maintain();
		size = testList.size();
		assertTrue(size==1);
	}
	
	
}
