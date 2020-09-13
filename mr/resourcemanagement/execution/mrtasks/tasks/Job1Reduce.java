package mr.resourcemanagement.execution.mrtasks.tasks;
import java.math.BigInteger;

import mr.resourcemanagement.execution.mrtasks.ReduceTask;
import mr.resourcemanagement.execution.mrtasks.TaskInfo;

public class Job1Reduce extends ReduceTask<String,Iterable<Long>, String, Long>{
	@Override
	public boolean reduce(String key,Iterable<Long> data) {
		Long sum 			= 0l;
		for (Long  count:data){
			sum 			= sum + count;
		}
		try {
			Long curSum = getOutputValue(key);
			if (curSum == null)
				curSum = 0l;
			output(key,curSum + sum);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return true;
	}
}
