package mr.dht.peer2peernetwork.handlers.connection;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import mr.communication.handlers.Connector;
import mr.communication.handlers.ConnectorListener;
import mr.communication.handlers.MultiMSGDecoder;
import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.handlers.MessageHandler;
import mr.dht.peer2peernetwork.nodes.Peer;
import mr.dht.peer2peernetwork.wireformates.RegisterRequest;

public class RegisteringHandler implements ConnectorListener {
	private Peer  localPeer;
	
	public  RegisteringHandler(Peer localPeer){
		this.localPeer 				= localPeer;
	}
	
	public void register(PacketChannel pc) throws IOException{
		RegisterRequest registerRequestMSG 	= new RegisterRequest();
		registerRequestMSG.setPeer(localPeer.getNodeData());
		MessageHandler.sendMessage(pc, registerRequestMSG);
	}

	@Override
	public void connectionEstablished(Connector connector, SocketChannel sc) {
		try {
			PacketChannel pChannel 		= new PacketChannel(sc,localPeer.getSelector(), new MultiMSGDecoder(),localPeer.getMessageHandler());    
			System.out.println("["+ connector + "] Connected: " + sc.socket().getInetAddress() );
			register(pChannel);
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
	    }
	}

	@Override
	public void connectionFailed(Connector connector, Exception cause) {System.err.println(" ######connectionFailed ###########");}
}
