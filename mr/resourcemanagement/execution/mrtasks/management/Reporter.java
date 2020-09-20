package mr.resourcemanagement.execution.mrtasks.management;

public class Reporter{
	private long numOfProcessedObs		= 0;
	private long numOfFailingObs		= 0;
	private long totalNumOfObs		= -1;
	
	public Reporter(long totalNumOfObs){
		this.totalNumOfObs 		= totalNumOfObs;
	}
	
	public void incProcessed(){
		numOfProcessedObs 		= numOfProcessedObs + 1;
	}
	
	public  void incFailed(){
		numOfFailingObs 		= numOfFailingObs + 1;
	}

	public  float progress(){
		return  (numOfFailingObs + numOfProcessedObs)/totalNumOfObs;
	}

}
