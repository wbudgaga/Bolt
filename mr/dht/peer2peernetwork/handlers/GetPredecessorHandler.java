package mr.dht.peer2peernetwork.handlers;

import java.io.IOException;

import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.nodes.Peer;
import mr.dht.peer2peernetwork.nodes.PeerData;
import mr.dht.peer2peernetwork.nodes.RemotePeer;
import mr.dht.peer2peernetwork.wireformates.GetPredecessor;
import mr.dht.peer2peernetwork.wireformates.GetPredecessorResponse;
import mr.dht.peer2peernetwork.wireformates.Message;

public class GetPredecessorHandler extends MessageHandler{
	public Peer getNode(){
		return (Peer) node;
	}
		
	private void sendGetPredecessorResponse(RemotePeer sender,  RemotePeer predPeer) throws IOException{
		GetPredecessorResponse gpr 		= new GetPredecessorResponse();
		gpr.setPeer(predPeer.getNodeData());
		sender.message(gpr);
	}
	@Override
	public void handle(PacketChannel pc, Message msg) {
		RemotePeer pred 			= getNode().getPredecessor();
		GetPredecessor getPredMsg 		= (GetPredecessor) msg;
		PeerData senderInfor 			= getPredMsg.getPeer();
		
		if (senderInfor.getPeerID() == pred.getID())
			return;
		System.out.println(senderInfor.getPeerID()+" asked me about my pred:  "+ pred.getID());
		RemotePeer sender 			= RemotePeer.getInstance(senderInfor);
		try {
			sendGetPredecessorResponse(sender,pred);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	@Override
	public int getHandlerID() {
		return Message.GET_PREDECESSOR;
	}
}
