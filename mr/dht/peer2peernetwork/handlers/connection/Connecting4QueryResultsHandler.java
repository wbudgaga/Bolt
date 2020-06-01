package mr.dht.peer2peernetwork.handlers.connection;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import mr.communication.handlers.Connector;
import mr.communication.handlers.ConnectorListener;
import mr.communication.handlers.MultiMSGDecoder;
import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.nodes.Peer;
import mr.dht.peer2peernetwork.nodes.RemotePeer;
import mr.dht.peer2peernetwork.wireformates.Lookup;

//Notes ==> think of timeout of pending peers
public class Connecting4QueryResultsHandler implements ConnectorListener {
	private Peer lPeer;
	private Lookup lookupMSG;
	
	public   Connecting4QueryResultsHandler(Peer localPeer, Lookup lookupMSG){
		lPeer 			= localPeer;
		this.lookupMSG	= lookupMSG;
	}
	
	@Override
	public void connectionEstablished(Connector connector, SocketChannel sc) {
		try {
			PacketChannel pChannel = new PacketChannel(sc,lPeer.getSelector(), new MultiMSGDecoder(),lPeer.getMessageHandler());    			
			RemotePeer newPeer = RemotePeer.getInstance(lookupMSG.getSourcePeer().getPeer(), pChannel);
			newPeer.queryResult(lookupMSG.getQueryKey(), lookupMSG.getMsgUUID(), lookupMSG.getSrcPeerHandlerID(), lPeer.getNodeData());

		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
	    }
	}

	@Override
	public void connectionFailed(Connector connector, Exception cause) {}
}
