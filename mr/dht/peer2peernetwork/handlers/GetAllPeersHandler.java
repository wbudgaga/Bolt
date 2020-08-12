package mr.dht.peer2peernetwork.handlers;

import java.io.IOException;

import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.nodes.Discovery;
import mr.dht.peer2peernetwork.nodes.RemotePeer;
import mr.dht.peer2peernetwork.wireformates.GetAllPeers;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.dht.peer2peernetwork.wireformates.PeerInfo;
import mr.dht.peer2peernetwork.wireformates.PeersList;

public class GetAllPeersHandler extends MessageHandler{
	private  Discovery getNode(){
		return (Discovery) node;
	}
	
	public synchronized void handleGetAllPeers(PacketChannel pc, GetAllPeers message) {
		try{
			PeerInfo[] allPeersInfo 		= getNode().getPeerInfoList();
			if (allPeersInfo != null)
				sendFingerTableResponse(pc, allPeersInfo);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
		
	private void sendFingerTableResponse(PacketChannel pc, PeerInfo[] allPeersInfo) throws IOException{
		PeersList peersListMsg 				= new PeersList();
		peersListMsg.setPeerList(allPeersInfo);
		sendMessage(pc, peersListMsg);
	}
	
	@Override
	public void handle(PacketChannel pc, Message msg) {
		handleGetAllPeers(pc, (GetAllPeers)msg);
	}

	@Override
	public int getHandlerID() {
		return Message.GET_ALLPEERS;
	}
}
