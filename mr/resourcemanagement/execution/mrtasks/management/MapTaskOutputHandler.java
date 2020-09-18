package mr.resourcemanagement.execution.mrtasks.management;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import mr.dht.peer2peernetwork.nodes.Setting;
import mr.dht.peer2peernetwork.threadpool.Task;
import mr.resourcemanagement.datatype.ReducerBuffer;

public class MapTaskOutputHandler<K1,V1,K2,V2> extends Task{
	private final int BUFFER_SIZE								= Setting.SENDBUFF_SIZE ;	
	private ConcurrentHashMap<Integer, ReducerBuffer<K2,V2>> currBuffers			= new ConcurrentHashMap<Integer, ReducerBuffer<K2,V2>>();
	public 	ConcurrentHashMap<Integer, BlockingQueue<ReducerBuffer<K2,V2>>>	extraBuffers	= new ConcurrentHashMap<Integer, BlockingQueue<ReducerBuffer<K2,V2>>>();
	
	//private ConcurrentHashMap<Integer, ReducerBuffer<K2,V2>>	reducerBuffersExtra= new ConcurrentHashMap<Integer, ReducerBuffer<K2,V2>>();
	private volatile boolean stillRunning							= true;
	private DataRouter	dataRouter							= null;
	private BlockingQueue<ReducerBuffer<K2,V2>>  readyBuffers= new ArrayBlockingQueue<ReducerBuffer<K2,V2>>(Setting.NUM_MAPBUFFERS);
	public static final ReducerBuffer POISON 						= new ReducerBuffer(-1);
	
	private final Object REDUCE_LOCK							= new Object(); 			
	private volatile int idx 								= 0;
	
	public MapTaskOutputHandler(DataRouter dataRouter) throws NoSuchAlgorithmException, FileNotFoundException, InstantiationException, IllegalAccessException, MalformedURLException, ClassNotFoundException, InterruptedException{
		this.dataRouter 								= dataRouter;
		createReducersBuffers(dataRouter.getNumOfReducer());
	}
	
	private void createReducersBuffers(int numOfReducers) throws InterruptedException{//OK
		for(int i = 0; i < numOfReducers; ++i){
			 BlockingQueue<ReducerBuffer<K2,V2>> reducerQueue 			= new ArrayBlockingQueue<ReducerBuffer<K2,V2>>(Setting.NUM_MAPBUFFERS);	 
			 for (int j = 0; j < Setting.NUM_MAPBUFFERS; ++j){
				 reducerQueue.offer(new ReducerBuffer<K2, V2>(i));
			 }
			extraBuffers.put(i, reducerQueue);
			currBuffers.put(i, reducerQueue.take());
		}	
	}
	
	private ReducerBuffer<K2,V2> nextBuffer(int reducerID) throws InterruptedException{
		BlockingQueue<ReducerBuffer<K2,V2>> buffersQ 					= extraBuffers.get(reducerID);
		return buffersQ.take();
	}
		
	public void returnBuffer(int reducerID, ReducerBuffer<K2,V2> rBuffer) throws InterruptedException{
		BlockingQueue<ReducerBuffer<K2,V2>> buffersQ 					= extraBuffers.get(reducerID);
		buffersQ.offer(rBuffer);
	}

	public boolean output(K2 key, V2 value) throws InterruptedException{//OK
		synchronized (REDUCE_LOCK){
			int reducerID 								= dataRouter.getReducerID(key, value);
			ReducerBuffer<K2, V2> curReducerBuf= currBuffers.get(reducerID);
//			int oldSize = curReducerBuf.getCounter();
			int addedBytes = 1;//curReducerBuf.add(key, value);
			if (addedBytes > BUFFER_SIZE){
/*				if (addedBytes> Setting.RECEIVEBUFF_SIZE)
					System.out.println(oldSize+"##############################   "+addedBytes);
*/				currBuffers.put(reducerID, nextBuffer(reducerID));
				readyBuffers.put(curReducerBuf);
			}
		}
		return true;
	}
	
	@Override
	public void execute() throws IOException {//OK
		while(true){
			try {
				ReducerBuffer reducerBuffer 					= readyBuffers.take();
				if (reducerBuffer == POISON)
					break;
				pushBuffer(reducerBuffer);
				reducerBuffer.clear();
				returnBuffer(reducerBuffer.getReducerIDX(), reducerBuffer);				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}			
		}
		dataRouter.stopAfterFinish(); // just for test
		//########flush();
		stillRunning 									= false;
	}
	
	//thread safe because called by only mainloop
	private void flush(){//OK
		for(int i=0;i<dataRouter.getNumOfReducer();++i){//OK
			ReducerBuffer<K2, V2> rBuffer = currBuffers.get(i);
			if (rBuffer.size()>0)
				pushBuffer(rBuffer);
		}
		dataRouter.stopAfterFinish();
	}
	//thread safe because called by only mainloop
	public boolean pushBuffer(ReducerBuffer<K2,V2> reducerBuffer){//OK
		int reducerID = reducerBuffer.getReducerIDX();
		try {
			dataRouter.pushBuffer(reducerID, reducerBuffer);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}	
		return true;
	}
	
	public boolean isStillRunning() {//OK
		return stillRunning;
	}
	//called by JobTaskManager
	public void stopRunning() {//OK
		try {
			readyBuffers.put(POISON); //lead to exit of the main loop
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
