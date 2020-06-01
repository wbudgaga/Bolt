package mr.dht.peer2peernetwork.handlers.client_handlers;

import java.io.IOException;
import java.net.Socket;

import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.exceptions.InvalidFingerTableEntry;
import mr.dht.peer2peernetwork.handlers.MessageHandler;
import mr.dht.peer2peernetwork.handlers.connection.ClientPeerConnectingHandler;
import mr.dht.peer2peernetwork.handlers.connection.RandomPeerRequestConnectingHandler;
import mr.dht.peer2peernetwork.nodes.Client;
import mr.dht.peer2peernetwork.nodes.PeerData;
import mr.dht.peer2peernetwork.nodes.RemotePeer;
import mr.dht.peer2peernetwork.nodes.Setting;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.dht.peer2peernetwork.wireformates.RandomPeer;

public class RandomPeerHandler extends MessageHandler{	

	@Override
	public int getHandlerID() {
		return Message.RANDOM_PEER;
	}

	@Override
	public void handle(PacketChannel pc, Message msg) {
		PeerData 	pd = ((RandomPeer)msg).getPeer();
		try {
			RemotePeer randomPeer = RemotePeer.getInstance(pd, pc);
			//((Client) node).handleRandomPeer(randomPeer);
			((Client) node).initiateConnectionManager(randomPeer.getNodeData().getHost(), randomPeer.getNodeData().getPortNum(), new ClientPeerConnectingHandler(((Client) node), pd));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
