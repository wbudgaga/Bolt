package mr.resourcemanagement.execution.mrtasks;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import mr.dht.peer2peernetwork.threadpool.Task;
import mr.resourcemanagement.datatype.TaskData;
import mr.resourcemanagement.execution.mrtasks.management.JobTasksManager;
import mr.resourcemanagement.execution.mrtasks.management.MapTaskDataProvider;

public abstract class MRTask<K1,V1,K2,V2> extends Task{
	protected JobTasksManager 			jobTasksManager;
	protected MapTaskDataProvider 		mapTaskDataProvider;
	protected BlockingQueue<TaskData<K1, V1>> pendingQueue= new LinkedBlockingQueue<TaskData<K1, V1>>();
	protected boolean 					stillRunning = true;
		
	@Override
	public void execute() throws IOException, InterruptedException {
		runPreTask();
		runTask();
		runPostTask();
	}

	public void setTasksManager(JobTasksManager jobTasksManager){
		this.jobTasksManager = jobTasksManager;
	}
	
	public boolean isStillRunning() {
		return stillRunning;
	}

	public void stopRunning() {
		this.stillRunning = false;
	}
	
	// Abstract methods
	public abstract boolean runPreTask();
	public abstract boolean runTask();
	public abstract boolean runPostTask();

	public abstract TaskInfo<K1,V1,K2,V2> getTaskInfo();
	public abstract void setTaskInfo(TaskInfo<K1,V1,K2,V2> taskInfo);

}
