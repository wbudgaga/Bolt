package mr.dht.peer2peernetwork.handlers;

import java.io.IOException;

import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.nodes.Peer;
import mr.dht.peer2peernetwork.nodes.RemotePeer;
import mr.dht.peer2peernetwork.wireformates.FingerTableRequest;
import mr.dht.peer2peernetwork.wireformates.FingerTableResponse;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.dht.peer2peernetwork.wireformates.PeerInfo;

public class FingerTableRequestHandler extends MessageHandler{
	public Peer getNode(){
		return (Peer) node;
	}
		
	private void sendFingerTableResponse(RemotePeer sender) throws IOException{
		System.out.println("sending My FT to "+sender.getID());
		PeerInfo[] allPeersInfo = getNode().getAllRemotePeersInfo();
		PeerInfo pred 				= getNode().getPredecessor().getPeerInfo();
		PeerInfo me 				= new PeerInfo();
		me.setPeer(getNode().getNodeData());
		FingerTableResponse gpr 		= new FingerTableResponse();
		gpr.setPeer(me);
		gpr.setPredecessor(pred);
		gpr.setPeerList(allPeersInfo);
		sendMessage(sender.getpChannel(), gpr);
	}

	@Override
	public void handle(PacketChannel pc, Message msg) {
		try {
			FingerTableRequest ftReqestMsg 	= (FingerTableRequest) msg;
			RemotePeer sender 		= RemotePeer.getInstance(ftReqestMsg.getPeer(),pc);
			sendFingerTableResponse(sender);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getHandlerID() {
		return Message.FINGER_TABLE_REQUEST;
	}
}
