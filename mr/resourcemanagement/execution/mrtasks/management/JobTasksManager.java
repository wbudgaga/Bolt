package mr.resourcemanagement.execution.mrtasks.management;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.openmbean.InvalidOpenTypeException;

import mr.dht.peer2peernetwork.nodes.Peer;
import mr.dht.peer2peernetwork.nodes.RemotePeer;
import mr.dht.peer2peernetwork.nodes.RemotePeerLocal;
import mr.dht.peer2peernetwork.nodes.Setting;
import mr.dht.peer2peernetwork.threadpool.Task;
import mr.dht.peer2peernetwork.threadpool.ThreadPoolManager;
import mr.dht.peer2peernetwork.util.UtilClass;
import mr.dht.peer2peernetwork.wireformates.FindRunningReducer;
import mr.dht.peer2peernetwork.wireformates.FinishedMapTaskNotify;
import mr.dht.peer2peernetwork.wireformates.Lookup;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.dht.peer2peernetwork.wireformates.PeerInfo;
import mr.dht.peer2peernetwork.wireformates.TextNumTaskData;
import mr.resourcemanagement.datatype.ReducerBuffer;
import mr.resourcemanagement.datatype.TaskData;
import mr.resourcemanagement.execution.mrtasks.JobInfo;
import mr.resourcemanagement.execution.mrtasks.MapTask;
import mr.resourcemanagement.execution.mrtasks.MapTaskInfo;
import mr.resourcemanagement.execution.mrtasks.ReduceTask;
import mr.resourcemanagement.execution.mrtasks.ReduceTaskInfo;

/*
 * Notes:
 *     By computing the routing keys, this class should be able to know the number of expected reducers out
 */
public class JobTasksManager{
	private long jobID;
	private JobInfo jobInfo;
	private ResourceManager resManager;			
	public volatile int numOFAllMaps		= -1;
	public int numLocMaps				= 0;
	public int numFinishedMaps			= 0;
	public int numLocReducers 			= 0;
	public int numFinishedReducers			= 0;
	private volatile  Integer numOFNotifiedReducers = 0;
	public  final 	RemotePeer LOCAL_PEER;
	////////////////////////////////////////
	private final Object 		LOCK 	 	= new Object();
	private final Object 		REDUCE_LOCK = new Object();
	private final Object 		REDUCE_LOCK1 = new Object();
	private final Object 		MAP_LOCK 	= new Object();
	private final Object 		MAP_LOCK1 	= new Object();
	private final Object 		JOB_LOCK 	= new Object();
	///////////////////////////////////////
	private long start;
	
	//include maps that are not ready to execute because of datarouter
	private ConcurrentHashMap <Long,MapTask>    penndingMaps 		= new ConcurrentHashMap<Long,MapTask>();
	//running tasks
	private ConcurrentHashMap <Long,MapTask>    runningMaps 		= new ConcurrentHashMap<Long,MapTask>();
	private ConcurrentHashMap <Long,ReduceTask> runningReducers 	= new ConcurrentHashMap<Long,ReduceTask>();
	 
    private ThreadPoolManager controlThreadPool; //used for parallel executions of control tasks to provide data to tasks and and handle tasks outputs
    
    // These are used by all mappers belonging to the same job to rout their outputs to the reducers
    private MapTaskOutputHandler	mapTaskOutputHandler;
	private DataRouter				dataRouter=null;    
	//These are needed to track the messages for the taskes 
	private ConcurrentHashMap <Long,Long> 	taskMSGs 		= new ConcurrentHashMap<Long,Long>();//<msgID, routingsKey(reducerID)>
	
	
	public JobTasksManager(long jobID, ResourceManager 	resManager) throws Exception{//OK
		LOCAL_PEER 				= new RemotePeerLocal(-1, "local", "local", -1, resManager.getLocalPeer().getMessageHandler());
		this.jobID  			= jobID;
		this.controlThreadPool 	= resManager.getControlThreadPool();
		this.resManager  		= resManager;
		start 					= System.currentTimeMillis();
		//FWLogger.getInstance().createLogger(jobID, Setting.LOG_DIR, Setting.HOSTNAME+"_"+jobID);
		//FWLogger.getInstance().log(jobID, "Log (PeerID: "+resManager.getLocalPeer().getID()+", jobID: "+jobID+", workDir: "+Setting.LOCAL_DIR+", creation Time: "+UtilClass.getCurrDateTimeAsString("yyyy-MM-dd HH:mm:ss") );
	}
	
