package mr.dht.peer2peernetwork.handlers;

import java.io.IOException;

import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.exceptions.InvalidFingerTableEntry;
import mr.dht.peer2peernetwork.nodes.Peer;
import mr.dht.peer2peernetwork.nodes.RemotePeer;
import mr.dht.peer2peernetwork.wireformates.GetSuccessorResponse;
import mr.dht.peer2peernetwork.wireformates.Message;

public class GetSuccessorResponseHandler extends MessageHandler{
	public Peer getNode(){
		return (Peer) node;
	}
	@Override
	public void handle(PacketChannel pc, Message msg) {
		try {
			System.out.println("GetSuccessor response receieved ");
			GetSuccessorResponse responseMsg = (GetSuccessorResponse) msg;
			RemotePeer rp = RemotePeer.getInstance(responseMsg.getPeer(),pc);
		
			getNode().setPredecessor(rp);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidFingerTableEntry e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public int getHandlerID() {
		return Message.GET_SUCCESSOR_RESPONSE;
	}
}
