package mr.dht.peer2peernetwork.handlers.connection;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import mr.communication.handlers.Connector;
import mr.communication.handlers.ConnectorListener;
import mr.communication.handlers.MultiMSGDecoder;
import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.exceptions.InvalidFingerTableEntry;
import mr.dht.peer2peernetwork.nodes.Client;
import mr.dht.peer2peernetwork.nodes.Peer;
import mr.dht.peer2peernetwork.nodes.PeerData;
import mr.dht.peer2peernetwork.nodes.RemotePeer;

//Notes ==> think of timeout of pending peers
public class ClientPeerConnectingHandler implements ConnectorListener {
	private Client lPeer;
	private PeerData connectingPeer;
	
	public   ClientPeerConnectingHandler(Client localPeer, PeerData srcPeer){
		lPeer 					= localPeer;
		connectingPeer				= srcPeer;
	}
	
	@Override
	public void connectionEstablished(Connector connector, SocketChannel sc) {
		try {
			PacketChannel pChannel 		= new PacketChannel(sc,lPeer.getSelector(), new MultiMSGDecoder(),lPeer.getMessageHandler());    			
			RemotePeer newPeer 		= RemotePeer.getInstance(connectingPeer, pChannel);
			lPeer.handleRandomPeer(newPeer);

		} catch (IOException | ClassNotFoundException | InvalidFingerTableEntry e) {
			e.printStackTrace();
	    }
	}

	@Override
	public void connectionFailed(Connector connector, Exception cause) {
		// TODO Auto-generated method stub
	}
}
