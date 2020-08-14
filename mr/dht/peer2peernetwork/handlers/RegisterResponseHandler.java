package mr.dht.peer2peernetwork.handlers;

import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.nodes.LNode;
import mr.dht.peer2peernetwork.nodes.Peer;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.dht.peer2peernetwork.wireformates.Response;

public class RegisterResponseHandler extends MessageHandler{
	public RegisterResponseHandler(){}
	
	public Peer getHandler(LNode peer){
		return (Peer) node;
	}
	
	@Override
	public void handle(PacketChannel pc, Message msg1) {
		Response msg 				= (Response) msg1;
		if (msg.getStatusCode() == Message.SUCCEESS)
			System.out.println("The peer has joined the DHT");
		else{
			System.out.println("The peer could not join the DHT. The reason:" + msg.getAdditionalInfo());
			node.exit();
		}
	}

	@Override
	public int getHandlerID() {
		return Message.REGISTER_RESPONSE;
	}
}
