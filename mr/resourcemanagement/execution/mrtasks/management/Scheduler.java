package mr.resourcemanagement.execution.mrtasks.management;

import java.util.PriorityQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import mr.resourcemanagement.execution.mrtasks.MapTaskInfo;
import mr.resourcemanagement.execution.mrtasks.ReduceTaskInfo;
import mr.resourcemanagement.execution.mrtasks.TaskInfo;

public class Scheduler {
	private BlockingQueue<Long>  jobs				= new ArrayBlockingQueue<Long>(1000);
	private ConcurrentHashMap<String,TaskInfo> tasks 		= new ConcurrentHashMap<String,TaskInfo>();
	private ConcurrentHashMap<Long,PriorityQueue<String>> jobTasks	= new ConcurrentHashMap<Long,PriorityQueue<String>>();//<jobID,<jobTaskIDs>>
	
	private Long curJobID 						= null;
		
	public String getUTaskID(TaskInfo taskInfo){
		return taskInfo.getJobID() + "_" + taskInfo.getTaskType() + "_" + taskInfo.getTaskID();
	}

	public synchronized void offerTask(TaskInfo taskInfo){
		long jobID 						= taskInfo.getJobID();
		PriorityQueue<String> jTasks 				= jobTasks.get(jobID);
		if (jTasks == null){
			jTasks 						= new PriorityQueue<String>();
			jobTasks.put(jobID, jTasks);
			jobs.offer(jobID);
		}
		String jobTaskID 					= getUTaskID(taskInfo);
		jTasks.offer(jobTaskID);
		tasks.put(jobTaskID, taskInfo);	
	}
	
	private TaskInfo nextTask(){
		if (curJobID==null)
			return null;
		PriorityQueue<String> jTasks = jobTasks.get(curJobID);
		if (jTasks!=null){
			String uTaskID = jTasks.poll();
			if (uTaskID!=null)
				return tasks.get(uTaskID);
		}
		return null;
	}
	
	public synchronized TaskInfo poolTask(){
		TaskInfo taskInfo = nextTask();
		while (taskInfo == null && !jobs.isEmpty()){//in case the tasks are completed for a given job
			curJobID = jobs.poll();
			taskInfo = nextTask();
		} 
		return taskInfo;
	}
	
	public static void main(String[] arg){
		TaskInfo m1 = new MapTaskInfo<Integer, Integer, Integer, Integer>();
		TaskInfo r1 = new ReduceTaskInfo<Integer, Integer, Integer, Integer>(1);
		TaskInfo m2 = new MapTaskInfo<Integer, Integer, Integer, Integer>();
		TaskInfo r2 = new ReduceTaskInfo<Integer, Integer, Integer, Integer>(2);

		m1.setJobID(1);
		m1.setTaskID(4);
		r1.setJobID(1);
		r1.setTaskID(12);
		r2.setJobID(1);
		r2.setTaskID(13);

		Scheduler s = new Scheduler();
		TaskInfo m3 = new MapTaskInfo<Integer, Integer, Integer, Integer>();
		m3.setJobID(11);
		m3.setTaskID(4);
		s.offerTask(m3);
		s.offerTask(r1);
		s.offerTask(m1);
		s.offerTask(r2);
		TaskInfo t;
		while (true){
			 t = s.poolTask();
			 if (t==null)
				 break;
			System.out.println(t.getJobID()+"=="+t.getTaskID());
		}
	}
}
