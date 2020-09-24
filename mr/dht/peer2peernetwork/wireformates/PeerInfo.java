package mr.dht.peer2peernetwork.wireformates;

import mr.dht.peer2peernetwork.nodes.PeerData;
import mr.dht.peer2peernetwork.util.ByteStream;

public class PeerInfo extends Message{
	private PeerData peer;
	
	public PeerInfo(int messageID, int handlerID) {
		super(messageID, handlerID);
	}
	public PeerInfo() {
		super(PEER_INFO, PEER_INFO);
	}
	
	private void unpackMessage(byte[] byteStream){
		peer 			= new PeerData();
		peer.setPeerID(unpackLongField(byteStream));
		peer.setNickName(unpackStringField(byteStream));
		peer.setHost(unpackStringField(byteStream));
		peer.setPortNum(unpackIntField(byteStream));
	}

	@Override
	public void initiate(byte[] byteStream) {
		if (byteStream.length>4)
			unpackMessage(byteStream);
	}

	private byte[] packPeerID(){
		return ByteStream.longToByteArray(peer.getPeerID());
	}

	private byte[] packNickName(){
		return ByteStream.packString(peer.getNickName());
	}

	private byte[] packHost(){
		return ByteStream.packString(peer.getHost());
	}
	
	private byte[] packPortNumber(){
		return ByteStream.intToByteArray(peer.getPortNum());
	}

	@Override
	protected byte[] packMessageBody() {
		if (peer == null)
			return null;
		byte[] bytes= ByteStream.join ( packPeerID(),packNickName());
		bytes			= ByteStream.join (bytes,packHost());
		return ByteStream.join(bytes, packPortNumber());
	}

	@Override
	public String getMessageType() {
		return null;
	}
	
	public PeerData getPeer() {
		return peer;
	}

	public void setPeer(PeerData peer) {
		if (peer == null){
			this.peer 	= new PeerData();
			this.peer.setPeerID(-1);
			this.peer.setHost("");
			this.peer.setNickName("");
		}else
			this.peer = peer;
	}
}
