package mr.dht.peer2peernetwork.nodes;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.rmi.CORBA.Util;

import mr.dht.peer2peernetwork.datastructure.JobDescriptor;
import mr.dht.peer2peernetwork.threadpool.Task;
import mr.dht.peer2peernetwork.util.UtilClass;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.dht.peer2peernetwork.wireformates.PeerInfo;
import mr.dht.peer2peernetwork.wireformates.StartMapTask;
import mr.dht.peer2peernetwork.wireformates.StartReduceTask;

public class JobTasksSubmitter  extends Task{
	private final JobSubmitter	jobSubmitter;
	private JobDescriptor		job;
	private BlockingQueue<Long>  chunksIDs	= new ArrayBlockingQueue<Long>(1000);
	private volatile int 		numOfChunks=0;
	
	public JobTasksSubmitter(JobSubmitter jobSubmitter) {
		this.jobSubmitter = jobSubmitter;
		
	}
	public void setJob(JobDescriptor job){
		this.job = job;
		numOfChunks=0;
	}
	
	protected void putChunkID(long chunkID) throws InterruptedException{
		chunksIDs.put(chunkID);
	}
	public void submitMapTasks(long key) throws IOException{		
		StartMapTask msg = new StartMapTask();
		PeerInfo taskOwner =new PeerInfo();
		taskOwner.setPeer(jobSubmitter.getNodeData());
		msg.setTaskOwner(taskOwner);
		msg.setJobID(job.getJobID());
		msg.setTaskID(key);
		msg.setNumOfReducers(job.getNumOfReducers());
		msg.setTaskClassName(job.getJobMapClassName());
		msg.setOutputName(job.getJobOutputName());
		jobSubmitter.sendDataToPeer(key, msg);
		++numOfChunks;
	}
	
	public void submitReduceTask(int idx) throws IOException{	
		long key = UtilClass.GetReduceKey(job.getJobOutputName(), idx);
		StartReduceTask msg = new StartReduceTask();
		PeerInfo taskOwner =new PeerInfo();
		taskOwner.setPeer(jobSubmitter.getNodeData());
		msg.setTaskOwner(taskOwner);
		msg.setJobID(job.getJobID());
		msg.setTaskID(key);
		msg.setNumberOfMappers(numOfChunks);
		msg.setTaskClassName(job.getJobReduceClassName());
		msg.setReducerOutputName(job.getJobOutputName());
		jobSubmitter.sendDataToPeer(key, msg);
	}
	
	@Override
	public void execute() throws IOException {//OK
		try {
			while(true){
				Long chunkID = chunksIDs.take();
				if (chunkID.equals(JobSubmitter.POISON))
					break;
				submitMapTasks(chunkID);
			}
			System.out.println("########submit reduce tasks####################"+job.getNumOfReducers());
			for (int i=1;i<=job.getNumOfReducers();++i){
				submitReduceTask(i);
			}
			jobSubmitter.jobSubmitted();
		} catch (InterruptedException e) {
				e.printStackTrace();
		}			
	}
}	