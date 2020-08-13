package mr.dht.peer2peernetwork.handlers;

import java.io.IOException;

import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.nodes.Peer;
import mr.dht.peer2peernetwork.nodes.PeerData;
import mr.dht.peer2peernetwork.nodes.RemotePeer;
import mr.dht.peer2peernetwork.wireformates.GetSuccessor;
import mr.dht.peer2peernetwork.wireformates.GetSuccessorResponse;
import mr.dht.peer2peernetwork.wireformates.Message;

public class GetSuccessorHandler extends MessageHandler{
	public Peer getNode(){
		return (Peer) node;
	}
		
	private void sendGetSuccessorResponse(RemotePeer sender, RemotePeer succ) throws IOException{
		GetSuccessorResponse gpr 		= new GetSuccessorResponse();
		gpr.setPeer(succ.getNodeData());
		sendMessage(sender.getpChannel(), gpr);
	}

	@Override
	public void handle(PacketChannel pc, Message msg) {
		try {
			RemotePeer succ 		= getNode().getSuccessor();
			GetSuccessor getSuccMsg 	= (GetSuccessor) msg;
			PeerData senderInfo 		= getSuccMsg.getPeer();
			if (senderInfo.getPeerID() == succ.getID())
				return;
			System.out.println(senderInfo.getPeerID() + " asked me about my succ:  " + succ.getID());
			RemotePeer sender 		= RemotePeer.getInstance(senderInfo, pc);
			sendGetSuccessorResponse(sender, succ);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getHandlerID() {
		return Message.GET_SUCCESSOR;
	}
}
