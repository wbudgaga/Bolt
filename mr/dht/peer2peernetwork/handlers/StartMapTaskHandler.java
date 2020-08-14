package mr.dht.peer2peernetwork.handlers;

import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.Socket;

import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.exceptions.InvalidFingerTableEntry;
import mr.dht.peer2peernetwork.nodes.Peer;
import mr.dht.peer2peernetwork.nodes.RemotePeer;
import mr.dht.peer2peernetwork.nodes.Setting;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.dht.peer2peernetwork.wireformates.PeerInfo;
import mr.dht.peer2peernetwork.wireformates.StartMapTask;
import mr.resourcemanagement.execution.mrtasks.JobInfo;
import mr.resourcemanagement.execution.mrtasks.MapTaskInfo;
import mr.resourcemanagement.execution.mrtasks.management.JobTasksManager;
import mr.resourcemanagement.execution.mrtasks.management.ResourceManager;

public class StartMapTaskHandler extends MessageHandler{
	
	public void startMapTask(PacketChannel pc, StartMapTask startMRTaskMSG){
		//PeerInfo pd 						= startMRTaskMSG.getPeer();
		long 	 jobID 						= startMRTaskMSG.getJobID();
		long 	 taskID						= startMRTaskMSG.getTaskID();
		ResourceManager resourceManager 			= ((Peer) node).getResourceManager();
		try {
			JobTasksManager jTaskManager 			= resourceManager.getOrCreateJobTaskManager(jobID);
			if (jTaskManager.hasAlreadyMTask(taskID)){
				System.out.println("It could not process the mapTask because a task with the same id(jobID:" + jobID + ", taskID:" + taskID + ") is already exist");
				return;
			}
			jTaskManager.incLocMaps();
			// this data could be redundant. this should be solved in future
			JobInfo jobInfo 				= jTaskManager.getJobInfo(); 
			jobInfo.setMTaskClassName(startMRTaskMSG.getTaskClassName());
			jobInfo.setNumOfReducer(startMRTaskMSG.getNumOfReducers());
			jobInfo.setOutputName(startMRTaskMSG.getOutputName());
			////////////////////////////////////////////////////////////////
			MapTaskInfo<Long,String,String,Long> mapTask = new MapTaskInfo<Long, String, String, Long>();
			mapTask.setInputPath(Setting.DATA_DIR+"/d0/"+startMRTaskMSG.getTaskID());
			mapTask.setJobID(jobID);
			mapTask.setTaskID(taskID);
			mapTask.setPartitionerClassName("mr.resourcemanagement.datapartitioning.ModPartitioner");
			
			resourceManager.processTask(mapTask);		
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}	
	
	@Override
	public void handle(PacketChannel pc, Message msg) {
		startMapTask(pc, (StartMapTask)msg);
	}

	@Override
	public int getHandlerID() {
		return Message.START_MAPTASK;
	}
}
/*public <K1,V1,K2,V2>  void processMapTask(int refCount, MapTaskInfo<K1,V1,K2,V2> mapTaskInfo) throws Exception{
	long jobID = mapTaskInfo.getJobID();
	JobTasksManager jTaskManager = getOrCreateJobTaskManager(jobID);
	jTaskManager.processMapTask(refCount, mapTaskInfo);			
}

public <K1,V1,K2,V2>  void processMapTask(int refCount, MapTaskInfo<K1,V1,K2,V2> mapTaskInfo) throws Exception{
	long jobID = mapTaskInfo.getJobID();
	JobTasksManager jTaskManager = getOrCreateJobTaskManager(jobID);
	jTaskManager.processMapTask(refCount, mapTaskInfo);			
}
*/
