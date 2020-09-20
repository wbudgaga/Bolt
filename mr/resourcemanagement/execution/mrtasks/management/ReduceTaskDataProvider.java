package mr.resourcemanagement.execution.mrtasks.management;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import mr.dht.peer2peernetwork.nodes.Setting;
import mr.dht.peer2peernetwork.threadpool.Task;
import mr.dht.peer2peernetwork.util.UtilClass;
import mr.resourcemanagement.datatype.TaskData;
import mr.resourcemanagement.execution.mrtasks.ReduceTask;
import mr.resourcemanagement.io.DataSource;

public class ReduceTaskDataProvider extends Task{
	
	protected BlockingQueue<ConcurrentHashMap<String, ArrayList<Long>>> dataBufQueue	= new ArrayBlockingQueue<ConcurrentHashMap<String, ArrayList<Long>>>(5000);
	private ReduceTask  rTask;
	public final ConcurrentHashMap<String, ArrayList<Long>> POISON 				= new ConcurrentHashMap<String, ArrayList<Long>>();
	
	public ReduceTaskDataProvider(ReduceTask ownerReducer){
		this.rTask 									= ownerReducer;
	}
	
	private void  feedReducer(ConcurrentHashMap<String, ArrayList<Long>> buf ) {
		for (Map.Entry<String, ArrayList<Long>> e:buf.entrySet()){
			rTask.offer(e.getKey(), e.getValue());
		}
	}

	@Override
	public void execute() throws IOException, InterruptedException {
		while (true){
			ConcurrentHashMap<String, ArrayList<Long>> dataBuf 			= dataBufQueue.take();
			if (dataBuf == POISON)
				break;
			feedReducer(dataBuf);
		}
	}
	
	public void equeueBuffer(long srcPeerID, ConcurrentHashMap buf) throws InterruptedException {
		rTask.incNumOfReceivedBuffers(srcPeerID);
		dataBufQueue.put(buf);
	}
	
	public void stop() throws InterruptedException{
		dataBufQueue.put(POISON);
	}
}
