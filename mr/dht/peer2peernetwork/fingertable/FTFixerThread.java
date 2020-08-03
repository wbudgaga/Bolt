package mr.dht.peer2peernetwork.fingertable;

import java.io.IOException;

import mr.dht.peer2peernetwork.nodes.Setting;

public class FTFixerThread extends Thread {
	private FTManager ftManager;
	public  FTFixerThread(FTManager ftManager) {
		this.ftManager				= ftManager;
	}
	
	public void run() {
		try{
		// Run forever, accepting and servicing connections
		 while (true) {
			sleep(Setting.FT_UPDATE_TIME);
			//System.out.println("Figer Table Fixer even is triggered...");
			ftManager.confirmDirectNeighbors();
			//ft.fixFingerEntries();
			if (Setting.PRINT_FT_ON_UPDATE)
				ftManager.print();			
		 }
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
