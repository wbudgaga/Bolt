package mr.dht.peer2peernetwork.handlers;

import java.net.Socket;

import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.nodes.Peer;
import mr.dht.peer2peernetwork.nodes.Setting;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.dht.peer2peernetwork.wireformates.PeerInfo;
import mr.dht.peer2peernetwork.wireformates.StartReduceTask;
import mr.resourcemanagement.execution.mrtasks.JobInfo;
import mr.resourcemanagement.execution.mrtasks.MapTaskInfo;
import mr.resourcemanagement.execution.mrtasks.ReduceTaskInfo;
import mr.resourcemanagement.execution.mrtasks.management.JobTasksManager;
import mr.resourcemanagement.execution.mrtasks.management.ResourceManager;

public class StartReduceTaskHandler extends MessageHandler{	
	public void startReduceTask(PacketChannel pc, StartReduceTask startMRTaskMSG){
		PeerInfo pd 					= startMRTaskMSG.getPeer();
		long 	 jobID 					= startMRTaskMSG.getJobID();
		long 	 taskID					= startMRTaskMSG.getTaskID();
		
		ResourceManager resourceManager 		= ((Peer) node).getResourceManager();
		try {
			JobTasksManager jTaskManager 		= resourceManager.getOrCreateJobTaskManager(jobID);
			if (jTaskManager.hasAlreadyRTask(taskID)){
				System.out.println("It could not process the reduce task because a task with the same id(" + taskID + ") is already exist");
				return;
			}
			jTaskManager.incLocReducers();
			// this data could be redundant. this should be solved in future
			JobInfo jobInfo = jTaskManager.getJobInfo(); 
			jobInfo.setRTaskClassName(startMRTaskMSG.getTaskClassName());
			
			ReduceTaskInfo<Long,String,String,Long> reduceTaskInfo = new ReduceTaskInfo<Long, String, String, Long>(startMRTaskMSG.getNumberOfMappers());
			reduceTaskInfo.setJobID(jobID);
			reduceTaskInfo.setTaskID(taskID);
			resourceManager.processTask(reduceTaskInfo);		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	@Override
	public void handle(PacketChannel pc, Message msg) {
		startReduceTask(pc, (StartReduceTask)msg);
	}

	@Override
	public int getHandlerID() {
		return Message.START_REDUCETask;
	}
}
