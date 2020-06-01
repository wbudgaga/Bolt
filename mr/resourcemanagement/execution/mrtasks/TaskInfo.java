package mr.resourcemanagement.execution.mrtasks;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;

import mr.dht.peer2peernetwork.nodes.Setting;
import mr.dht.peer2peernetwork.threadpool.ThreadPoolManager;
import mr.dht.util.ClassLoader;
import mr.resourcemanagement.io.DataSource;

public abstract class TaskInfo<K1,V1,K2,V2> {
	public final static int  	MAP				= 1;
	public final static int  	REDUCE			= 0;
	public final static int 	STRING_TYPE		= 0;
	public final static int 	LONG_TYPE		= 1;
	public final static int 	DOUBLE_TYPE		= 2;
	
	private int 				taskType;
	private long 				jobID;
	private long 				taskID;
	
	protected MRTask<K1,V1,K2,V2>	mrTask;
		
	public TaskInfo(int taskType){
		this.taskType = taskType;
	}
		
	public int getTaskType() {
		return taskType;
	}
		
	public long getJobID() {
		return jobID;
	}
	public void setJobID(long jobID) {
		this.jobID = jobID;
	}
	public long getTaskID() {
		return taskID;
	}
	public void setTaskID(long taskID) {
		this.taskID = taskID;
	}
	public MRTask<K1,V1,K2,V2> createTask(String taskClassName) throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException{
		Class<MRTask<K1,V1,K2,V2>> taskClass =  ClassLoader.loadTask(taskClassName,Setting.TASK_PACKAGE + taskClassName);
		mrTask = (MRTask<K1,V1,K2,V2>) taskClass.newInstance();
		mrTask.setTaskInfo(this);
		return mrTask;
	}

	public abstract DataSource<K1,V1> 	getDataReader(ThreadPoolManager ioThreadPool) throws FileNotFoundException, InterruptedException;
	public abstract String 				getTaskTypeAsString();
}
