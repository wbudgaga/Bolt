package mr.dht.peer2peernetwork.handlers;

import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.nodes.Peer;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.resourcemanagement.execution.mrtasks.management.ResourceManager;

public class StartJobBatchHandler extends MessageHandler{
		
	@Override
	public void handle(PacketChannel pc, Message msg) {
		ResourceManager resourceManager 	= ((Peer) node).getResourceManager();
		try {
			resourceManager.execute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int getHandlerID() {
		return Message.START_JOB_BATCH;
	}
}
