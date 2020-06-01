package mr.resourcemanagement.execution.mrtasks;

import java.io.FileNotFoundException;
import java.io.IOException;

import mr.dht.peer2peernetwork.nodes.Setting;
import mr.dht.peer2peernetwork.util.UtilClass;
import mr.resourcemanagement.datatype.TaskData;
import mr.resourcemanagement.execution.mrtasks.management.MapTaskDataProvider;
import mr.resourcemanagement.io.DataSource;

public abstract class MapTask<K1,V1,K2,V2> extends MRTask<K1,V1,K2,V2>{
	protected MapTaskInfo<K1,V1,K2,V2>			mapTaskInfo;
	
	//called by map()  function implemented by the user
	public void output(K2 key, V2 data) throws InterruptedException{
		jobTasksManager.output(key, data);
	}
	
	@Override
	public void setTaskInfo(TaskInfo<K1,V1,K2,V2> taskInfo) {
		this.mapTaskInfo = (MapTaskInfo<K1, V1, K2, V2>) taskInfo;
	}
	
	@Override
	public TaskInfo<K1, V1, K2, V2> getTaskInfo() {
		return mapTaskInfo;
	}
	public MapTaskDataProvider createDataProvider() throws FileNotFoundException, InterruptedException{
		MapTaskDataProvider mapTaskDataProvider = new MapTaskDataProvider();
		mapTaskDataProvider.setDataSource(mapTaskInfo.getDataReader(jobTasksManager.getIOThreadPool()));
		jobTasksManager.addToControlThreadPool(mapTaskDataProvider);
		return mapTaskDataProvider;
	}

	@Override
	public boolean runPreTask(){	
		try {
			mapTaskDataProvider = createDataProvider();
			if (mapTaskDataProvider!=null)
				return preMap();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean runTask() {
		TaskData<K1, V1> dataObj;
		K1 dataID;
		V1 data;
		while (isStillRunning()){
			try {
				dataObj = pendingQueue.take();
				if (dataObj==DataSource.POISON)
					break;

				dataID = dataObj.getDataID();
				data   = dataObj.getData();
				mapTaskDataProvider.returnTaskDataObject(dataObj);
				map(dataID, data);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	@Override
	public boolean runPostTask() {
		try {
			finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return postMap();
	}

	public boolean finish() throws Exception {	
		jobTasksManager.finishedMapTask(mapTaskInfo.getTaskID());
		return true;
	}
	
//	==================================================================	
	public boolean preMap() throws FileNotFoundException{
		return true;
	}
	
	public abstract boolean map(K1 key, V1 data);
	
	public boolean postMap(){
		return true;
	}
//==================================================================
}
