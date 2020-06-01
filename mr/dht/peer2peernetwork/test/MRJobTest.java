package mr.dht.peer2peernetwork.test;

import java.math.BigInteger;

import mr.resourcemanagement.execution.mrtasks.MapTaskInfo;
import mr.resourcemanagement.execution.mrtasks.ReduceTaskInfo;
import mr.resourcemanagement.execution.mrtasks.TaskInfo;
import mr.resourcemanagement.execution.mrtasks.management.ResourceManager;

public class MRJobTest {
	public static void testSetReducer(ResourceManager rm) throws Exception{
		ReduceTaskInfo<BigInteger,String,String,BigInteger> reduceTask = new ReduceTaskInfo<BigInteger, String, String, BigInteger>();
		//reduceTask.setDataFileName("reducerResults.txt");
		reduceTask.setDataPath("c:\\tmp");
		reduceTask.setTaskID(24);
		reduceTask.setJobID(1);
		rm.process(reduceTask);

	}

	public static void main(String[] s) throws Exception{
		MapTaskInfo<BigInteger,String,String,BigInteger> mapTask = new MapTaskInfo<BigInteger, String, String, BigInteger>();
		
		mapTask.setDataPath("C:\\tmp\\mapReduceData\\job1");
		//mapTask.setDataPath("/tmp/MRlocalDir/jobs/job1");
		mapTask.setTaskID(23);
		mapTask.setJobID(1);
		mapTask.setNumOfReducer(4);
		mapTask.setTaskClassName("Job1Map");

		mapTask.setPartitionerClassName("mr.resourcemanagement.datapartitioning.ModPartitioner");
		ResourceManager rm = new ResourceManager(null, 1);
		rm.start();
		rm.process(mapTask);
		//testSetReducer(rm);
		rm.stop();
//		mrt.runPreTask();
	}
}
