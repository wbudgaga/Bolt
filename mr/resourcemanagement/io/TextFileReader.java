package mr.resourcemanagement.io;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import mr.dht.peer2peernetwork.nodes.Setting;
import mr.dht.peer2peernetwork.threadpool.Task;
import mr.dht.peer2peernetwork.threadpool.ThreadPoolManager;
import mr.resourcemanagement.datatype.TaskData;
import mr.resourcemanagement.execution.mrtasks.management.ResourceManager;

public class TextFileReader extends DataSource<Long, String>{
	private volatile boolean keepReading 					= true;
	private String chunkName;
	private int pos;
	private Long lineNum 							= 0l;
	private BlockingQueue<TaskData<Long, String>> 		dataQueue;
	private final BlockingQueue<TaskData<Long, String>> internalDataQueue 	= new ArrayBlockingQueue<TaskData<Long,String>>(Setting.INPUT_QUEUESIZE);
    
	public <K,V> TextFileReader(String chunkFullName) throws FileNotFoundException, InterruptedException{	
		chunkName 							= chunkFullName;
		initQueue(); 
	}

	private void initQueue() throws InterruptedException{
		for (int i = 0; i < Setting.INPUT_QUEUESIZE; ++i)
			internalDataQueue.offer(new TaskData<Long, String>(0l,""));
	}
	
	private TaskData getTaskDataObject(long id, String data) throws InterruptedException{
		TaskData taskData 						= internalDataQueue.take();
		taskData.setDataID(id);
		taskData.setData(data);
		return taskData;
	}
	private String getLine(ByteBuffer b, int s, int e){
		byte bt[] 							= new byte[e-s-1];
		int oldPos 							= b.position();
		b.position(s);
		b.get(bt);
		b.position(oldPos);
		return new String(bt);
	}

	private String getLine(ByteBuffer buf){
		char c;
		while(buf.hasRemaining() && (buf.get()!='\n'));
		if (pos<buf.position()){
			String line 						= getLine(buf, pos, buf.position());
			pos 							= buf.position() ;
			return line;
		}
		return null;
	}
	
	@Override
	public void passNextDataList() throws IOException, InterruptedException{
		String line;
		ByteBuffer buf 							= ByteBuffer.allocateDirect( Setting.CHUNK_SIZE );
		readchunk(chunkName, buf);
		while ((line= getLine(buf)) != null/*keepReading && lineNum<100000*/){//loop over one file
			dataQueue.offer(getTaskDataObject(lineNum++, line));
			//.out.println("###########################"+keepReading);
/*			if (line.length()>1000000)
				System.out.println(line.length()+" ##############################   "+lineNum+"  ...."+f+"   at "+Setting.DATA_DIR);*/
		}
		dataQueue.offer(POISON);
	}
	
	@Override
	public void setBuffer(BlockingQueue<TaskData<Long, String>>  buf) {
		this.dataQueue 							= buf;
	}

	@Override
	public void returnTaskDataObject(TaskData<Long, String> td) {
		internalDataQueue.offer(td);
	}
	
	public static void main(String[] args) throws IOException, InterruptedException{
		TextFileReader t = new TextFileReader("/s/chopin/b/grad/wbudgaga/noaa/1332_10.txt");
		ThreadPoolManager ioThreadPool 		= new ThreadPoolManager(2);
		t.setIOThreadPool(ioThreadPool);
		ioThreadPool.start();
		t.passNextDataList();
		ioThreadPool.stop();
		ioThreadPool=null;
		System.out.println("EEEEEEEEEEEEEEEEEEEEE");
	}
}
