package mr.dht.peer2peernetwork.handlers;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.exceptions.InvalidFingerTableEntry;
import mr.dht.peer2peernetwork.handlers.MessageHandler;
import mr.dht.peer2peernetwork.nodes.Peer;
import mr.dht.peer2peernetwork.nodes.RemotePeer;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.dht.peer2peernetwork.wireformates.MessageFactory;
import mr.dht.peer2peernetwork.wireformates.PeerInfo;
import mr.dht.peer2peernetwork.wireformates.QueryResult;
import mr.dht.peer2peernetwork.wireformates.TextNumTaskData;
import mr.resourcemanagement.execution.mrtasks.management.JobTasksManager;

public class NotifyFinishedMapHandler extends MessageHandler{
	public Peer getNode(){
		return (Peer) node;
	}
	
	public void handleQueryResult(PacketChannel pc, QueryResult queryResult) throws IOException {
		RemotePeer rp 				= new RemotePeer(queryResult.getPeer().getPeer());
		JobTasksManager jTaskManager 		=  ((Peer) node).getResourceManager().pollMSGJob(queryResult.getMsgUUID());
		if (jTaskManager == null){
			System.out.println("NotifyFinishedMapHandler: There is no job for the message with UUID"+queryResult.getMsgUUID());
			return;
		}
		jTaskManager.publishFinishedMap(queryResult.getMsgUUID(),rp);
	}	
	
	@Override
	public void handle(PacketChannel pc, Message msg) {
		QueryResult queryResult 		= (QueryResult) msg;
		try {
			handleQueryResult(pc, queryResult);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getHandlerID() {
		return Message.FINISHEDMAPTASK_NOTIFYQRESULT;
	}
}
