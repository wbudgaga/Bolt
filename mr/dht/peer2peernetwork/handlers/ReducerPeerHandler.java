package mr.dht.peer2peernetwork.handlers;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.exceptions.InvalidFingerTableEntry;
import mr.dht.peer2peernetwork.handlers.MessageHandler;
import mr.dht.peer2peernetwork.nodes.Peer;
import mr.dht.peer2peernetwork.nodes.RemotePeer;
import mr.dht.peer2peernetwork.nodes.Setting;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.dht.peer2peernetwork.wireformates.MessageFactory;
import mr.dht.peer2peernetwork.wireformates.PeerInfo;
import mr.dht.peer2peernetwork.wireformates.QueryResult;
import mr.dht.peer2peernetwork.wireformates.TextNumTaskData;
import mr.resourcemanagement.execution.mrtasks.management.DataRouter;
import mr.resourcemanagement.execution.mrtasks.management.JobTasksManager;

public class ReducerPeerHandler extends MessageHandler{
	public Peer getNode(){
		return (Peer) node;
	}
	
	public void handleQueryResult(PacketChannel pc, QueryResult queryResult) throws IOException {
		RemotePeer rp 	q				= new RemotePeer(queryResult.getPeer().getPeer(), pc);
		Peer lPeer 					= (Peer) node;
		lPeer.cachePeer(rp);
		JobTasksManager jTaskManager 			=  lPeer.getResourceManager().pollMSGJob(queryResult.getMsgUUID());
		if (jTaskManager == null){
			System.out.println(Setting.HOSTNAME + "There is no job for the message with UUID" + queryResult.getMsgUUID());
			return;
		}
		//System.out.println("the Peer that has ID: " +rp.getNodeData().getPeerID()+ " is reponsible for routing K: "+ queryResult.getQueryKey());
		DataRouter dataRouter = jTaskManager.getDataRouter();
		dataRouter.setRoutingPeer(queryResult.getQueryKey(), rp);
	}	
	
	@Override
	public void handle(PacketChannel pc, Message msg) {
		QueryResult queryResult = (QueryResult) msg;
		try {
			handleQueryResult(pc, queryResult);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getHandlerID() {
		return Message.REDUCERPEER;
	}
}
