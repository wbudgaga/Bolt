package mr.resourcemanagement.execution.mrtasks.tasks;

import java.math.BigInteger;

import mr.resourcemanagement.execution.mrtasks.MapTask;

public class Job1Map extends MapTask<Long, String, String, Long>{
	@Override
	public boolean map(Long key, String data) {
		String[] words 				= data.split(" ");
		if(words.length<2 || words.length>60)
			return true;
		
		for (String word:words){
			try {
				output(word,1l);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	@Override
	public boolean postMap() {
		return true;
	}
}
