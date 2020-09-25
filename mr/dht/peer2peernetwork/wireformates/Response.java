package mr.dht.peer2peernetwork.wireformates;

import java.net.Socket;

import mr.dht.peer2peernetwork.handlers.MessageHandler;
import mr.dht.peer2peernetwork.util.ByteStream;

//it is a supper class of RegisterResponse and DegisterResponse 
public class Response extends Message{
	private byte 	statusCode;
	private String 	additionalInfo;
	
	public Response(int messageID, int handlerID) {
		super(messageID, handlerID);
	}

	private void unpackMessage(byte[] byteStream){
		setStatusCode(unpackByteField(byteStream));
		setAdditionalInfo(unpackStringField(byteStream));
	}

	@Override
	public void initiate(byte[] byteStream) {
		unpackMessage(byteStream);
	}

	private byte[] packAdditionalInfo(){
		return ByteStream.packString(getAdditionalInfo());
	}

	@Override
	public byte[] packMessageBody(){
		byte[] StatusCodeBytes 		= {getStatusCode()};
		return ByteStream.join (StatusCodeBytes, packAdditionalInfo());
	}

	public byte getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(byte statusCode) {
		this.statusCode 		= statusCode;
	}

	public String getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo 		= additionalInfo;
	}

	@Override
	public String getMessageType() {
		return null;
	}
}
