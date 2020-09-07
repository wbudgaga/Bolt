package mr.dht.peer2peernetwork.wireformates;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

import mr.dht.peer2peernetwork.handlers.MessageHandler;
import mr.dht.peer2peernetwork.nodes.Setting;
import mr.dht.peer2peernetwork.util.ByteStream;
import mr.dht.peer2peernetwork.util.MergeTask;

public class GetResponse extends Message{
	private String 	fileName;
	private int 	status;
	private byte[]  fileBytes;
	
	public GetResponse() {
		super(GET_RESPONSE, GET_RESPONSE);
	}
	
	@Override
	public void initiate(byte[] byteStream) {
		setFileName(unpackStringField( byteStream ));
		setStatus (unpackIntField( byteStream ));
		fileBytes 				= readObjectBytes(byteStream);
	}
	
	private File createFile(File tmpFile){		
		File localDir 				= new File(Setting.LOCAL_DIR);
		if (!localDir.exists())
			localDir.mkdir();
		
		if(tmpFile.exists()){
			new MergeTask(tmpFile,ByteStream.byteArrayToString(getFileBytes()));
			return null;
		}
		try {
			tmpFile.createNewFile();
			return tmpFile;
		} catch (IOException e) {System.out.println("File ("+fileName+") could not be created!");}
    		return null;	
	}

	private void handleFile(){
		File dstDir				= createFile( new File(Setting.LOCAL_DIR,fileName));
		if(dstDir == null){
			return;
		}
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(dstDir,true);
			fileOutputStream.write(fileBytes);
			fileOutputStream.flush();
			fileOutputStream.close();
			System.out.println("The file: "+ dstDir.getName()+ " received!");
		} catch (FileNotFoundException e) {
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected byte[] packMessageBody() {
		byte[] buffer 				= ByteStream.join(ByteStream.packString(getFileName()), ByteStream.intToByteArray(getStatus()));
		return  ByteStream.join(buffer,ByteStream.addPacketHeader(fileBytes));
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

/*	@Override
	public void handle(Socket link, MessageHandler handler) {
		handleFile();
	}			
*/
	@Override
	public String getMessageType() {
		return null;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public void setFileBytes(byte[] bytes){
		fileBytes = bytes;
	}
	public byte[] getFileBytes() {
		return fileBytes;
	}
}
