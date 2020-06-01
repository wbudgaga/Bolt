package mr.dht.peer2peernetwork.handlers.connection;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import mr.communication.handlers.Connector;
import mr.communication.handlers.ConnectorListener;
import mr.communication.handlers.MultiMSGDecoder;
import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.handlers.MessageHandler;
import mr.dht.peer2peernetwork.nodes.Client;
import mr.dht.peer2peernetwork.wireformates.RandomPeerRequest;

public class RandomPeerRequestConnectingHandler implements ConnectorListener {
	private Client  localPeer;
	
	public  RandomPeerRequestConnectingHandler(Client localPeer){
		this.localPeer = localPeer;
	}
	
	public void sendRequest(PacketChannel pc) throws IOException{
		RandomPeerRequest randomPeerRequestMSG = new RandomPeerRequest();
		randomPeerRequestMSG.setPeer(localPeer.getNodeData());
		MessageHandler.sendMessage(pc, randomPeerRequestMSG);
	}

	@Override
	public void connectionEstablished(Connector connector, SocketChannel sc) {
		try {
			PacketChannel pChannel = new PacketChannel(sc,localPeer.getSelector(), new MultiMSGDecoder(),localPeer.getMessageHandler());    
			System.out.println("["+ connector + "] Connected: " + sc.socket().getInetAddress() );
			sendRequest(pChannel);
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
	    }
	}

	@Override
	public void connectionFailed(Connector connector, Exception cause) {System.err.println(" ######connectionFailed ###########");}

}
