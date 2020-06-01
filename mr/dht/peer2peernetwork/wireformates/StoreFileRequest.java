package mr.dht.peer2peernetwork.wireformates;

import mr.dht.peer2peernetwork.util.ByteStream;

public class StoreFileRequest extends Message{
	private String 	fileName;
	private int 	replicarionFactore;
	private int 	bufferSize;
	private byte[]  fileBytes;
	
	
	public StoreFileRequest() {
		super(STORE_FILE_REQUEST, STORE_FILE_REQUEST);
	}
	@Override
	public void initiate(byte[] byteStream) {
		setFileName		( unpackStringField( byteStream ) );
		setReplicarionFactore	( unpackIntField( byteStream ) );
		setBufferSize( unpackIntField( byteStream ) );
		fileBytes = readObjectBytes(byteStream);
	}

	@Override
	protected byte[] packMessageBody() {
		byte[] buffer = ByteStream.join(ByteStream.packString(getFileName()), ByteStream.intToByteArray(getReplicarionFactore()));
		buffer = ByteStream.join(buffer, ByteStream.intToByteArray(getBufferSize()));
		return  ByteStream.join(buffer,ByteStream.addPacketHeader(fileBytes));
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public String getMessageType() {
		return null;
	}
	public int getReplicarionFactore() {
		return replicarionFactore;
	}
	public void setReplicarionFactore(int rf) {
		this.replicarionFactore = rf;
	}
	public void setFileBytes(byte[] bytes){
		fileBytes = bytes;
	}

	public byte[] getFileBytes() {
		return fileBytes;
	}
	public int getBufferSize() {
		return bufferSize;
	}
	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}
}
