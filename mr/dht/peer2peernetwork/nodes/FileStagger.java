package mr.dht.peer2peernetwork.nodes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import mr.dht.peer2peernetwork.threadpool.Task;
import mr.dht.peer2peernetwork.util.MurmurHash3;
import mr.dht.peer2peernetwork.util.UtilClass;
import mr.dht.peer2peernetwork.wireformates.FileMetaData;
import mr.dht.peer2peernetwork.wireformates.StoreFileRequest;

public class FileStagger extends Task{
	private FileChannel fChannel;
	private File srcFile;
	private String 	chunkNameFormat;
	private DatasetStagger datasetStagger;
	private DataStagger dataStagger;
	public  static int SPLIT_PART_SIZE 	= Setting.RECEIVEBUFF_SIZE - 10 * Setting.KILO;
	private ByteBuffer buffer 		= ByteBuffer.allocateDirect(Setting.CHUNK_SIZE);
	private FileMetaData fileMetaData 	= new FileMetaData();
	
	public FileStagger(DatasetStagger datasetStagger){
		this.datasetStagger 		= datasetStagger;
		dataStagger			= datasetStagger.getDataStagger();
		fileMetaData.setChunkSize(Setting.CHUNK_SIZE);
		fileMetaData.setReplicationFactor(Setting.REPLICATION_FACTOR);
	}
	
	public void setFile(File srcFile){
		this.srcFile 			= srcFile;
		String fName 			= srcFile.getName();
		fileMetaData.setFileName(fName);
		fileMetaData.setFileSize(srcFile.length());
		fileMetaData.setFileHashedKey(UtilClass.hashMKey(fName));
	}
	
	public void setChunkNameFormat(String chunkNameFormat){
		this.chunkNameFormat 		= chunkNameFormat;
	}
	
	private void sendChunkPart(Long hashedKey, int bufSize) throws InterruptedException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException{
		StoreFileRequest sfr 		= dataStagger.getChunkMSG();		
		byte[] chunkBytes 		= sfr.getFileBytes();
		sfr.setBufferSize(bufSize);
		buffer.get(chunkBytes,0,bufSize);
		sfr.setFileName(String.valueOf(hashedKey));
		dataStagger.sendDataToPeer(hashedKey, sfr);
	}

	public void submitFileChunck(String chunkName, int splitSize) throws InterruptedException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException{
		int numOfChunkParts = splitSize / SPLIT_PART_SIZE;
		Long hashedKey 		= UtilClass.hashMKey(chunkName);
		for (int i=0; i<numOfChunkParts; ++i){
			sendChunkPart(hashedKey, SPLIT_PART_SIZE);
			//Thread.sleep(100);
		}
		if (buffer.hasRemaining()){
			sendChunkPart(hashedKey, buffer.remaining());
		}
		//dataStagger.pendChunk(hashedKey, DataStagger.POISION_MSG);
	}

	public void submitFile() throws IOException, InterruptedException, ClassNotFoundException, InstantiationException, IllegalAccessException{
		fChannel 		= new FileInputStream(srcFile).getChannel();
		long totalBytesRead = 0;
		int  chunkCount		= 0;
		int lastChunkSize		= (int) (fChannel.size() % Setting.CHUNK_SIZE);
		buffer.clear();
		while (totalBytesRead < fChannel.size()){
			int bytesRead = fChannel.read(buffer);     
			if (bytesRead == -1) {
				fChannel.close();
				System.out.println("ERROR: Reading file is failed");
				return;
			}
			totalBytesRead += bytesRead;
			buffer.flip();
			if (buffer.remaining() >= Setting.CHUNK_SIZE){
				submitFileChunck(String.format(chunkNameFormat,chunkCount++),Setting.CHUNK_SIZE);
				buffer.clear();
			}
		}	
		if (lastChunkSize>0){
			submitFileChunck(String.format(chunkNameFormat,chunkCount++),lastChunkSize);
		}
		
		fileMetaData.setNumOfChunks(chunkCount);
		dataStagger.sendMetaDataToPeer(fileMetaData.getFileHashedKey(), fileMetaData);
		//dataStagger.pendChunk(fileMetaData.getFileHashedKey(), DataStagger.POISION_MSG);
	}
	
	@Override
	public void execute() throws IOException, InterruptedException {
		System.out.println("Sending the file "+srcFile.getName()+" ...");
		try {
			submitFile();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			throw new IOException("Submitting a part of file's chunk is failed");
		}
		datasetStagger.returnObjToQueue(this);
	}
	
}