	/////////////////======================MR-tasks and their control tasks=========================/////////////////
	//======================================= create MR-tasks =======================================
	public <K1,V1,K2,V2>  void processMapTask(MapTaskInfo<K1,V1,K2,V2> mapTaskInfo) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchAlgorithmException, IOException, InterruptedException{//OK
		MapTask<K1,V1,K2,V2> mrTask = (MapTask<K1, V1, K2, V2>) mapTaskInfo.createTask(jobInfo.getMTaskClassName());
		mrTask.setTasksManager(this);	
		synchronized (MAP_LOCK){
			if (mapTaskOutputHandler == null)
				createOutputHandler(mapTaskInfo);
			
			if (dataRouter.isReady())
				runMapTask(mrTask);
			else{
				penndingMaps.put(mapTaskInfo.getTaskID(), mrTask);
			}
		}
	}
	
	public <K1,V1,K2,V2>  void processReduceTask(ReduceTaskInfo<K1,V1,K2,V2> reduceTaskInfo) throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException{//OK
		ReduceTask<K1,V1,K2,V2> mrTask = (ReduceTask<K1, V1, K2, V2>) reduceTaskInfo.createTask(jobInfo.getRTaskClassName());
		synchronized(LOCK){
			if (numOFAllMaps == -1){
				numOFAllMaps = reduceTaskInfo.getNumOfMaps();
				//UtilClass.createPath(jobInfo.getOutputName());
			}
		}
		mrTask.setTasksManager(this);
		runReduceTask(mrTask);
	}
	//======================================= create Output handlers =======================================
	//Thread safe: because it is called only when mapTaskOutputHandler = null (first map task leads to this)
	public void createOutputHandler(MapTaskInfo mapTaskInfo) throws NoSuchAlgorithmException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException, InterruptedException{//OK
		dataRouter 	   		= new DataRouter(mapTaskInfo.getPartitioner(), jobInfo.getNumOfReducer(), jobID, this);
		dataRouter.computeRountingKeys();
		mapTaskOutputHandler= new MapTaskOutputHandler(dataRouter);
		addToControlThreadPool(mapTaskOutputHandler);
	}
	public <K1,V1,K2,V2> ReduceTaskOutputHandler<K1, V1, K2, V2> createOutputHandler(ReduceTaskInfo<K1,V1,K2,V2> reduceTaskInfo) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException{//OK
		ReduceTaskOutputHandler<K1, V1, K2, V2> reduceTaskOutputHandler = new ReduceTaskOutputHandler<K1, V1, K2, V2>(reduceTaskInfo.getDataWriter(jobInfo.getOutputName()));
		addToControlThreadPool(reduceTaskOutputHandler);
		return reduceTaskOutputHandler;
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//called by map()  function implemented by the user
	public <K2,V2> void output(K2 key, V2 data) throws InterruptedException{//OK
		synchronized (MAP_LOCK) {
			mapTaskOutputHandler.output(key, data);
		}
	}

/*	public void  feedReducer(long srcPeerID, ReduceTask rTask, HashMap<String, ArrayList<Long>> buf ) throws Exception{
		for (Map.Entry<String, ArrayList<Long>> e:buf.entrySet()){
			TaskData<String, ArrayList<Long>> td = new TaskData<String, ArrayList<Long>>(e.getKey(), e.getValue());
			rTask.enqueuReducerBuffer(td);
		}
		rTask.incNumOfReceivedBuffers(srcPeerID);
		//FWLogger.getInstance().log(jobID, rTask.getNumOfReceivedBuffers(srcPeerID)+" buffers have been received from :========>"+srcPeerID+"..........size: "+buf.size() +"   ");
	}
*/	
	public PeerInfo getLocalPeerInfo(){
		return RemotePeer.getPeerInfo(resManager.getLocalPeer().getNodeData());
	}
	//======================================= finishing MR-tasks =================================================================//
	/*
	 * called by only one thread by datarouter that is required by mapOutputHandler after exiting the main loop
	 * At the time this method is called  there aren't any running maps  
	 */
	public void publishFinishedMap() throws Exception{//OK
		PeerInfo taskOwner 			= RemotePeer.getPeerInfo(resManager.getLocalPeer().getNodeData());
		numOFNotifiedReducers = dataRouter.publishFinishedMap(taskOwner, numFinishedMaps);
		if (numFinishedReducers==numLocReducers)
			cleanUp();
	}

	public int decreaseMapRef(int numOfEndedMaps) throws Exception{//OK
		synchronized(LOCK){
			numOFAllMaps -= numOfEndedMaps;
			return numOFAllMaps;
		}
	}	
	public void stopReducers(){
		for (ReduceTask rTask:runningReducers.values()){
			rTask.stopRunning();
		}
	}
	
	public void stopReducers(Long srcID, int numOfExpectedBuffers) throws InterruptedException{
		synchronized (REDUCE_LOCK){
			for (ReduceTask rTask:runningReducers.values()){
				rTask.stopRunning(srcID, numOfExpectedBuffers);
			}
		}
	}

	//called by reduceTask.postTask 
	public void finishedReduceTask(Long reduceIDX) throws Exception{
		runningReducers.remove(reduceIDX);
		if (incFinishedReducers()){
			synchronized(REDUCE_LOCK){
					cleanUp();
			}
		}
		resManager.exeTask();
	}

	//called by mapTask.postTask 
	public void finishedMapTask(long mapIDX) throws Exception{
		runningMaps.remove(mapIDX);
		if (incFinishedMaps()){
			mapTaskOutputHandler.stopRunning();
		}
		System.out.println(Setting.LOCAL_DIR+"########## map finshed "+mapIDX+"   #"+ numFinishedMaps+ " of "+numLocMaps);
		resManager.exeTask();
	}	
	public void cleanUp() throws Exception{
		taskMSGs 	= null;
		runningMaps	= null;
		runningReducers = null;
		resManager.removeJobManager(jobID);
	}
	//running tasks
	
	

	//==================================== Task Massages ===============================================================//
/*	public void addTaskMSG(long msgID, long routingKey){
		synchronized (taskMSGs){
			taskMSGs.put(msgID, routingKey);
		}
	}
	public long removeTaskMSG(long msgID){
		synchronized (taskMSGs){
			return taskMSGs.remove(msgID);
		}
	}
*/	public void findReducerPeer(long routingsKey, int reducerID) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException{
		if (resManager.getLocalPeer().inPeerRange(routingsKey)){
			dataRouter.setRoutingPeer(routingsKey, LOCAL_PEER);
			return;
		}
		Peer localPeer = resManager.getLocalPeer();
		FindRunningReducer lookupMSG = RemotePeer.getFindRunningReducerMSG(routingsKey, jobID, reducerID, localPeer.getNodeData());
		resManager.addJobMSG(lookupMSG.getMsgUUID(), jobID);
		localPeer.lookup(lookupMSG);
	}	
	//========================================================================================================//	

	public <K1,V1,K2,V2> void runPendingMapTasks(){//OK
		synchronized (MAP_LOCK) {
			for (Map.Entry<Long, MapTask> pendingMap:penndingMaps.entrySet()){
				MapTask tmp = pendingMap.getValue();
				runMapTask(pendingMap.getValue());
			}
			penndingMaps.clear();
		}
	}

	private <K1,V1,K2,V2> void runMapTask(MapTask<K1,V1,K2,V2> mTask){//OK
		runningMaps.put(mTask.getTaskInfo().getTaskID(), mTask);	
		resManager.executeTask(mTask);
	}
	private <K1,V1,K2,V2> void runReduceTask(ReduceTask<K1,V1,K2,V2> rTask){
		addReduceTask(rTask.getTaskInfo().getTaskID(), rTask);	
		resManager.executeTask(rTask);
	}
	// examples of control tasks reading data from local drive and feed the data to map task 
	public boolean addToControlThreadPool(Task dataManagerTask){
		controlThreadPool.addTask(dataManagerTask);
		return true;
	}
	public ThreadPoolManager  getIOThreadPool(){
		return resManager.getIOThreadPool();
	}

	public ReduceTask getReduceTask(long k) {
		synchronized (REDUCE_LOCK) {
			return runningReducers.get(k);
		}
	}	
	public synchronized void  incLocMaps(){
		synchronized (MAP_LOCK1){
			++numLocMaps;
		}
	}
	public synchronized boolean incFinishedMaps(){
		synchronized (MAP_LOCK1){
			 ++numFinishedMaps;
			 return numFinishedMaps == numLocMaps;
		}
	}
	
	public synchronized void  incLocReducers(){
		synchronized (REDUCE_LOCK1){
			++numLocReducers;
		}
	}

	public synchronized boolean  incFinishedReducers(){
		synchronized (REDUCE_LOCK1){
			++numLocReducers;
			return numLocReducers == numLocReducers;
		}
	}

	public void addReduceTask(long l, ReduceTask rTask){
		synchronized (REDUCE_LOCK) {
			runningReducers.put(l, rTask);
		}
	}	

	public boolean hasAlreadyMTask(long taskID){
		synchronized (MAP_LOCK) {
			return runningMaps.containsKey(taskID);
		}
	}
	public boolean hasAlreadyRTask(long reduceIDX){
		synchronized (REDUCE_LOCK) {
			return runningReducers.containsKey(reduceIDX);
		}
	}	


	public DataRouter getDataRouter() {
		return dataRouter;
	}

	public JobInfo getJobInfo() {
		synchronized (JOB_LOCK){
			if (jobInfo==null)
				jobInfo = new JobInfo(jobID);
			return jobInfo;
		}
	}

	public void setJobInfo(JobInfo jobInfo) {
		synchronized (JOB_LOCK){
			this.jobInfo = jobInfo;
		}
	}
}
