package mr.dht.peer2peernetwork.nodes;

import java.io.IOException;

import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.handlers.LocalMessageHandler;
import mr.dht.peer2peernetwork.handlers.MessageHandler;
import mr.dht.peer2peernetwork.wireformates.GetSuccessor;
import mr.dht.peer2peernetwork.wireformates.Lookup;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.dht.peer2peernetwork.wireformates.PeerInfo;
import mr.dht.peer2peernetwork.wireformates.Predecessor;
import mr.dht.peer2peernetwork.wireformates.QueryResult;
import mr.dht.peer2peernetwork.wireformates.Successor;

public class RemotePeerLocal extends RemotePeer{
	private LocalMessageHandler	messageHandler;
	
	public RemotePeerLocal(long id, String name, String host, int port, LocalMessageHandler	messageHandler) throws IOException {
		super(id, name, host, port, null);
		this.messageHandler 		= messageHandler;
	}

	public void sendMessage(Message msg) throws IOException{
		try {
			messageHandler.handle(null, msg);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
