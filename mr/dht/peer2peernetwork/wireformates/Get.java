package mr.dht.peer2peernetwork.wireformates;

import java.io.IOException;

import mr.dht.peer2peernetwork.nodes.PeerData;
import mr.dht.peer2peernetwork.util.ByteStream;

//this class represents a RegisterRequest message. It is used to pack and unpack the RegisterRequest message by using the methods in superclass
//the method handle() calls the method handle() in receiver to handle the message
public class Get extends PeerInfo{
	private long hashedKey 			= -1; //-1 means all
	
	public Get() {
		super(GET, GET);
	}
	
	public Get(int messageID, int handlerID) {
		super(messageID,handlerID);
	}

	public void initiate(byte[] byteStream) {
		super.initiate(byteStream);
		setHashedKey(unpackLongField( byteStream ));
	}
	@Override
	protected byte[] packMessageBody() {
		return ByteStream.join(super.packMessageBody(), ByteStream.longToByteArray(getHashedKey()));
	}

	public String getMessageType() {
		return "GET_DATASET_METADATA";
	}
	public long getHashedKey() {
		return hashedKey;
	}
	public void setHashedKey(long hashedKey) {
		this.hashedKey = hashedKey;
	}
}
