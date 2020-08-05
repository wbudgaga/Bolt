package mr.dht.peer2peernetwork.handlers.connection;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import mr.communication.handlers.Connector;
import mr.communication.handlers.ConnectorListener;
import mr.communication.handlers.MultiMSGDecoder;
import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.nodes.Peer;
import mr.dht.peer2peernetwork.nodes.RemotePeer;
import mr.dht.peer2peernetwork.nodes.Setting;
import mr.dht.peer2peernetwork.wireformates.FindRunningReducer;
import mr.dht.peer2peernetwork.wireformates.Lookup;
import mr.resourcemanagement.execution.mrtasks.management.ResourceManager;

//Notes ==> think of timeout of pending peers
public class Connecting4ReducerPeerHandler implements ConnectorListener {
	private Peer lPeer;
	private FindRunningReducer frr;
	
	public   Connecting4ReducerPeerHandler(Peer localPeer, FindRunningReducer lookupMSG){
		lPeer 				= localPeer;
		this.frr			= lookupMSG;
	}
	@Override
	public void connectionEstablished(Connector connector, SocketChannel sc) {
		try {
			PacketChannel pChannel = new PacketChannel(sc,lPeer.getSelector(), new MultiMSGDecoder(),lPeer.getMessageHandler());    			
			RemotePeer mapPeer = RemotePeer.getInstance(frr.getSourcePeer().getPeer(), pChannel);
			if (lPeer.getResourceManager().findRunningReducer(mapPeer, frr)){//in case the reducer is running
				mapPeer.queryResult(frr.getQueryKey(), frr.getMsgUUID(), frr.getSrcPeerHandlerID(), lPeer.getNodeData());
			}
				
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
	    }
	}

	@Override
	public void connectionFailed(Connector connector, Exception cause) {}
}
