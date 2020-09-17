package mr.dht.peer2peernetwork.wireformates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import mr.dht.peer2peernetwork.nodes.PeerData;
import mr.dht.peer2peernetwork.nodes.Setting;
import mr.dht.peer2peernetwork.util.ByteStream;


public class TextNumTaskData extends TaskData{
	protected HashMap<String, ArrayList<Long>> outputBuf 	= new HashMap<String, ArrayList<Long>>();
	
	protected TextNumTaskData(int msgID, int handlerID) {
		super(msgID, handlerID);
	}
	
	public TextNumTaskData() {
		super(TEXTNUM_TASKDATA, TEXTNUM_TASKDATA);
	}
	
/*	public void returnDataBuf(HashMap hashMap){
		this.dataBufQueue.offer(hashMap);
	}
*/
	public void setDataBuf(HashMap hashMap){
		this.outputBuf 					= hashMap;
	}
	
	public HashMap getDataBuf(){
		return outputBuf;
	}

	private void unpackKVHashmap(byte[] byteStream) throws InterruptedException{
		String  dataKey 				= unpackStringField(byteStream);
		ArrayList<Long> dataValue 			= unpackLongArrayListField(byteStream);
		outputBuf.put(dataKey, dataValue);
	}
	@Override
	public void initiate(byte[] byteStream) {
		super.initiate(byteStream);
		int size = unpackIntField(byteStream);
		outputBuf.clear();
		for (int i=0; i<size;++i){
			try {
				unpackKVHashmap(byteStream);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	private byte[] packKVHashmap(String k, ArrayList<Long> v){
		return ByteStream.join(ByteStream.packString(k), ByteStream.packLongArrayList(v));
	}
	
	protected byte[] packDataBuffer() {
		int size = outputBuf.size();
		byte[] bufferBytes = ByteStream.intToByteArray(size);
		for (Map.Entry<String,ArrayList<Long>> pairs: outputBuf.entrySet()){
			bufferBytes= ByteStream.join(bufferBytes, packKVHashmap(pairs.getKey(), pairs.getValue()));
		}
		return bufferBytes;
	}
	
	@Override
	protected byte[] packMessageBody() {
		setOutputName("");
		setTaskClassName("");
		byte[] bytes = ByteStream.join(super.packMessageBody(), packDataBuffer());
		return bytes;
	}
	
	@Override
	public String getMessageType() {
		return "TextNumTaskData";
	}
	
}
