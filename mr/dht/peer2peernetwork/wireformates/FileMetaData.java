package mr.dht.peer2peernetwork.wireformates;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import mr.dht.peer2peernetwork.nodes.Setting;
import mr.dht.peer2peernetwork.util.ByteStream;

public class FileMetaData extends Message{
	private long	fileHashedKey;
	private String	fileName;
	private long	fileSize;
	private byte 	replicateNr=1;
	private int 	replicationFactor;
	private int 	numOfChunks;
	private int 	chunkSize;
	
	public FileMetaData() {
		super(FILE_META, FILE_META);
	}
	
	@Override
	public void initiate(byte[] byteStream) {
		setFileHashedKey( unpackLongField( byteStream ) );
		setFileName(unpackStringField(byteStream));
		setFileSize( unpackLongField( byteStream ) );
		setReplicateNr( unpackByteField( byteStream ) );
		setReplicationFactor(unpackIntField(byteStream));
		setNumOfChunks(unpackIntField(byteStream));
		setChunkSize(unpackIntField(byteStream));
	}

	@Override
	protected byte[] packMessageBody() {
		byte[] buffer 		= ByteStream.join(ByteStream.longToByteArray(getFileHashedKey()), ByteStream.packString(getFileName()));
		buffer 			= ByteStream.join(buffer,ByteStream.longToByteArray(getFileSize()));
		buffer 			= ByteStream.join(buffer,new byte[]{getReplicateNr()});
		buffer 			= ByteStream.join(buffer,ByteStream.intToByteArray(getReplicationFactor()));
		buffer 			= ByteStream.join(buffer,ByteStream.intToByteArray(getNumOfChunks()));
		return  ByteStream.join(buffer,ByteStream.intToByteArray(getChunkSize()));
	}

	@Override
	public String getMessageType() {
		return null;
	}

	public static void main(String args[]) throws IOException {
		FileMetaData d 		= new FileMetaData();
		FileChannel fChannel 	= new FileInputStream("c:\\tmp\\3279056892").getChannel();	
		ByteBuffer buffer 	= ByteBuffer.allocateDirect(Setting.CHUNK_SIZE);
		fChannel.read(buffer);
		buffer.flip();
		byte[] db		= new byte[buffer.remaining()];
		buffer.get(db);
	
		FileMetaData d1 	= new FileMetaData();
		d1.setReplicateNr((byte)50);
		d1.initiate(db);
		System.out.println(d1.getFileHashedKey());
		System.out.println(d1.getFileName());
		System.out.println(d1.getReplicateNr());
		System.out.println(d1.getReplicationFactor());
		System.out.println(d1.getChunkSize());
		System.out.println(d1.getFileSize());
		System.out.println(d1.getNumOfChunks());
	}
	
	//#################### Get & Set methods ###################################
	public long getFileHashedKey() {
		return fileHashedKey;
	}
	public void setFileHashedKey(long fileHashedKey) {
		this.fileHashedKey = fileHashedKey;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public int getReplicationFactor() {
		return replicationFactor;
	}
	public void setReplicationFactor(int replicationFactor) {
		this.replicationFactor = replicationFactor;
	}
	public int getNumOfChunks() {
		return numOfChunks;
	}
	public void setNumOfChunks(int numOfChunks) {
		this.numOfChunks = numOfChunks;
	}
	public int getChunkSize() {
		return chunkSize;
	}
	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}

	public byte getReplicateNr() {
		return replicateNr;
	}
	public void setReplicateNr(byte replicateNr) {
		this.replicateNr = replicateNr;
	}
	public long getFileSize() {
		return fileSize;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
}
