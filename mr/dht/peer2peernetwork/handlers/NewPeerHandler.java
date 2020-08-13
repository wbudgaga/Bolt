package mr.dht.peer2peernetwork.handlers;

import java.io.IOException;

import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.exceptions.InvalidFingerTableEntry;
import mr.dht.peer2peernetwork.nodes.Peer;
import mr.dht.peer2peernetwork.nodes.RemotePeer;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.dht.peer2peernetwork.wireformates.NewPeer;

public class NewPeerHandler extends MessageHandler{
	public Peer getNode(){
		return (Peer) node;
	}
	
	@Override
	public void handle(PacketChannel pc, Message msg) {
		NewPeer peerInfo 		= (NewPeer) msg;
		try {
			getNode().addNewPeer(RemotePeer.getInstance(peerInfo,pc));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFingerTableEntry e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int getHandlerID() {
		return Message.New_PEER;
	}
}
