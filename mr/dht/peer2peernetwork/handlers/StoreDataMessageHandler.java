package mr.dht.peer2peernetwork.handlers;


import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.nodes.Peer;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.dht.peer2peernetwork.wireformates.StoreFileRequest;
import mr.resourcemanagement.execution.mrtasks.management.CopyAndForwardTask;

public class StoreDataMessageHandler extends MessageHandler{
	public void storeChunk(PacketChannel pc, StoreFileRequest chunkMSG) {
		//System.out.println(node.getID()+" Chunk part recieved>> "+chunkMSG.getFileName());
		Peer lp = (Peer) node; 
		if (!lp.inPeerRange(Long.parseLong(chunkMSG.getFileName()))){
			System.out.println("Peer (ID:"+lp.getID()+") received stronger chunk with id: "+chunkMSG.getFileName()+"   "+pc.getSocketChannel().socket());
			//System.exit(0);
		}
		CopyAndForwardTask cfTask = new CopyAndForwardTask(chunkMSG, lp);
		lp.getResourceManager().executeTask(cfTask);
	}	

	@Override
	public void handle(PacketChannel pc, Message msg) {
		storeChunk(pc, (StoreFileRequest)msg);
	}

	@Override
	public int getHandlerID() {
		return Message.STORE_FILE_REQUEST;
	}

}
