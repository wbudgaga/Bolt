package mr.resourcemanagement.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import mr.dht.peer2peernetwork.nodes.Setting;
import mr.dht.peer2peernetwork.threadpool.Task;
import mr.dht.peer2peernetwork.threadpool.ThreadPoolManager;
import mr.dht.peer2peernetwork.threadpool.Worker;
import mr.dht.peer2peernetwork.util.UtilClass;
import mr.resourcemanagement.datatype.ReducerBuffer;
import mr.resourcemanagement.datatype.TaskData;
import mr.resourcemanagement.datatype.TaskDataQueue;

public abstract class DataSource <K,V> {
	protected long 				alreadyreadBytes=0; 
	public    static final TaskData POISON 		= new TaskData(0,"");
	
	//private final BlockingQueue<TaskData<Long, String>> internalDataQueue = new ArrayBlockingQueue<TaskData<Long,String>>(Setting.INPUT_QUEUESIZE);
	private ThreadPoolManager	ioThread;
	public abstract void passNextDataList() throws IOException, InterruptedException;
	public abstract void setBuffer(BlockingQueue<TaskData<K, V>> buf);
	public abstract void returnTaskDataObject(TaskData<K, V> td);
	
	public void setIOThreadPool(ThreadPoolManager ioThreadPool){
		ioThread = ioThreadPool;
	}
	protected void readLine(Task t){
		ioThread.addTask(t);
	}
	//reads the whole chunk's bytes and store them in buf in synchronize way
	protected void readchunk(String cn, ByteBuffer buf) throws InterruptedException{
		ChunkReader chunkReader = new ChunkReader();
		chunkReader.setChunkName(cn);
		chunkReader.addBuffer(buf);
		ioThread.addTask(chunkReader);
		chunkReader.take();
	}

	//helpClass to read from hard disk  
	private class ChunkReader extends Task{
		private FileChannel fChannel;
		private String		chunkName;
		ByteBuffer byteBuff;
		private final BlockingQueue<ByteBuffer> ByteBufferSynch = new ArrayBlockingQueue<ByteBuffer>(1);
		
		public void setChunkName(String cn){
			chunkName = cn;
		}
		public void addBuffer(ByteBuffer buf) throws InterruptedException{
			byteBuff = buf;
		}
		
		
		public ByteBuffer take() throws InterruptedException{
			return ByteBufferSynch.take();
		}
		@Override
		public void execute() throws IOException, InterruptedException {
			fChannel 			= new FileInputStream(chunkName).getChannel();
			//ByteBuffer byteBuff = take();
			byteBuff.clear();
			System.out.println("#################start#############");
			if (fChannel.read(byteBuff) != -1 ){
				byteBuff.flip();
			}
			fChannel.close();
			ByteBufferSynch.offer(byteBuff);
			System.out.println("#################end#############");
		}
	}

}
