package mr.dht.peer2peernetwork.handlers;

import java.io.IOException;

import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.exceptions.InvalidFingerTableEntry;
import mr.dht.peer2peernetwork.nodes.Peer;
import mr.dht.peer2peernetwork.nodes.PeerData;
import mr.dht.peer2peernetwork.nodes.RemotePeer;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.dht.peer2peernetwork.wireformates.RandomPeer;

public class RandomPeerHandler extends MessageHandler{
	
	public void handleRandomPeer(PacketChannel pc, RandomPeer randomPeerMSG){// pc represents discovery node
		PeerData 	pd = randomPeerMSG.getPeer();
		try {
			
			RemotePeer randomPeer = RemotePeer.getInstance(pd, pc);
			System.out.println("RandomPeer recieved   "+randomPeer.getID());
			((Peer) node).handleRandomPeer(randomPeer);
		} catch (InvalidFingerTableEntry | IOException e1) {
			e1.printStackTrace();
		}
		
	}
	
	@Override
	public void handle(PacketChannel pc, Message msg) {
		handleRandomPeer(pc, (RandomPeer)msg);
	}

	@Override
	public int getHandlerID() {
		return Message.RANDOM_PEER;
	}
}
