package mr.resourcemanagement.execution.mrtasks.management;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import mr.dht.peer2peernetwork.logging.FWLogger;
import mr.dht.peer2peernetwork.nodes.RemotePeer;
import mr.dht.peer2peernetwork.nodes.Setting;
import mr.dht.peer2peernetwork.util.HashFunction;
import mr.dht.peer2peernetwork.util.UtilClass;
import mr.dht.peer2peernetwork.wireformates.FinishedMapTaskNotify;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.dht.peer2peernetwork.wireformates.PeerInfo;
import mr.dht.peer2peernetwork.wireformates.TextNumTaskData;
import mr.resourcemanagement.datapartitioning.Partitioner;
import mr.resourcemanagement.datatype.ReducerBuffer;
import mr.resourcemanagement.datatype.TaskData;
import mr.resourcemanagement.execution.mrtasks.MapTaskInfo;
import mr.resourcemanagement.execution.mrtasks.TaskInfo;

public class DataRouter{
	private 	Partitioner<Long, Long>	partitioner;
//	protected 	HashFunction hashFunction;
	private 	volatile   boolean  	ready		= false;
	protected 	Long[] 	routingsKey;
	protected 	HashMap<Long,RemotePeer>ThePeers 	= new HashMap<Long,RemotePeer>(); 
/*	private 	Long sigmentSize;
	private 	Long jobOffset;
*/	private 	int numOfReducer;
	private 	JobTasksManager jobManger;
	public final int numOfReducerMSGs  			= 0;
	public TextNumTaskData dataMSG;
	//public 	ConcurrentHashMap<Long,  BlockingQueue<TextNumTaskData>>	reducerQMSGs= new ConcurrentHashMap<Long, BlockingQueue<TextNumTaskData>>	();
	//private final BlockingQueue<TaskData<Long, String>> internalDataQueue = new ArrayBlockingQueue<TaskData<Long,String>>(QueueSize);
	private 	HashMap<Long, Integer>	numOfSentBuffers= new HashMap<Long, Integer>();
	public volatile long tmp				= 0;
	/////////////////////////////////////
	private 	final 	Object 	PEERS_LOCK		= new Object();
	private 	final 	Object 	LOCK 			= new Object();
	/////////////////////////////////////
	public DataRouter(Partitioner partitioner,int numOfReducer, long jobID, JobTasksManager jobManger) throws NoSuchAlgorithmException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {//OK
		this.partitioner				= partitioner;
		this.numOfReducer 				= numOfReducer;
		routingsKey 					= new Long[numOfReducer];
		this.jobManger					= jobManger;
		createDataMSG();
		//log("Data router to the reducers is created! ");
	}
	
	public void computeRountingKeys() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException{//OK
		for(int i=0; i<numOfReducer; ++i){
			routingsKey[i] 				= UtilClass.GetReduceKey(jobManger.getJobInfo().getOutputName(), (i+1));
			numOfSentBuffers.put(routingsKey[i], 0);
			jobManger.findReducerPeer(routingsKey[i], i);
		}
	}
	
	public void  setRoutingPeer(Long routingsKey, RemotePeer rPeer){//OK
		synchronized(PEERS_LOCK){
			ThePeers.put(routingsKey, rPeer);
			becomeReady();
			//log("A reducer peer (ID:"+rPeer.getID()+ ") has been found for the rounting key "+ routingsKey +" ("+ThePeers.size()+" of "+numOfReducer+") routerStatus: "+isReady());
		}
	}
	
	public RemotePeer  getRoutingPeer(Long routingKey){//OK
		synchronized(PEERS_LOCK){
			return ThePeers.get(routingKey);
		}
	}
	
	private void becomeReady(){//OK
		jobManger.runPendingMapTasks();
		ready 						= true;
	}
	
	public boolean isReady(){//OK
		return ready;
	}
	
	public Long getRoutingKey(int reducerID){//OK
		return routingsKey[reducerID];
	}
	
