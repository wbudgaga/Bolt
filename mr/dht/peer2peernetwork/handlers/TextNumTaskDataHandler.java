package mr.dht.peer2peernetwork.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.nodes.Peer;
import mr.dht.peer2peernetwork.nodes.Setting;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.dht.peer2peernetwork.wireformates.PeerInfo;
import mr.dht.peer2peernetwork.wireformates.TextNumTaskData;
import mr.resourcemanagement.datatype.TaskData;
import mr.resourcemanagement.execution.mrtasks.ReduceTask;
import mr.resourcemanagement.execution.mrtasks.management.JobTasksManager;

public class TextNumTaskDataHandler extends MessageHandler{
	public boolean handleArrivedTaskData(PacketChannel pc, TextNumTaskData textNumTaskData) throws Exception{
		PeerInfo pd 				= textNumTaskData.getPeer();
		long 	 jobID 	= textNumTaskData.getJobID();
		JobTasksManager jTaskManager = ((Peer) node).getResourceManager().getJobTaskManager(jobID);
		if (jTaskManager== null){
			System.out.println("there is no job(id:"+ jobID+" for a given!");
			return false;
		}		
		
		ReduceTask rTask = jTaskManager.getReduceTask(textNumTaskData.getTaskID());
		if (rTask==null){
			System.out.println(textNumTaskData.getTaskID()+": getTaskIDxxxxxxxxxxxxxxxxxjobID="+jTaskManager.getJobInfo().getJobID()+"xxxxxxxxxxxxxxxxxxxxxxxxreducerExist: "+jTaskManager.hasAlreadyRTask(textNumTaskData.getTaskID())+"..."+Setting.HOSTNAME);
			return true;
			//throw new Exception(textNumTaskData.getTaskID()+": getTaskIDxxxxxxxxxxxxxxxxxjobID="+jTaskManager.getJobInfo().getJobID()+"xxxxxxxxxxxxxxxxxxxxxxxxreducerExist: "+jTaskManager.hasAlreadyRTask(textNumTaskData.getTaskID())+"..."+Setting.HOSTNAME);
		}

		HashMap buf = textNumTaskData.getDataBuf();
		feedReducer(pd.getPeer().getPeerID(), rTask, buf);
		return true;
	}	
	
	public void  feedReducer(long srcPeerID, ReduceTask rTask, HashMap<String, ArrayList<Long>> buf ) throws Exception{
		for (Map.Entry<String, ArrayList<Long>> e:buf.entrySet()){
			rTask.offer(e.getKey(), e.getValue());
		}
		rTask.incNumOfReceivedBuffers(srcPeerID);
		//FWLogger.getInstance().log(jobID, rTask.getNumOfReceivedBuffers(srcPeerID)+" buffers have been received from :========>"+srcPeerID+"..........size: "+buf.size() +"   ");
	}

	@Override
	public void handle(PacketChannel pc, Message msg) {
		try {
			handleArrivedTaskData(pc, (TextNumTaskData)msg);
		} catch (Exception e) {
			
			e.printStackTrace();
			System.exit(0);
		}
	}

	@Override
	public int getHandlerID() {
		return Message.TEXTNUM_TASKDATA;
	}
}
