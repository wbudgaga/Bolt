package mr.dht.peer2peernetwork.handlers;

import java.io.IOException;

import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.exceptions.InvalidFingerTableEntry;
import mr.dht.peer2peernetwork.nodes.Peer;
import mr.dht.peer2peernetwork.nodes.RemotePeer;
import mr.dht.peer2peernetwork.wireformates.FingerTableResponse;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.dht.peer2peernetwork.wireformates.PeerInfo;
import mr.dht.peer2peernetwork.wireformates.PeersList;

public class PeersListHandler extends MessageHandler{
	public Peer getNode(){
		return (Peer) node;
	}
		
	@Override
	public void handle(PacketChannel pc, Message msg) {
		try {
			PeersList peersInfoListMsg 	= (PeersList) msg;
			System.out.println("PeersList received ");
			for(PeerInfo pi: peersInfoListMsg.getPeerList()){
				getNode().notifyRemotePeer(pi.getPeer());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidFingerTableEntry e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getHandlerID() {
		return Message.PEERS_LIST;
	}
}
