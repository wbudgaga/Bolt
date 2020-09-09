package mr.dht.peer2peernetwork.wireformates;

import java.net.Socket;
import java.util.ArrayList;

import mr.dht.peer2peernetwork.handlers.MessageHandler;
import mr.dht.peer2peernetwork.util.ByteStream;
import mr.dht.peer2peernetwork.util.UtilClass;

// it has the methods that are used by all message classes. It has the basic operations to convert between primitive types and byte strem
public abstract class Message implements MessageTypes{
	private int msgID;
	private int handlerID;
	private long msgUUID;
	private int 	currentIndex		= 16;
	
	public  Message(int msgID, int handlerID){
		this.msgID 			= msgID;
		this.handlerID 			= handlerID;
		msgUUID 			= UtilClass.getUUID();
	}
	
	public int getMessageID() {
		return msgID;
	}
	public int getHandlerID() {
		return handlerID;
	}
	public long getMsgUUID() {
		return msgUUID;
	}
	public void setMsgUUID(long msgID) {
		this.msgUUID 			= msgID;
	}

	public void setHandlerID(int handlerID) {
		this.handlerID 			= handlerID;
	}

	protected byte[] readNextBytes(byte[] byteStream,int length){
		byte [] bytes 			= ByteStream.getBytes(byteStream,currentIndex,length);
		currentIndex 			+= length;
		return bytes;
	}

	protected String unpackStringField(byte[] byteStream){
		byte[] stringBytes 		= readObjectBytes(byteStream);
		return ByteStream.byteArrayToString(stringBytes);
	}

	protected Long[] unpackLongArrayField(byte[] byteStream){
		int size 			= unpackIntField(byteStream);
		Long[] longArray 		= new Long[size];
		for (int i=0; i< size;++i){
			longArray[i] 		= unpackLongField(byteStream);
		}
		return longArray;
	}

	protected ArrayList<Long> unpackLongArrayListField(byte[] byteStream){
		int size 			= unpackIntField(byteStream);	
		ArrayList<Long> longArray 	= new ArrayList<Long>();
		for (int i=0; i< size;++i){
			longArray.add(unpackLongField(byteStream));
		}
		return longArray;
	}

	protected ArrayList<String> unpackStringArrayListField(byte[] byteStream){
		int size 			= unpackIntField(byteStream);	
		ArrayList<String> stringArray = new ArrayList<String>();
		for (int i=0; i< size;++i){
			stringArray.add(unpackStringField(byteStream));
		}
		return stringArray;
	}

	protected int unpackIntField(byte[] byteStream){
		byte[] intBytes 		= readNextBytes(byteStream,4);
		return ByteStream.byteArrayToInt(intBytes);
	}
	protected long unpackLongField(byte[] byteStream){
		byte[] longBytes 		= readNextBytes(byteStream,8);
		return ByteStream.byteArrayToLong(longBytes);
	}

	protected byte unpackByteField(byte[] byteStream){
		byte[] byteFiled 		= readNextBytes(byteStream,1);
		return byteFiled[0];
	}
	
	protected byte[] packMessageID(){
		return ByteStream.intToByteArray(getMessageID());
	}
	protected byte[] packMessageUUID(){
		return ByteStream.longToByteArray(getMsgUUID());
	}
	protected byte[] packHandlerID(){
		return ByteStream.intToByteArray(getHandlerID());
	}

	public static final int unpackMessageID(byte[]  byteStream){
		byte[] messageIdBytes = ByteStream.getBytes(byteStream,0,4);
		return ByteStream.byteArrayToInt(messageIdBytes);
	}

	public static final int unpackHandlerID(byte[]  byteStream){
		byte[] messageIdBytes = ByteStream.getBytes(byteStream,4,4);
		return ByteStream.byteArrayToInt(messageIdBytes);
	}

	public static final long unpackMessageUUID(byte[]  byteStream){
		byte[] messageIdBytes = ByteStream.getBytes(byteStream,8,8);
		return ByteStream.byteArrayToLong(messageIdBytes);
	}

	protected byte[] readObjectBytes(byte[] byteStream){
		byte[] objectLengthInBytes 	= readNextBytes(byteStream,4);
		int objectLength 			= ByteStream.byteArrayToInt(objectLengthInBytes);
		return readNextBytes(byteStream,objectLength);
	}

	public  byte[] packMessage(){
		byte[] msgHead = ByteStream.join(packMessageID(), packHandlerID());
		msgHead = ByteStream.join(msgHead, packMessageUUID());
		return ByteStream.join(msgHead, packMessageBody());
	}
	
	public void create(byte[]  byteStream){
		msgUUID = unpackMessageUUID(byteStream);
		currentIndex 	= 16; //Beginning of msg info. 8 bytes = msgID:int(4 bytes) + handlerID:int(4 bytes)
		initiate(byteStream);
	}
	
	public 		abstract void   initiate(byte[]  byteStream);
	protected	abstract byte[] packMessageBody();
	public 		abstract String getMessageType();
}
