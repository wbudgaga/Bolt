package mr.resourcemanagement.execution.mrtasks;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import mr.dht.peer2peernetwork.nodes.Setting;
import mr.resourcemanagement.datatype.TaskData;
import mr.resourcemanagement.execution.mrtasks.management.ReduceTaskDataProvider;
import mr.resourcemanagement.execution.mrtasks.management.ReduceTaskOutputHandler;
import mr.resourcemanagement.io.DataSource;
import mr.resourcemanagement.io.DataWriter;

public abstract class ReduceTask<K2,V2,K3,V3> extends MRTask<K2,V2,K3,V3>{
	protected 	ReduceTaskInfo<K2,V2,K3,V3>	 reduceTaskInfo;
	private   	ConcurrentHashMap<Long,Integer> numOfReceivedBuffers 	= new ConcurrentHashMap<Long,Integer>();//<sourceID,numOfBuffers>
	private   	ConcurrentHashMap<Long,Integer> numOfExpectedBuffers 	= new ConcurrentHashMap<Long,Integer>();//<sourceID,numOfBuffers>
	private 	Object 		LOCK 					= new Object();
    	public 		final Long 	POISON 					= new Long(-1);

	protected BlockingQueue<K2> 				pendingQueue1			= new LinkedBlockingQueue<K2>();
	protected ConcurrentHashMap<K2,V2> 			dataBuffer 				= new ConcurrentHashMap<K2,V2> ();
	protected HashMap<K3,V3> 					OutputDataBuffer 		= new HashMap<K3,V3> ();
	
	
	private ReduceTaskOutputHandler<K2,V2,K3,V3>reducerTaskOutputHandler;
    
	public void offer(K2 key, V2 valueList){
		synchronized(LOCK){
			V2 vList =  dataBuffer.get(key);
			if (vList==null){
				dataBuffer.put(key, valueList);
				pendingQueue1.offer(key);
				return;
			}
			// This part should not be here because the type is not known
			((ArrayList<Long>) vList).addAll((ArrayList<Long>) valueList);
		}
	}

/*	public void enqueuReducerBuffer(TaskData<K2, V2> td){
		if (reducerTaskOutputHandler==null && pendingQueue.size()%100==0){
			System.out.println(Setting.HOSTNAME+"<<No yet running reducer. info=> jobID: "+jobTasksManager.getJobInfo().getJobID()+", taskID: " + reduceTaskInfo.getTaskID());
		}
	//	if (pendingQueue.size()<FLUSH_SIZE)
			pendingQueue.offer(td);
		//flush process should be done here
	}
*/	
	//called by reduce()  function implemented by the user
	public void output(K3 key, V3 data) throws InterruptedException{
		OutputDataBuffer.put(key, data);
		int c = OutputDataBuffer.size();
/*		if (c%1000000==0)
			System.out.println(Setting.LOCAL_DIR+"###########"+c);
*/		//reducerTaskOutputHandler.output(key, data);
	}
	public V3 getOutputValue(K3 key) throws InterruptedException{
		return OutputDataBuffer.get(key);
	}

	
	@Override
	public void setTaskInfo(TaskInfo<K2,V2,K3,V3> taskInfo) {
		this.reduceTaskInfo =  (ReduceTaskInfo<K2, V2, K3, V3>) taskInfo;
	}

	@Override
	public boolean runPreTask() {
/*		try {
			reducerTaskOutputHandler = jobTasksManager.createOutputHandler(reduceTaskInfo);
		} catch (InstantiationException | IllegalAccessException
				| MalformedURLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
*/		return true;
	}

	@Override
	public boolean runTask() {
		while (true){
			try {
				K2 key = pendingQueue1.take();
				if (key==POISON)
					break;
				synchronized(LOCK){
					reduce(key, dataBuffer.remove(key));
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean runPostTask() {
		try {
			finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return postReduce();
	}
	public boolean finish() throws Exception {
		System.out.println("################## flushing reducer data into "+reduceTaskInfo.getOutputFullName());
		DataWriter dataWriter = reduceTaskInfo.getDataWriter(reduceTaskInfo.getOutputFullName());
		dataWriter.write(OutputDataBuffer);
		dataWriter.close();
		jobTasksManager.finishedReduceTask(reduceTaskInfo.getTaskID());
		return true;
	}

	public Integer getNumOfReceivedBuffers(Long srcID) {
		Integer numOfRcdBuffers = 0;
		synchronized(numOfReceivedBuffers){
			numOfRcdBuffers =  numOfReceivedBuffers.get(srcID);
		}
		if (numOfRcdBuffers==null)
			return 0;		
		return numOfRcdBuffers;
	}

	public void incNumOfReceivedBuffers(Long srcID) {
		synchronized(numOfReceivedBuffers){
			numOfReceivedBuffers.put(srcID, getNumOfReceivedBuffers(srcID) + 1);
		}
	}
	public void stopRunning(Long srcID, int numOfExpectedBuffers) throws InterruptedException{
		setNumOfExpectedBuffers(srcID, numOfExpectedBuffers);
		stopRunning();
		pendingQueue1.offer((K2) POISON);
		
	}
	public Integer getNumOfExpectedBuffers(Long srcID) {
		Integer numOfEBuffers = 0;
		synchronized(numOfExpectedBuffers){
			numOfEBuffers =  numOfExpectedBuffers.get(srcID);
		}
		if (numOfEBuffers==null)
			return 0;		
		return numOfEBuffers;
	}

	public void setNumOfExpectedBuffers(Long srcID, int num) {
		synchronized(numOfExpectedBuffers){
			numOfExpectedBuffers.put(srcID,num);
		}
	}
	public boolean expectBuffers(){
		synchronized(numOfExpectedBuffers){
			for(Map.Entry<Long, Integer> reducerExpectedBuffer:numOfExpectedBuffers.entrySet()){
				if(reducerExpectedBuffer.getValue() > getNumOfReceivedBuffers(reducerExpectedBuffer.getKey()))
					return true;
			}
		}
		return false;
	}

	@Override
	public TaskInfo<K2, V2, K3, V3> getTaskInfo() {
		return reduceTaskInfo;
	}

	
//	==================================================================	
	public boolean preReduce() {
		return true;
	}
	public abstract boolean reduce(K2 key, V2 value);
	
	public boolean postReduce(){
		return true;
	}
//==================================================================

}
