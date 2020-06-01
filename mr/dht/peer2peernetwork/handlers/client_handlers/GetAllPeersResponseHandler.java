package mr.dht.peer2peernetwork.handlers.client_handlers;

import java.io.IOException;

import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.handlers.MessageHandler;
import mr.dht.peer2peernetwork.handlers.connection.ClientPeerConnectingHandler;
import mr.dht.peer2peernetwork.nodes.Client;
import mr.dht.peer2peernetwork.nodes.DataStagger;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.dht.peer2peernetwork.wireformates.PeerInfo;
import mr.dht.peer2peernetwork.wireformates.PeersList;

public class GetAllPeersResponseHandler extends MessageHandler{
	public Client getNode(){
		return (Client) node;
	}
		
	@Override
	public void handle(PacketChannel pc, Message msg) {
		try {
			PeersList peersInfoListMsg = (PeersList) msg;
			Client client = getNode();
			System.out.println("PeersList received "+peersInfoListMsg.getPeerList().length);
			for(PeerInfo pi: peersInfoListMsg.getPeerList()){
				client.initiateConnectionManager(pi.getPeer().getHost(),pi.getPeer().getPortNum(), new ClientPeerConnectingHandler(client,pi.getPeer()));
			}
			client.setNumOfClusterPeers(peersInfoListMsg.getPeerList().length);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getHandlerID() {
		return Message.PEERS_LIST;
	}
}
