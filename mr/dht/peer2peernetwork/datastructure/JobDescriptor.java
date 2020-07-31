package mr.dht.peer2peernetwork.datastructure;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import mr.dht.peer2peernetwork.util.HashFunction;
import mr.dht.peer2peernetwork.util.UtilClass;

public class JobDescriptor {
	private long 	jobID;
	private String 	jobName;
	
	private int 	mapInKeyType;
	private int 	mapInValueType;
	private int 	mapOutKeyType;
	private int 	mapOutValueType;
	private int 	reduceOutKeyType;
	private int 	reduceOutValueType;
	
	private String 	jobInputData;
	private String 	outputName;
	
	private String 	jobMapClassName;
	private String 	jobReduceClassName;
	private int 	numOfReducers;
	
	public long getJobID() {
		return jobID;
	}
	public void setJobID(long jobID) {
		this.jobID 		= jobID;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName 		= jobName;
	}
	public int getMapInKeyType() {
		return mapInKeyType;
	}
	public void setMapInKeyType(int mapInKeyType) {
		this.mapInKeyType 	= mapInKeyType;
	}
	public int getMapInValueType() {
		return mapInValueType;
	}
	public void setMapInValueType(int mapInValueType) {
		this.mapInValueType = mapInValueType;
	}
	public int getMapOutKeyType() {
		return mapOutKeyType;
	}
	public void setMapOutKeyType(int mapOutKeyType) {
		this.mapOutKeyType = mapOutKeyType;
	}
	public int getMapOutValueType() {
		return mapOutValueType;
	}
	public void setMapOutValueType(int mapOutValueType) {
		this.mapOutValueType = mapOutValueType;
	}
	public int getReduceOutKeyType() {
		return reduceOutKeyType;
	}
	public void setReduceOutKeyType(int reduceOutKeyType) {
		this.reduceOutKeyType = reduceOutKeyType;
	}
	public int getReduceOutValueType() {
		return reduceOutValueType;
	}
	public void setReduceOutValueType(int reduceOutValueType) {
		this.reduceOutValueType = reduceOutValueType;
	}
	public String getJobInputData() {
		return jobInputData;
	}
	public void setJobInputData(String jobInputData) {
		this.jobInputData = jobInputData;
	}
	
	public String getJobOutputName() {
		return outputName;
	}
	public void setJobOutputName(String jobOutputName) {
		this.outputName = jobOutputName;
	}

	public int getNumOfReducers() {
		return numOfReducers; 
	}
	public void setNumOfReducers(int numOfReducers) {
		this.numOfReducers = numOfReducers;
	}
	public String getJobReduceClassName() {
		return jobReduceClassName;
	}
	public void setJobReduceClassName(String jobReduceClassName) {
		this.jobReduceClassName = jobReduceClassName;
	}
	public String getJobMapClassName() {
		return jobMapClassName;
	}
	public void setJobMapClassName(String jobMapClassName) {
		this.jobMapClassName = jobMapClassName;
	}
	
/*	public static JobDescriptor loadfromFile(String filename) throws NumberFormatException, IOException{
		System.out.println("=========>"+filename);
		HashFunction f;
		try {
			f = new HashFunction();
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Problem by hashing the job title");
			e.printStackTrace();
			return null;
		}

		FileReader fr = new FileReader(filename);
		BufferedReader buf = new BufferedReader(fr);
		
		JobDescriptor job = new JobDescriptor();
		String jobName = buf.readLine().split(":")[1];
		job.setJobID(f.hash(jobName));
		job.setJobName(jobName);
		job.setNumOfReducers(Integer.parseInt(buf.readLine().split(":")[1]));
		job.setJobMapClassName(buf.readLine().split(":")[1]);
		job.setJobReduceClassName(buf.readLine().split(":")[1]);
		job.setJobInputData(buf.readLine().split(":")[1]);
		job.setJobOutputDataDir(buf.readLine().split(":")[1]);
		return job;
	}	
*/	public static JobDescriptor loadfromFile(String filename) throws NumberFormatException, IOException{
		FileReader fr = new FileReader(filename);
		BufferedReader buf = new BufferedReader(fr);
		
		JobDescriptor job = new JobDescriptor();
		String jobName = buf.readLine().split(":")[1];
		job.setJobName(jobName);
		job.setJobID(UtilClass.hashMKey(jobName));
		job.setNumOfReducers(Integer.parseInt(buf.readLine().split(":")[1]));
		job.setJobMapClassName(buf.readLine().split(":")[1]);
		job.setJobReduceClassName(buf.readLine().split(":")[1]);
		job.setJobInputData(buf.readLine().split(":")[1]);
		job.setJobOutputName(buf.readLine().split(":")[1]);
		return job;
	}	

	public static JobDescriptor[] createJobs(int n) throws NumberFormatException, IOException{
		HashFunction f;
		try {
			f = new HashFunction();
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Problem by hashing the job title");
			e.printStackTrace();
			return null;
		}
		JobDescriptor[] job = new JobDescriptor[n];
		for (int i=1; i<=n;++i){
			job[i-1] = new JobDescriptor();
			String jobName = "job"+i;
			job[i-1].setJobID(f.hash(jobName));
			job[i-1].setJobName(jobName);
			job[i-1].setNumOfReducers(1);
			job[i-1].setJobMapClassName("Job1Map");
			job[i-1].setJobReduceClassName("Job1Reduce");
			job[i-1].setJobInputData("job1");
			//job[i-1].setJobOutputDataDir("job"+i+"/out");
		}
		return job;
	}	

	
	public static void main(String[] s) throws NumberFormatException, IOException{
		JobDescriptor j = JobDescriptor.loadfromFile("c:\\tmp\\data\\jobDesc.txt");
		System.out.println(j.getJobName());
		JobDescriptor job =j;
		System.out.println("Job: " +job.getJobName()+"\njobID: "+job.getJobID()+"\n number of reducres:"+job.getNumOfReducers()+" ===== "+job.getJobOutputName());	
	}
}
