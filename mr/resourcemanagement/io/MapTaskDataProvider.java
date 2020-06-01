package mr.resourcemanagement.io;

import java.io.IOException;

import mr.dht.peer2peernetwork.nodes.Setting;
import mr.dht.peer2peernetwork.threadpool.Task;
import mr.dht.peer2peernetwork.util.UtilClass;
import mr.resourcemanagement.datatype.TaskData;
import mr.resourcemanagement.io.DataSource;

public class MapTaskDataProvider extends Task{
	private boolean 	stillRunning = true;
	private DataSource	fileReader;
	
	public void setDataSource(DataSource fileReader){
		this.fileReader = fileReader;
	}
	@Override
	public void execute() throws IOException, InterruptedException {
		fileReader.passNextDataList();
		stopRunning();
	}
	public void returnTaskDataObject(TaskData td) {
		fileReader.returnTaskDataObject(td);
	}
	public boolean isStillRunning() {
		return stillRunning;
	}
	public void stopRunning() {
		this.stillRunning = false;
	}
}
