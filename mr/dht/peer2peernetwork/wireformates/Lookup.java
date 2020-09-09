package mr.dht.peer2peernetwork.wireformates;

import mr.dht.peer2peernetwork.util.ByteStream;
public class Lookup extends Message{
	private PeerInfo 	sourcePeer;
	private int 		srcPeerHandlerID;
	private long 		queryKey;
	private int 		hops;

	protected Lookup(int msgID, int handlerID) {
		super(msgID, handlerID);
	}
	
	public Lookup() {
		super(LOOKUP, LOOKUP);
		setSrcPeerHandlerID(QUERY_RESULT);
	}

	public int incHop(){
		setHops(getHops()+1);
		return getHops();
	}
	
	private void unpackSourcePeer(byte[] byteStream){
		sourcePeer 			= new PeerInfo();
		byte [] routerInfoObjectBytes 	= readObjectBytes(byteStream);
		sourcePeer.initiate(routerInfoObjectBytes);	
	}
	
	protected void unpackMessage(byte[] byteStream){
		unpackSourcePeer(byteStream);
		setSrcPeerHandlerID(unpackIntField(byteStream));
		setQueryKey(unpackLongField(byteStream));
		setHops(unpackIntField(byteStream));
	}

	@Override
	public void initiate(byte[] byteStream) {
		unpackMessage(byteStream);
	}
	@Override
	protected byte[] packMessageBody() {
		byte[] bytes= ByteStream.join (packSourcePeer(),packSrcPeerHandlerID());
		bytes= ByteStream.join (bytes,packQueryKey());
		return ByteStream.join(bytes, packHops());
	}
	
	private byte[] packSourcePeer(){
		byte[] routerBytes 		= sourcePeer.packMessage();
		return ByteStream.addPacketHeader(routerBytes);
	}
	
	private byte[] packSrcPeerHandlerID(){
		return ByteStream.intToByteArray(getSrcPeerHandlerID());
	}	
	private byte[] packQueryKey(){
		return ByteStream.longToByteArray(getQueryKey());
	}

	private byte[] packHops(){
		return ByteStream.intToByteArray(getHops());
	}

	@Override
	public String getMessageType() {
		return "Lookup";
	}

	public PeerInfo getSourcePeer() {
		return sourcePeer;
	}

	public void setSourcePeer(PeerInfo sourcePeer) {
		this.sourcePeer 		= sourcePeer;
	}

	public long getQueryKey() {
		return queryKey;
	}

	public void setQueryKey(long queryKey) {
		this.queryKey 			= queryKey;
	}

	public int getHops() {
		return hops;
	}

	public void setHops(int hops) {
		this.hops 			= hops;
	}

	public int getSrcPeerHandlerID() {
		return srcPeerHandlerID;
	}

	public void setSrcPeerHandlerID(int srcPeerHandlerID) {
		this.srcPeerHandlerID 	= srcPeerHandlerID;
	}
}
