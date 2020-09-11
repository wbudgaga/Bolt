package mr.resourcemanagement.execution.mrtasks;

import java.io.FileNotFoundException;
import java.io.IOException;

import mr.dht.peer2peernetwork.nodes.Setting;
import mr.dht.peer2peernetwork.threadpool.ThreadPoolManager;
import mr.resourcemanagement.io.DataSource;
import mr.resourcemanagement.io.DataWriter;
import mr.resourcemanagement.io.TextFileWriter;

public class ReduceTaskInfo<K1,V1,K2,V2> extends TaskInfo<K1,V1,K2,V2>{
	private int numOfMaps;
	private String outputFullName;
	
	public ReduceTaskInfo(int numOfMaps){
		super(REDUCE);
		this.setNumOfMaps(numOfMaps);
	}
	
	public DataWriter getDataWriter(String output) throws IOException{
		return  new TextFileWriter("", output);//new TextFileWriter(getDataPath(),getDataFileName());
	}

	@Override
	public String getTaskTypeAsString() {
		return "Reduce";
	}

	public void setTaskID(long taskID) {
		super.setTaskID(taskID);
		this.outputFullName 		= Setting.DATA_DIR+"/d0/"+taskID;
	}

/*	@Override
	public DataSource<K1, V1> getDataReader() throws FileNotFoundException {
		return null;
	}
*/
	public int getNumOfMaps() {
		return numOfMaps;
	}

	public void setNumOfMaps(int numOfMaps) {
		this.numOfMaps 			= numOfMaps;
	}

	public String getOutputFullName() {
		return outputFullName;
	}

	@Override
	public DataSource<K1, V1> getDataReader(ThreadPoolManager ioThreadPool)
			throws FileNotFoundException, InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}
}
