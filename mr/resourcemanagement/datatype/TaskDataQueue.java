package mr.resourcemanagement.datatype;

import java.util.LinkedList;
import java.util.Queue;


public class TaskDataQueue<K,V> {
	protected Queue<TaskData<K,V>> 	pendingData 	= new LinkedList<TaskData<K,V>>();
	
	public boolean enqueueInstance(TaskData<K,V> instance){
		synchronized (pendingData){
			return pendingData.offer(instance);
		}
	}
	
	public TaskData<K,V> dequeueInstance( ){
		synchronized (pendingData){
			return pendingData.poll();
		}
	}
	public int getQSize(){
		return pendingData.size();
	}

	public boolean isTherePendingData(){
		synchronized (pendingData){
			return getQSize() > 0;
		}
	}
}