package mr.dht.peer2peernetwork.handlers.connection;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import mr.communication.handlers.Connector;
import mr.communication.handlers.ConnectorListener;
import mr.communication.handlers.MultiMSGDecoder;
import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.exceptions.InvalidFingerTableEntry;
import mr.dht.peer2peernetwork.nodes.Discovery;
import mr.dht.peer2peernetwork.nodes.Peer;
import mr.dht.peer2peernetwork.nodes.PeerData;
import mr.dht.peer2peernetwork.nodes.RemotePeer;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.dht.peer2peernetwork.wireformates.RandomPeer;
import mr.dht.peer2peernetwork.wireformates.RandomPeerRequest;

//Notes ==> think of timeout of pending peers
public class NewPeerNotifyingHandler implements ConnectorListener {
	private Peer lPeer;
	private PeerData connectingPeer;
	
	public   NewPeerNotifyingHandler(Peer localPeer, PeerData remotePeer){
		lPeer 				= localPeer;
		connectingPeer			= remotePeer;
	}
	
	
	@Override
	public synchronized void connectionEstablished(Connector connector, SocketChannel sc) {
		try {
			PacketChannel pChannel 	= new PacketChannel(sc,lPeer.getSelector(), new MultiMSGDecoder(),lPeer.getMessageHandler());    
			RemotePeer newPeer 	= RemotePeer.getInstance(connectingPeer, pChannel);
			lPeer.addNewPeer(newPeer);
			newPeer.setSuccessor(lPeer.getNodeData());
		} catch (IOException | ClassNotFoundException | InvalidFingerTableEntry  e) {
			e.printStackTrace();
	    }
	}

	@Override
	public void connectionFailed(Connector connector, Exception cause) {
		// TODO Auto-generated method stub
	}
}
