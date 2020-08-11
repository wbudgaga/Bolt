package mr.dht.peer2peernetwork.handlers.mr_handlers;

import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.Socket;

import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.exceptions.InvalidFingerTableEntry;
import mr.dht.peer2peernetwork.handlers.MessageHandler;
import mr.dht.peer2peernetwork.nodes.Peer;
import mr.dht.peer2peernetwork.nodes.RemotePeer;
import mr.dht.peer2peernetwork.nodes.Setting;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.dht.peer2peernetwork.wireformates.PeerInfo;
import mr.dht.peer2peernetwork.wireformates.StartMapTask;
import mr.resourcemanagement.execution.mrtasks.MapTaskInfo;
import mr.resourcemanagement.execution.mrtasks.management.JobTasksManager;
import mr.resourcemanagement.execution.mrtasks.management.ResourceManager;

public class StartMapTaskHandler extends MessageHandler{
	
	public void startMapTask(PacketChannel pc, StartMapTask startMRTaskMSG){
		PeerInfo pd 						= startMRTaskMSG.getPeer();
		long 	 jobID 						= startMRTaskMSG.getJobID();
		MapTaskInfo<Long,String,String,Long> mapTask 		= new MapTaskInfo<Long, String, String, Long>();
		mapTask.setDataPath(Setting.MRJOBS_DIR + startMRTaskMSG.getDataInputPath());
		mapTask.setTaskClassName(startMRTaskMSG.getTaskClassName());
		mapTask.setTaskID(startMRTaskMSG.getTaskID());
		mapTask.setJobID(jobID);
		mapTask.setNumOfReducer(startMRTaskMSG.getNumOfReducers());
		mapTask.setPartitionerClassName("mr.resourcemanagement.datapartitioning.ModPartitioner");
		try {
			JobTasksManager jTaskManager 			= ((Peer) node).getResourceManager().getOrCreateJobTaskManager(jobID);
			jTaskManager.processMapTask(mapTask);		
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
		return Message.START_MAPTask;
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
