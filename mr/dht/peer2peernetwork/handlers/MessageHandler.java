package mr.dht.peer2peernetwork.handlers;
import java.io.IOException;
import java.nio.ByteBuffer;

import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.nodes.LNode;
import mr.dht.peer2peernetwork.nodes.Setting;
import mr.dht.peer2peernetwork.util.ByteStream;
import mr.dht.peer2peernetwork.wireformates.FinishedMapTaskNotify;
import mr.dht.peer2peernetwork.wireformates.Message;


public abstract  class MessageHandler {
	protected LNode node;
	
	public void setNode(LNode node){
		this.node = node;
	}
	public abstract int getHandlerID();
	public abstract void handle(PacketChannel pc, Message msg);	
	
	public static void sendMessage(PacketChannel pc, Message msg) {
		sendMessage(pc, ByteStream.addPacketHeader(msg.packMessage()));
	}
	private static void sendMessage(PacketChannel pc, byte[] bytesToSend) {
		pc.sendPacket(ByteBuffer.wrap(bytesToSend));
	}

}
