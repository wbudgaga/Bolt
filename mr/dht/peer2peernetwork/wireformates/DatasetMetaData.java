package mr.dht.peer2peernetwork.wireformates;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import mr.dht.peer2peernetwork.nodes.Setting;
import mr.dht.peer2peernetwork.util.ByteStream;

public class DatasetMetaData extends Message{
	private long	dataSetHashKey;
	private int 	replicateNr		= 1;
	private ArrayList<String> fileNameList 	= new ArrayList<String>();
	private ArrayList<Long> fileSizeList 	= new ArrayList<Long>();
	
	
	public DatasetMetaData() {
		super(DATASET_META, DATASET_META);
	}
	
	@Override
	public void initiate(byte[] byteStream) {
		setDataSetHashKey( unpackLongField( byteStream ) );
		setReplicateNr( unpackIntField( byteStream ) );
		fileNameList 			= unpackStringArrayListField( byteStream ) ;
		fileSizeList 			= unpackLongArrayListField( byteStream ) ;
	}

	@Override
	protected byte[] packMessageBody() {
		byte[] buffer 		= ByteStream.join(ByteStream.longToByteArray(getDataSetHashKey()), ByteStream.intToByteArray(getReplicateNr()));
		buffer 			= ByteStream.join(buffer,ByteStream.packStringArrayList(fileNameList));
		return  ByteStream.join(buffer,ByteStream.packLongArrayList(fileSizeList));
	}

	@Override
	public String getMessageType() {
		return null;
	}

	public ArrayList<Long> getFileSizeList() {
		return fileSizeList;
	}
	public ArrayList<String> getFileNameList() {
		return fileNameList;
	}

	public void addFileData(String fileName, long size) {
		fileNameList.add(fileName);
		fileSizeList.add(size);
	}
	public long getDataSetHashKey() {
		return dataSetHashKey;
	}
	public void setDataSetHashKey(long dataSetHashKey) {
		this.dataSetHashKey = dataSetHashKey;
	}

	public int getReplicateNr() {
		return replicateNr;
	}
	
	public void setReplicateNr(int replicateNr) {
		this.replicateNr 	= replicateNr;
	}
	
	public static void main(String args[]) throws IOException {
		DatasetMetaData d 	= new DatasetMetaData();
		FileChannel fChannel 	= new FileInputStream("c:\\tmp\\304841012").getChannel();	
		ByteBuffer buffer 	= ByteBuffer.allocateDirect(Setting.CHUNK_SIZE);
		fChannel.read(buffer);
		buffer.flip();
		byte[] db= new byte[buffer.remaining()];
		buffer.get(db);
		DatasetMetaData d1 = new DatasetMetaData();
		d1.setReplicateNr(5);
		d1.initiate(db);
		System.out.println(d1.getDataSetHashKey());
		System.out.println(d1.getFileNameList());
		System.out.println(d1.getFileSizeList());
		System.out.println(d1.getReplicateNr());
	}
}
