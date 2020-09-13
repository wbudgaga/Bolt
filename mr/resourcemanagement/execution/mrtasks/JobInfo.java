package mr.resourcemanagement.execution.mrtasks;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;

import mr.dht.peer2peernetwork.nodes.Setting;
import mr.dht.util.ClassLoader;
import mr.resourcemanagement.io.DataSource;


public class JobInfo<K1,V1,K2,V2> {
	public final static int MAP			= 1;
	public final static int REDUCE			= 2;
	public final static int STRING_TYPE		= 0;
	public final static int LONG_TYPE		= 1;
	public final static int DOUBLE_TYPE		= 2;
	
	private long 	jobID;
	private String 	mTaskClassName;
	private String 	rTaskClassName;
	private String 	outputName;
	private int 	numOfReducer;	
		
	public JobInfo(long jobID){
		this.jobID 				= jobID;
	}
			
	public long getJobID() {
		return jobID;
	}
	
	public void setMTaskClassName(String taskClassPath) {
		this.mTaskClassName 			= taskClassPath;
	}
	public String getMTaskClassName() {
		return mTaskClassName;
	}
	
	public void setRTaskClassName(String taskClassPath) {
		this.rTaskClassName = taskClassPath;
	}
	public String getRTaskClassName() {
		return rTaskClassName;
	}

	public String getOutputName() {
		return outputName;
	}
	public void setOutputName(String dataName) {
		this.outputName = dataName;
	}
	public int getNumOfReducer() {
		return numOfReducer;
	}

	public void setNumOfReducer(int numOfReducer) {
		this.numOfReducer = numOfReducer;
	}

}
