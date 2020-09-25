package mr.dht.peer2peernetwork.wireformates;

import java.net.Socket;

import mr.dht.peer2peernetwork.handlers.MessageHandler;
import mr.dht.peer2peernetwork.nodes.PeerData;

public class Request extends Message{
	private PeerInfo peer;
		
	public Request(int messageID, int handlerID) {
		super(messageID,handlerID);
	}
			
	private void unpackMessage(byte[] byteStream){
		peer 			= new PeerInfo();
		peer.initiate(byteStream);
		
	}
		
	protected byte[] packMessageBody(){
		return peer.packMessageBody();
	}
		
	@Override
	public void initiate(byte[] byteStream) {
		unpackMessage(byteStream);
	}

	@Override
	public String getMessageType() {
		return null;
	}

	public PeerInfo getPeer() {
		return peer;
	}

	public void setPeer(PeerInfo peer) {
		this.peer 		= peer;
	}
	
	public void setPeer(PeerData peer) {
		PeerInfo peerInfo 	= new PeerInfo();
		peerInfo.setPeer(peer);
		setPeer(peerInfo);
	}
}
