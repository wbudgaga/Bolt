package mr.resourcemanagement.execution.mrtasks.management;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import mr.dht.peer2peernetwork.logging.FWLogger;
import mr.dht.peer2peernetwork.nodes.Peer;
import mr.dht.peer2peernetwork.nodes.RemotePeer;
import mr.dht.peer2peernetwork.nodes.Setting;
import mr.dht.peer2peernetwork.threadpool.Task;
import mr.dht.peer2peernetwork.threadpool.ThreadPoolManager;
import mr.dht.peer2peernetwork.util.UtilClass;
import mr.dht.peer2peernetwork.wireformates.FindRunningReducer;
import mr.resourcemanagement.execution.mrtasks.MapTaskInfo;
import mr.resourcemanagement.execution.mrtasks.ReduceTaskInfo;
import mr.resourcemanagement.execution.mrtasks.TaskInfo;

public class ResourceManager {
	private ConcurrentHashMap <Long,JobTasksManager> 			jobTasksManagers = new ConcurrentHashMap<Long,JobTasksManager>();
	private ConcurrentHashMap <Long,Long> 						jobMSGs = new ConcurrentHashMap<Long,Long>();//<msgID, jobID>
	private HashMap <String, HashMap<Long, FindRunningReducer>> waitingMaps = new HashMap<String,HashMap<Long, FindRunningReducer>>();//<job_reducerID, <remotePeerID, FindRunningReducer>>
	private ThreadPoolManager 									taskThreadPool; 
   
	// private ThreadPoolManager 									controlThreadPool;
    	private ThreadPoolManager 									ioThreadPool;
    	private Peer 												localPeer;
    
    private final  Scheduler									scheduler = new Scheduler();
    private final int 											MAX_TASKS = 40;// Runtime.getRuntime().availableProcessors() ;
    private volatile boolean 									batchStarted =false;
    private int 												numOfBatchJobs=0;
    private MetaDataManager 									metadataManager;
    
    public final Object LOCK = new Object();
    public final Object MSG_LOCK = new Object();
    
    //========================== benchmark variables ========================
    private long	startTime=-1;
    //=======================================================================
	public ResourceManager(Peer peer) throws Exception{	
		taskThreadPool 		= new ThreadPoolManager(MAX_TASKS);
		//controlThreadPool 	= new ThreadPoolManager(2*(MAX_TASKS));
		ioThreadPool 		= new ThreadPoolManager(32);
		setLocalPeer(peer);
		metadataManager=  new MetaDataManager(peer);
	}
	
	public void start() throws Exception{
		taskThreadPool.start();
		//controlThreadPool.start();
		ioThreadPool.start();
	}	
	public ThreadPoolManager getIOThreadPool(){
		return ioThreadPool;
	}
	public void stop() throws Exception{
		while (!taskThreadPool.isIdle())
			Thread.sleep(10);
		taskThreadPool.stop();
		ioThreadPool.stop();
	}	
	
	public void processTask(TaskInfo taskInfo){
		scheduler.offerTask(taskInfo);
	}
	
	public void execute() throws Exception{
		if (batchStarted)
			return;
		batchStarted = true;
		numOfBatchJobs = jobTasksManagers.size();
		int i=0;
		startTime = System.currentTimeMillis();
		while(i<MAX_TASKS){
			exeTask();
			++i;
		}
	}
	protected void exeTask() throws Exception{
		TaskInfo t = scheduler.poolTask();
		if (t==null){
			//System.out.println(" There is not any pending tasks on "+Setting.HOSTNAME);
			return;
		}
		JobTasksManager jTaskManager = jobTasksManagers.get(t.getJobID());
		if (t.getTaskType()==TaskInfo.MAP){
			jTaskManager.processMapTask((MapTaskInfo) t);
		}
		else{
			jTaskManager.processReduceTask((ReduceTaskInfo)t);
			synchronized (LOCK){
				if (waitingMaps.containsKey(t.getJobID()+"_"+t.getTaskID())){
					//System.out.println(Setting.HOSTNAME+"#####  Pool reducer (jobiD:"+t.getJobID()+"_"+t.getTaskID()+"), from Q and notify wainting maps==> ");
					notifyMaps(waitingMaps.remove(t.getJobID()+"_"+t.getTaskID()));
					
				}
			}
		}
	}
	
