package mr.dht.peer2peernetwork.benchmark;

import java.io.IOException;
import java.net.SocketException;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import mr.communication.handlers.Acceptor;
import mr.communication.handlers.MultiMSGDecoder;
import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.datastructure.JobDescriptor;
import mr.dht.peer2peernetwork.exceptions.InvalidFingerTableEntry;
import mr.dht.peer2peernetwork.handlers.ClientMessageHandler;
import mr.dht.peer2peernetwork.handlers.connection.RandomPeerRequestConnectingHandler;
import mr.dht.peer2peernetwork.nodes.CMDLineInterface;
import mr.dht.peer2peernetwork.nodes.Client;
import mr.dht.peer2peernetwork.nodes.LNode;
import mr.dht.peer2peernetwork.nodes.RemotePeer;
import mr.dht.peer2peernetwork.nodes.Setting;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.dht.peer2peernetwork.wireformates.PeerInfo;
import mr.dht.peer2peernetwork.wireformates.StartMapTask;
import mr.dht.peer2peernetwork.wireformates.StartReduceTask;
import mr.dht.peer2peernetwork.wireformates.TextNumTaskData;
import mr.resourcemanagement.datapartitioning.ModPartitioner;
import mr.resourcemanagement.execution.mrtasks.management.DataRouter;
import mr.resourcemanagement.execution.mrtasks.management.JobTasksManager;

public class RunJobs  extends Client{
	private int     numOfJobs;	
	private	String  jobFile;

	public RunJobs(String name, int port, String  jobFile, int     numOfJobs) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
		super(name, port);
		this.jobFile 				= jobFile;
		this.numOfJobs 				= numOfJobs; 
	}
			

	public void submitJobs(String jobFile, int numOfJobs)  {
		int start 				= 1;
		int end 				= numOfJobs+1;
		allJobSubmitted 			= false;
		for (int i = start; i<end;++i){
			JobDescriptor job;
			try {
				job 			= JobDescriptor.loadfromFile(jobFile, i, "out");
				System.out.println("Submit   job "+i);
				submitJob(job);
			} catch (NumberFormatException | IOException e) {
				e.printStackTrace();
			}
		}
		allJobSubmitted 			= true;
	}
	
	
//#############################################################################################
	//=============================================================		
	@Override
	public void submitJob(JobDescriptor job) {
		System.out.println("Job: " + job.getJobName() + "\njobID: " + job.getJobID() + "\n number of reducres:" + job.getNumOfReducers());	
		//TODO here should find a way find where the data is to start maptask locally

		int numOfMaps				= 77;		
		try {
			int taskID 			= 0;
			int numOfReducers 		= job.getNumOfReducers();
			long[] reducersKeys 		= DataRouter.getRountingKeys(new ModPartitioner<>(), numOfReducers, job.getJobID());
			for(long key:reducersKeys){
				System.out.println("Submitting reduce"+(taskID+1) +"  rkey="+key);
				submitReduceTasks(key, taskID++, numOfMaps, job);
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}	
		//submitting the <<map>> tasks
		for(int i=0; i< numOfMaps; ++i){
			long mapKey = mapIDs[i]-100;
			System.out.println("Submitting map"+i +"  rkey="+mapKey);
			submitMapTasks(mapKey, i, job);
		}
	}
	public void handleRandomPeer(RemotePeer rp) throws InvalidFingerTableEntry, IOException{
		remotePeer = rp;
		//System.out.println("random peer( ("+rp.getID()+") )has been received###########"+jobFile);
		submitJobs(jobFile, numOfJobs);
		System.out.println("jobs submitted ...");
	}

	//#############################################################################################
	/*
	 * Main Method
	 */
	public static void main(String args[]) throws InstantiationException, IllegalAccessException, IOException, ClassNotFoundException {
		RunJobs peer;
		if (args.length < 1) {
			System.err.println("Client Node:  Usage:");
			System.err.println("         java mr.dht.peer2peernetwork.nodes.Client portnum");
		    return;
		}
		try{
			int 	port 			= Integer.parseInt(args[0]);
			int     numOfJobs		= Integer.parseInt(args[1]);
			int     numOfReducers	= Integer.parseInt(args[2]);
			String  jobFile     = "/s/chopin/b/grad/budgaga/noaa/mrWorkdir/benchmark/jobs/jobDesc"+numOfReducers+".txt";
			RunJobs job= new RunJobs("client",port, jobFile, numOfJobs);
			job.startup(port);
			job.findRandomPeer();

		}catch(NumberFormatException e){
			System.err.println("Peer Node: the values of portnum must be integer");
		} catch (SocketException e) {
			System.err.println(e.getMessage());
		}
	}
}	