	public void stopAfterFinish(){//OK
		try {
			jobManger.publishFinishedMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void pushMessage(long rKey, Message msg) throws IOException{
		RemotePeer rp 					= getRoutingPeer(rKey);
		if (rp == null)
			throw new IOException(">>>There is not any responsible peer for the routing key: "+routingsKey);
	
		rp.sendMessage(msg);
	}
	
	//Thread safe: mapTaskOutputHandler.mainloop->this.pushBuffer
	public void  pushBuffer(int reducerID, ReducerBuffer rBuffer) throws IOException, InterruptedException{//OK
		Long routingsKey 				= getRoutingKey(reducerID);
		dataMSG.setTaskID(routingsKey);
		dataMSG.setDataBuf(rBuffer.getOutputBuf());
		pushMessage(routingsKey, dataMSG);		
	//	returnRBufferMSG(routingsKey, msg);
	}
	
	public int publishFinishedMap(PeerInfo taskOwner, int numOfFinishedMaps) throws IOException{//OK
		FinishedMapTaskNotify msg 			= new FinishedMapTaskNotify();
		msg.setTaskOwner(taskOwner);
		msg.setJobID(jobManger.getJobInfo().getJobID());
		msg.setNumOfFinishedMaps(numOfFinishedMaps);
		int i						= 0;
		for (; i<getNumOfReducer(); ++i){
			long routingsKey 	= getRoutingKey(i);
			msg.setNumOfSentBuffers(getNumOfSentBuffers(routingsKey));
			pushMessage(routingsKey, msg);
		}
		return i;
	}

	public void createDataMSG(){
		dataMSG = new TextNumTaskData();
		PeerInfo taskOwner 	= jobManger.getLocalPeerInfo();
		dataMSG.setTaskOwner(taskOwner);
		dataMSG.setJobID(jobManger.getJobInfo().getJobID());
	}

	public BlockingQueue<TextNumTaskData> createTaskDataMSG(int reducerID){
		 BlockingQueue<TextNumTaskData> messagesQueue = new ArrayBlockingQueue<TextNumTaskData>(numOfReducerMSGs);
		 for (int i=0; i<numOfReducerMSGs; ++i){
			TextNumTaskData msg = new TextNumTaskData();
			PeerInfo taskOwner 	= jobManger.getLocalPeerInfo();
			msg.setTaskOwner(taskOwner);
			msg.setJobID(jobManger.getJobInfo().getJobID());
			msg.setTaskID(reducerID);
			messagesQueue.offer(msg);
		 }
		 return messagesQueue;
	}
	////
	public Integer getNumOfSentBuffers(Long routingsKey) {//OK
		synchronized (LOCK) {
			return numOfSentBuffers.get(routingsKey);
		}
	}
	public void incNumOfSentBuffers(Long routingsKey) {//OK
		synchronized (LOCK) {
			numOfSentBuffers.put(routingsKey, numOfSentBuffers.get(routingsKey) + 1 );
		}
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//-------------------------------------------------------------------------------------------------------------
	//find the reducer index for the result k, v. used by the map to forward the results to the appropriate reducers
	public <K, V> int  getReducerID(K key, V value){
		Long hashedKey = UtilClass.hashMKey((String)key);
		return partitioner.getReducerID(hashedKey, (Long) value, numOfReducer);
	}

	public int getNumOfReducer() {
		return numOfReducer;
	}
	
	public int getReducerIDX(long rKey) {//OK
		for (int i=0; i < numOfReducer; ++i){
			if (routingsKey[i] == rKey)
				return i;
		}
		return -1;
	}
	//////////////////////////////static//////////////PLEASE CHECK
	public static long getRountingKey(Partitioner partitioner,int numOfReducers, long jobID, int idx){
		Long sigmentSize	= Setting.RING_KEYSPACE / numOfReducers;
		Long jobOffset 		= jobID % sigmentSize;
		return jobOffset + idx * sigmentSize;
	}
	public static long[] getRountingKeys(Partitioner partitioner,int numOfReducers, long jobID){
		Long sigmentSize= Long.valueOf(Setting.RING_KEYSPACE / numOfReducers);
		Long jobOffset 	= jobID % sigmentSize;
		
		long[] RoutingKeys = new long[numOfReducers];
		for(int i=0; i<numOfReducers; ++i){
			RoutingKeys[i] = jobOffset + i * sigmentSize;
		}
		return RoutingKeys;
	}
	
/*	public void log(String txt){
		FWLogger.getInstance().log(jobManger.getJobInfo().getJobID(),txt);
	}
*/
}