	private void notifyMaps(HashMap<Long, FindRunningReducer> mapsList) throws IOException{
		for (Map.Entry<Long, FindRunningReducer> mapEntry: mapsList.entrySet()){
			FindRunningReducer frr = mapEntry.getValue();
			RemotePeer mapPeer = localPeer.getQueryPeer(frr);
			mapPeer.queryResult(frr.getQueryKey(), frr.getMsgUUID(), frr.getSrcPeerHandlerID(), localPeer.getNodeData());
		}
		
	}
	public JobTasksManager getJobTaskManager(long jobID){
		return jobTasksManagers.get(jobID);
	}
	
	public boolean findRunningReducer(RemotePeer mapPeer, FindRunningReducer frr){
		JobTasksManager jTaskManager = jobTasksManagers.get(frr.getJobID());
		if (jTaskManager==null){
			System.out.println(" For looked reducer (jobiD:"+frr.getJobID()+","+frr.getReduceID()+"),  there is not any job");
			return false;
		}
		if (jTaskManager.hasAlreadyRTask(frr.getQueryKey())){
			return true;
		}
		synchronized (LOCK){
			localPeer.cachePeer(mapPeer);
			String reducerKey = frr.getJobID()+"_"+frr.getReduceID();
			HashMap<Long, FindRunningReducer> mapsList = waitingMaps.get(reducerKey);
			if (mapsList==null){
				mapsList = new HashMap<Long, FindRunningReducer>();
				waitingMaps.put(reducerKey, mapsList);
			}
			//System.out.println(" Request from " + mapPeer.getNodeData().getHost()+ " for non running reducer (jobiD:"+reducerKey+" ,   is cached on "+Setting.HOSTNAME);
			mapsList.put(mapPeer.getID(), frr);
		}
		return false;
	}
	public synchronized <K,V> JobTasksManager getOrCreateJobTaskManager(long jobID) throws Exception{
		JobTasksManager jTaskManager = jobTasksManagers.get(jobID);
		if (jTaskManager == null){
			jTaskManager = new<K,V> JobTasksManager(jobID, this);
			jobTasksManagers.put(jobID, jTaskManager);
		}
		return jTaskManager;
	}

	public synchronized void removeJobManager(long jobID) throws Exception{
		JobTasksManager jtm =jobTasksManagers.remove(jobID);
		if (jobTasksManagers.size()==0){
			long spentTime 	= (System.currentTimeMillis() - startTime);
			startTime		= -1;
			UtilClass.createPath(Setting.LOG_DIR);
			FWLogger.getInstance().createLogger(1, Setting.LOG_DIR, Setting.HOSTNAME+"_J"+numOfBatchJobs+"_R"+jtm.getJobInfo().getNumOfReducer()+"_T"+spentTime);
			FWLogger.getInstance().log(1, "ResourceManager: Host"+Setting.HOSTNAME+", job " +jobID+" has been completeted in "+spentTime +" msec" );
		}
	}
	
	public Peer getLocalPeer() {
		return localPeer;
	}

	public void setLocalPeer(Peer localPeer) {
		this.localPeer = localPeer;
	}
	public JobTasksManager pollMSGJob(long msgID){
		long jobID = removeJobMSG(msgID);
		return jobTasksManagers.get(jobID);
	}

	public void addJobMSG(long msgID, long jobID){
		synchronized(MSG_LOCK){
			jobMSGs.put(msgID, jobID);
		}
	}
	
	public long removeJobMSG(long msgID){
		synchronized(MSG_LOCK){
			return jobMSGs.remove(msgID);
		}
	}

	public void executeTask(Task task){
		taskThreadPool.addTask(task);
	}
 /*   public ThreadPoolManager getTaskThreadPool() {
		return taskThreadPool;
	}
*/
	public ThreadPoolManager getControlThreadPool() {
		return taskThreadPool;
	}
	public MetaDataManager getMetadataManager(){
		return metadataManager;
	}
}
