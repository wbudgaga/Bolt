package mr.resourcemanagement.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import mr.dht.peer2peernetwork.nodes.Setting;
import mr.dht.peer2peernetwork.util.UtilClass;
import mr.resourcemanagement.datatype.ReducerBuffer;

public class TextFileWriter extends DataWriter<String, Long>{
	private BufferedWriter out;
	
	public <K,V> TextFileWriter(String dstDir, String fName) throws IOException{
		file 						= new File(dstDir,fName); 
		openFile();
	}
	
	public void openFile() throws IOException{
		FileWriter f 					= new FileWriter(file);
		out 						= new BufferedWriter(f, Setting.CHUNK_SIZE);
	}
	
	public void write(HashMap<String, Long> dataBuffer) throws IOException{
		//dataBuffer.resetCounter();
		//HashMap<String, ArrayList<Long>>  kVBuffer = dataBuffer.getOutputBuf();
		final Iterator<Entry<String, Long>> mapIter 	= dataBuffer.entrySet().iterator();
		while (mapIter.hasNext()) {
			final Entry<String, Long> dataItem 	= mapIter.next();
			//mapIter.remove();
			out.write(dataItem.getKey() + ":" + dataItem.getValue() + "\n");
		}
		++alreadyWritten;
	}
	public void close() throws IOException {
		out.flush();
		out.close();
	}
}
