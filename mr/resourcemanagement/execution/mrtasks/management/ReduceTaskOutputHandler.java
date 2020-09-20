package mr.resourcemanagement.execution.mrtasks.management;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import mr.dht.peer2peernetwork.nodes.Setting;
import mr.dht.peer2peernetwork.threadpool.Task;
import mr.resourcemanagement.datatype.ReducerBuffer;
import mr.resourcemanagement.datatype.TaskData;
import mr.resourcemanagement.datatype.TaskDataQueue;
import mr.resourcemanagement.execution.mrtasks.ReduceTaskInfo;
import mr.resourcemanagement.execution.mrtasks.TaskInfo;
import mr.resourcemanagement.io.DataSource;
import mr.resourcemanagement.io.DataWriter;

public class ReduceTaskOutputHandler<K1,V1,K2,V2> extends Task{
	private boolean stillRunning					= true;
	private DataWriter<K2,V2> fileWriter;
	private ReducerBuffer<K2, V2>   outputBuffer;
	//private BlockingQueue<ReducerBuffer<K2,V2>>  readyBuffers= new ArrayBlockingQueue<ReducerBuffer<K2,V2>>(Setting.NUM_REDUCEBUFFERS);
	private BlockingQueue<HashMap<K2,V2>>  readyBuffers		= new ArrayBlockingQueue<HashMap<K2,V2>>(Setting.NUM_REDUCEBUFFERS);
	//private	BlockingQueue<ReducerBuffer<K2,V2>>	extraBuffers= new ArrayBlockingQueue<ReducerBuffer<K2,V2>>(Setting.NUM_REDUCEBUFFERS);
	public static final HashMap POISON = new HashMap();
	
	public ReduceTaskOutputHandler(DataWriter dw) throws  InstantiationException, IllegalAccessException, ClassNotFoundException, IOException{
		setDataSource(dw);
		//createReducersBuffers();
	}
	
/*	private void createReducersBuffers(){ 
		 for (int j=0; j<Setting.NUM_REDUCEBUFFERS -1; ++j){
			 extraBuffers.offer(new ReducerBuffer<K2, V2>(0));
		 }
		 outputBuffer = new ReducerBuffer<K2, V2>(0);
	}
*/
	public void setDataSource(DataWriter<K2,V2> fileWriter){
		this.fileWriter = fileWriter;
	}

	public void output(HashMap<K2,V2> data) throws InterruptedException{
		readyBuffers.put(data);
	}
/*	public boolean output(K2 key, V2 value) throws InterruptedException{
		int addedBytes = outputBuffer.add(key, value);
		if (addedBytes > DataSource.BUFFER_SIZE){
			readyBuffers.put(outputBuffer);
			outputBuffer = extraBuffers.take();
		}
    	return true;
	}
*/	
	@Override
	public void execute() throws IOException {
		while(true){
			try {
				HashMap<K2,V2> reducerBuffer = readyBuffers.take();
				if (reducerBuffer==POISON)
					break;
				/*Steps should done (sorting the buffer, load storded data, merge & store both
				 * But for know we just add new buffer to reduce file
				 */
				fileWriter.write(reducerBuffer);
				//reducerBuffer.clear();
				//extraBuffers.offer(reducerBuffer);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}			
		}
		fileWriter.close();
	}
	
	public boolean isStillRunning() {
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
