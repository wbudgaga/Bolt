package mr.dht.peer2peernetwork.nodes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import mr.dht.peer2peernetwork.datastructure.JobDescriptor;
import mr.dht.peer2peernetwork.util.HashFunction;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.dht.peer2peernetwork.wireformates.MessageFactory;
import mr.dht.peer2peernetwork.wireformates.PeerInfo;
import mr.dht.peer2peernetwork.wireformates.TextNumTaskData;

public class ClientCMDThread extends Thread{
	private BufferedReader 	bufferedReader;
	private CMDLineInterface userCommand;
	
	public ClientCMDThread(CMDLineInterface userInterface){
		this.userCommand 				=  userInterface;
		bufferedReader 					= new BufferedReader(new InputStreamReader(System.in));
	}
	
	private void search(String command){
		String[] para  					= command.split(" ");
		if (para.length > 1)
			userCommand.lookup(Long.parseLong(para[1]), Message.QUERY_RESULT);
		else
			System.err.println("Invalid usageof search command!");
	}
	
	private JobDescriptor createJob(String jobName){
/*		HashFunction f;
		try {
			f = new HashFunction();
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Problem by hashing the job title");
			e.printStackTrace();
			return null;
		}
		long jobID = f.hash(jobName);
		System.out.println("Enter the number of reducers:");
		int numOfReducers = Integer.parseInt(readCommand());
		JobDescriptor job = new JobDescriptor();
		job.setJobID(jobID);
		job.setJobName(jobName);
		job.setNumOfReducers(numOfReducers);
		job.setJobInputDataDir(jobName);
		System.out.println("Enter the mapTask class name:");
		job.setJobMapClassName(readCommand());
		System.out.println("Enter the reduceTask class name:");
		job.setJobReduceClassName(readCommand());
		return job;
*/	
		return null;
	}
	
	private void jobSubmit(String command) {
		String[] para  = command.split(" ");
		JobDescriptor job;
		System.err.println("number of inputs:"+para.length );
		if (para.length == 2){
			System.err.println("Enter the name of text file you want load a job from:");
			try {
				job = JobDescriptor.loadfromFile(readCommand());
				
			} catch (NumberFormatException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				job = null;
			}
			
		}else{
			job = createJob(para[1]);
		}
		userCommand.submitJob(job);
	}
	
	private void submitJobs(String command)  {
/*		String[] para  = command.split(" ");
		if (para.length < 3){
			System.err.println("Enter the name of text file and start and end job index:");
			return;
		}
		System.out.println(" p0=" +para[0]+" p1=" +para[1]+" p2= "+para[2]+"   p3= "+ para[3]);
		int start = Integer.parseInt(para[2]);
		int end = Integer.parseInt(para[3]);
		((Client)userCommand).allJobSubmitted = false;
		String jobFile = para[1];
		for (int i = start; i<end;++i){
			JobDescriptor job;
			try {
				job = JobDescriptor.loadfromFile(jobFile, i, "out");
				userCommand.submitJob(job);
			} catch (NumberFormatException | IOException e) {
				e.printStackTrace();
			}
		}
		((Client)userCommand).allJobSubmitted = true;
*/	}
	
	private void submitnJobs(String command)  {
		String[] para  = command.split(" ");
		if (para.length < 1){
			System.err.println("Enter the number of jobs:");
			return;
		}
		int n = Integer.parseInt(para[1]);
		JobDescriptor[] job=null;
		try {
			job = JobDescriptor.createJobs(n);
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}

		for (int i = 0; i<n;++i){
			userCommand.submitJob(job[i]);
		}
		
	}

	public HashMap<String, Long[]> createTestBuf(){
		HashMap<String, Long[]> 	outputBuf = new HashMap<String, Long[]>();
		outputBuf.put("key1", new Long[]{12l,80l,461819041972l});
		outputBuf.put("key2", new Long[]{0l,-2l});
		outputBuf.put("walid", new Long[]{2l,4l, 28l,45l});
		return outputBuf;
	}

	private void dataSubmit(String command) {
/*		String[] para  = command.split(" ");
		String datasetName = para[1];
		String dataDir = para[2];
		userCommand.submitTestBuffer(outputBuf, jobID, numOfReducers,reducerIDX );		
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/		}

	private String readCommand(){
		try {
			return bufferedReader.readLine();
		} catch (IOException e) {}
		return "quit";
	}
	
	public void run(){
		String command;
		while (true){
			System.out.println("enter your command (random-p(1), lookup(2), submitJob(3), submitTaskData(4), submitJobs(5), submitnJobs(6),  quit(0)");
			command = readCommand();
			if (command == null)							continue;
			if(command.compareTo("random-p")==0	|| command.compareTo("1")==0)	{userCommand.findRandomPeer();	continue;}
			if(command.startsWith("lookup")	    || command.startsWith("2 ")	)	{search(command);				continue;}
			if(command.startsWith("submitJob")  || command.startsWith("3")	)	{jobSubmit(command);			continue;}
			if(command.startsWith("submitTaskData") || command.startsWith("4"))	{dataSubmit(command);			continue;}
			if(command.startsWith("submitJobs")  || command.startsWith("5")	)	{submitJobs(command);			continue;}
			if(command.startsWith("submitnJobs")  || command.startsWith("6"))	{submitnJobs(command);			continue;}
			if(command.compareTo("quit")==0	    || command.compareTo("0")==0)	 break;
			System.err.println("Invalid Command!");
		}
	}

}
