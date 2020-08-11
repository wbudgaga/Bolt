package mr.dht.peer2peernetwork.handlers.mr_handlers;

import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.handlers.MessageHandler;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.dht.peer2peernetwork.wireformates.PeerInfo;

public class PeerInfoHandler extends MessageHandler{

	@Override
	public int getHandlerID() {
		return Message.PEER_INFO;
	}

	@Override
	public void handle(PacketChannel pc, Message msg1) {
		PeerInfo msg 				= (PeerInfo) msg1;
		System.out.println(msg.getMessageID() + "  " + msg.getHandlerID() + "  " +  msg.getPeer().getPeerID() + "  " + msg.getPeer().getNickName() + "  " + msg.getPeer().getHost() + "  " + msg.getPeer().getPortNum() + "  ");	
	}
}
