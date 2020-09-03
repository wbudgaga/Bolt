package mr.dht.peer2peernetwork.test;

import java.io.FileNotFoundException;
import java.util.HashMap;

import mr.dht.peer2peernetwork.nodes.Setting;
import mr.dht.peer2peernetwork.util.UtilClass;
import mr.resourcemanagement.execution.mrtasks.management.MapTaskOutputHandler;

public class routingKey {
	HashMap<Integer, Integer> maps 		= new HashMap<Integer, Integer>();
	HashMap<Integer, Integer> reducers 	= new HashMap<Integer, Integer>();
	
	int[] peersID				= {1000,2000,3000,4000,5000,6000,7000,8000,9000,10000,11000,12000,13000,14000,15000,16000,17000,18000,19000,20000,21000,22000,23000,24000,25000,26000,27000,28000,29000,30000,31000,32000,33000,34000,35000,36000,37000,38000,39000,40000,41000,42000,43000,44000,45000,46000,47000,47620,48240,48860,49480,50100,50720,51340,51960,52580,53200,53820,54440,55060,55680,56300,56920,57540,58160,58780,59400,60020,60640,61260,61880,62500,63120,63740,64360,64980,65535};
	
	public routingKey(){
		for (int i = 0 ; i < peersID.length; ++i){
			maps.put(peersID[i], 0);
			reducers.put(peersID[i], 0);
		}
	}
	
	private int getJobPeerIdx(int jobID){
		for (int i = 1 ; i < peersID.length; ++i){
			if (jobID>peersID[i - 1] && jobID<peersID[i])
				return i;
		}
		return 0;	
	}
	
	private void storeRd(long jobID, int numOfReducers){
		long sigmentSize	= 65536 / numOfReducers;
		long jobOffset 		= jobID % sigmentSize;
		for (int i = 0; i < numOfReducers; ++i){
			int id		= (int) (jobOffset + (i * sigmentSize));
			int pID 	= getJobPeerIdx(id);
			store(reducers, peersID[pID], 1);
		}
	}

	public void store(HashMap<Integer, Integer> m, int  peerID, int n){
		int old 		= m.get(peerID);
		m.put(peerID, old + n);
	}
	
	public void process(){
		
		int jobID 		= UtilClass.getRandomNumber(0, 65535);
		int jobPeerIdx 		=  getJobPeerIdx(jobID);
		store(maps, peersID[jobPeerIdx] , 10);
		jobPeerIdx =  (jobPeerIdx+1) % peersID.length;
		store(maps, peersID[jobPeerIdx] , 10);
		jobPeerIdx =  (jobPeerIdx+1) % peersID.length;
		store(maps, peersID[jobPeerIdx] , 10);
		
		storeRd(jobID, 4);
	}

	
	public void print(){
		for (int i =0 ; i < peersID.length ; ++i){
			int pID = peersID[i];
			System.out.println("pID:"+pID+ "  maps:"+maps.get(pID)+"    red:"+reducers.get(pID));
		}
	}
	public static void main(String[] s) throws Exception{
		routingKey tst = new routingKey();
		for (int i=0;i<100;++i)
			tst.process();
		tst.print();
	}

}
