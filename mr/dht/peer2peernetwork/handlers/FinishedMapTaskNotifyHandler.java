package mr.dht.peer2peernetwork.handlers;

import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.nodes.Peer;
import mr.dht.peer2peernetwork.nodes.Setting;
import mr.dht.peer2peernetwork.wireformates.FinishedMapTaskNotify;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.dht.peer2peernetwork.wireformates.PeerInfo;
import mr.resourcemanagement.execution.mrtasks.management.JobTasksManager;

public class FinishedMapTaskNotifyHandler extends MessageHandler{
	
	public synchronized boolean handleJobManagerAboutFinishedMap(PacketChannel pc, FinishedMapTaskNotify finishedMapTaskNotify) throws Exception{
		PeerInfo pd 	= finishedMapTaskNotify.getPeer();
		long 	 jobID 	= finishedMapTaskNotify.getJobID();	
		JobTasksManager jTaskManager = ((Peer) node).getResourceManager().getJobTaskManager(jobID);
		if (jTaskManager== null){
			System.out.println("handleJobManagerAboutFinishedMap: There is no task for a given jobID:========>"+jobID);
			return false;
		}
		int remainingMaps = jTaskManager.decreaseMapRef(finishedMapTaskNotify.getNumOfFinishedMaps());
		//System.out.println(Setting.LOCAL_DIR+"#################   reducer received finshed map notificastion  ===>"+remainingMaps);
		
		if (remainingMaps==0){
			jTaskManager.stopReducers(pd.getPeer().getPeerID(), finishedMapTaskNotify.getNumOfSentBuffers());
		}
		return true;
	}	
	
	@Override
	public void handle(PacketChannel pc, Message msg) {
		try {
			handleJobManagerAboutFinishedMap(pc, (FinishedMapTaskNotify)msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getHandlerID() {
		return Message.FINISHEDMAPTASKNOTIFY;
	}
}
