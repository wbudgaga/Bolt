package mr.dht.peer2peernetwork.threadpool;

import java.util.LinkedList;
import java.util.Queue;

// Starts and manages the work between workers that execute taks
public class ThreadPoolManager{
	private Worker[] 	workers;
	private Queue<Task> 	taskQueue 		= new LinkedList<Task>();
	private Queue<Worker> 	workerQueue 		= new LinkedList<Worker>();
	
	public ThreadPoolManager() {
		createWorkers(1);
	}
	
	public ThreadPoolManager(int numberOfThreads) {
		createWorkers(numberOfThreads);
	}
		
	public void createWorkers(int numberOfThreads){
		workers 		= new Worker[numberOfThreads];
		for (int i = 0; i < numberOfThreads; ++i) {
			workers[i] 	= new Worker(this, i);
		}
	}
	public synchronized boolean isIdle(){
		if (taskQueue.size() == 0 && workerQueue.size() == getSize())
			return true;
		return false;
	}
	
	public synchronized void addTask(Task task) {	
		taskQueue.offer(task);
		if (taskQueue.size() > 0) {
			assignTasks(); //Implement this method
		}
	}
	
	public int getSize(){
		return workers.length;
	}
	
	/** Called by worker threads when their Task has completed. */
	protected synchronized void workerFinished(Worker worker) {
		workerQueue.offer(worker);
		if (taskQueue.size() > 0) {
			assignTasks();
		}
	}
	
	private  void assignTasks(){
		if (taskQueue.size() > 0 && workerQueue.size()>0){
			Task task = taskQueue.poll();
			Worker workerThread = workerQueue.poll();
			workerThread.assignTask(task);
		}
	}
	
	public void start() {
		for (Worker workerThread : workers){
			workerThread.start();
		}
		
		for (Worker worker : workers) {
			workerQueue.offer(worker);
		}
	}
	
	public synchronized void stop() {
		for (Worker worker : workers) {
			worker.stop();
			
		}
	}	
}
