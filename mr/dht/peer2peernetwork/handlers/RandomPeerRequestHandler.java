package mr.dht.peer2peernetwork.handlers;

import java.io.IOException;

import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.nodes.Discovery;
import mr.dht.peer2peernetwork.nodes.RemotePeer;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.dht.peer2peernetwork.wireformates.RandomPeer;
import mr.dht.peer2peernetwork.wireformates.RandomPeerRequest;

public class RandomPeerRequestHandler extends MessageHandler{
	private  Discovery getNode(){
		return (Discovery) node;
	}
	
	public void handleRandomPeerRequest(PacketChannel pc, RandomPeerRequest message) {
		Discovery discovery 	= getNode();
		RemotePeer randomPeer 	= discovery.getRandomPeer();
		System.out.println("RandomPeerRequest message has been received...");	
		RandomPeer npMSG = new RandomPeer();
		if (randomPeer!=null)
			npMSG.setPeer(randomPeer.getNodeData());
		else 
			npMSG.setPeer(discovery.getNodeData());
		sendMessage(pc, npMSG);
	}
	
	@Override
	public void handle(PacketChannel pc, Message msg) {
		handleRandomPeerRequest(pc, (RandomPeerRequest)msg);
	}

	@Override
	public int getHandlerID() {
		return Message.RANDOM_PEER_REQUEST;
	}
}
