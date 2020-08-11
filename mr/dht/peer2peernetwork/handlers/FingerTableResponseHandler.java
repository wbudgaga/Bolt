package mr.dht.peer2peernetwork.handlers;

import java.io.IOException;

import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.exceptions.InvalidFingerTableEntry;
import mr.dht.peer2peernetwork.nodes.Peer;
import mr.dht.peer2peernetwork.nodes.RemotePeer;
import mr.dht.peer2peernetwork.wireformates.FingerTableResponse;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.dht.peer2peernetwork.wireformates.PeerInfo;

public class FingerTableResponseHandler extends MessageHandler{
	public Peer getNode(){
		return (Peer) node;
	}
		
	@Override
	public void handle(PacketChannel pc, Message msg) {
		try {
			FingerTableResponse ftRespMsg 		= (FingerTableResponse) msg;
			RemotePeer peer 			= RemotePeer.getInstance(ftRespMsg.getPredecessor());
			getNode().addNewPeer(peer);
			peer 					= RemotePeer.getInstance(ftRespMsg.getPeer());
			getNode().addNewPeer(peer);
			System.out.println("ft received from " + peer.getID());
			for(PeerInfo pi: ftRespMsg.getPeerList()){
				peer 				= RemotePeer.getInstance(pi);
				getNode().addNewPeer(peer);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidFingerTableEntry e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getHandlerID() {
		return Message.FINGER_TABLE_RESPONSE;
	}
}
