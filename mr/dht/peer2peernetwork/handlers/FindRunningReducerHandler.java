package mr.dht.peer2peernetwork.handlers;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.exceptions.InvalidFingerTableEntry;
import mr.dht.peer2peernetwork.handlers.MessageHandler;
import mr.dht.peer2peernetwork.handlers.connection.Connecting4QueryResultsHandler;
import mr.dht.peer2peernetwork.handlers.connection.Connecting4ReducerPeerHandler;
import mr.dht.peer2peernetwork.nodes.Peer;
import mr.dht.peer2peernetwork.nodes.PeerData;
import mr.dht.peer2peernetwork.nodes.RemotePeer;
import mr.dht.peer2peernetwork.nodes.Setting;
import mr.dht.peer2peernetwork.wireformates.FindRunningReducer;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.dht.peer2peernetwork.wireformates.MessageFactory;
import mr.dht.peer2peernetwork.wireformates.PeerInfo;
import mr.dht.peer2peernetwork.wireformates.QueryResult;
import mr.dht.peer2peernetwork.wireformates.TextNumTaskData;
import mr.resourcemanagement.execution.mrtasks.management.DataRouter;
import mr.resourcemanagement.execution.mrtasks.management.JobTasksManager;

public class FindRunningReducerHandler extends MessageHandler{
	public Peer getNode(){
		return (Peer) node;
	}
	
	@Override
	public void handle(PacketChannel pc, Message msg) {
		FindRunningReducer frr 				= (FindRunningReducer) msg;
		Peer lPeer 					= getNode();
		try {
			RemotePeer peer 			= lPeer.getResponsiblePeer(frr.getQueryKey());
			if (peer == null){// lPerr is the responsible
				RemotePeer mapPeer 		= lPeer.getQueryPeer(frr);
				if (mapPeer == null){
					PeerData srcPeer 	= frr.getSourcePeer().getPeer();
					lPeer.initiateConnectionManager(srcPeer.getHost(),srcPeer.getPortNum(), new Connecting4ReducerPeerHandler(lPeer, frr));
					return;
				}
				if (lPeer.getResourceManager().findRunningReducer(mapPeer, frr)){//in case the reducer is running
					mapPeer.queryResult(frr.getQueryKey(), frr.getMsgUUID(), frr.getSrcPeerHandlerID(), lPeer.getNodeData());
				}
			}else
				peer.forward(frr);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getHandlerID() {
		return Message.FIND_RUNNING_REDUCER;
	}
}
