package mr.dht.peer2peernetwork.wireformates;

import mr.dht.peer2peernetwork.util.ByteStream;
public class QueryResult extends Message{
	private PeerInfo 	peer;
	private long 		queryKey;

	protected QueryResult(int msgID, int handlerID) {
		super(msgID, handlerID);
	}
	
	public QueryResult() {
		super(QUERY_RESULT, QUERY_RESULT);
	}
	
	private void unpackPeer(byte[] byteStream){
		peer = new PeerInfo();
		byte [] routerInfoObjectBytes = readObjectBytes(byteStream);
		peer.initiate(routerInfoObjectBytes);	
	}
	
	private void unpackMessage(byte[] byteStream){
		unpackPeer(byteStream);
		setQueryKey(unpackLongField(byteStream));
	}

	@Override
	public void initiate(byte[] byteStream) {
		unpackMessage(byteStream);
	}
	@Override
	protected byte[] packMessageBody() {
		byte[] bytes= ByteStream.join (packPeer(),packQueryKey());
		return bytes;
	}
	
	private byte[] packPeer(){
		byte[] routerBytes 		= peer.packMessage();
		return ByteStream.addPacketHeader(routerBytes);
	}
	
	
	private byte[] packQueryKey(){
		return ByteStream.longToByteArray(getQueryKey());
	}

	@Override
	public String getMessageType() {
		return "Lookup";
	}

	public PeerInfo getPeer() {
		return peer;
	}

	public void setPeer(PeerInfo peer) {
		this.peer = peer;
	}

	public long getQueryKey() {
		return queryKey;
	}

	public void setQueryKey(long queryKey) {
		this.queryKey = queryKey;
	}
}
