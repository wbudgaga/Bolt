package mr.dht.peer2peernetwork.nodes;

import java.io.IOException;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import mr.dht.peer2peernetwork.datastructure.JobDescriptor;
import mr.dht.peer2peernetwork.handlers.MessageHandler;
import mr.dht.peer2peernetwork.handlers.connection.RetrivingAllPeersOnConnectingHandler;
import mr.dht.peer2peernetwork.util.UtilClass;
import mr.dht.peer2peernetwork.wireformates.DatasetMetaData;
import mr.dht.peer2peernetwork.wireformates.FileMetaData;
import mr.dht.peer2peernetwork.wireformates.GetAllPeers;
import mr.dht.peer2peernetwork.wireformates.GetDatasetMetaData;
import mr.dht.peer2peernetwork.wireformates.GetFileMetaData;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.dht.peer2peernetwork.wireformates.StartJobBatch;

public class JobSubmitter  extends Client{
	private String 			datasetName;
	private PeerCacher		peersCacher;
	private JobTasksSubmitter	tasksSubmitter;
	private volatile int 		numOfFiles; 
	private volatile int		numOfExecutions;
	private volatile int		numOfSubmittedJobs	= 0; 
	private JobDescriptor 		job;
	public static final Long	POISON  		= new Long(0);
	public static final Object	LUCK  			= new Object();
	private ArrayList<Long>     	chunck_IDs 		= new ArrayList<Long>();
	
	public JobSubmitter(String name, int port) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, NoSuchAlgorithmException {
		super(name, port);
		peersCacher 					= new PeerCacher();
	}
	
	public  synchronized void sendDataToPeer(long hashedKey, Message chunkMSG) throws IOException{
		RemotePeer[] chunkPeers 			= peersCacher.getPeer(hashedKey);
		chunkPeers[0].sendMessage(chunkMSG);
	}
	
	public  synchronized void sendMetaDataToPeer(long hashedKey, FileMetaData metaMSG) throws IOException{
		RemotePeer[] chunkPeers 			= peersCacher.getPeer(hashedKey);
		chunkPeers[0].sendMessage(metaMSG);
	}
	
//#############################################################################################	
	public void setDatasetMetaData(DatasetMetaData datasetMetaData) throws IOException{
		ArrayList<String> filesList = datasetMetaData.getFileNameList();
		numOfFiles = filesList.size();
		submitJob(1);
		for (String fileName: filesList){
			getFileMetaData(UtilClass.hashMKey(fileName));
/*			if (fileName!=null)
				break;
*/		}				
	}
	
	public void startJobBatch() throws IOException {
		StartJobBatch msg = new StartJobBatch();
		msg.setPeer(getNodeData());
		System.out.println("################# sending batch msgs");
		for (RemotePeer p: peersCacher.getCachedPeers()){
			p.sendMessage(msg);
		}
	}

	public void submitJob(int jobIDX){
		JobDescriptor job = getIterationJob(jobIDX);
		tasksSubmitter.setJob(job);
		threadPool.addTask(tasksSubmitter);
	}
	
	public void jobSubmitted() throws InterruptedException, IOException{
		++numOfSubmittedJobs;
		System.out.println(numOfSubmittedJobs+" of "+numOfExecutions+ " jobs are submitted ");
		if (numOfSubmittedJobs< numOfExecutions){
			submitJob(numOfSubmittedJobs+1);
			for (Long chunkID:chunck_IDs){
				tasksSubmitter.putChunkID(chunkID);
			}
			tasksSubmitter.putChunkID(POISON);
		}else
			startJobBatch();
	}
	
	public void setFileMetaData(FileMetaData fileMetaData) throws IOException{
		String 		baseName 	=  datasetName+"_"+fileMetaData.getFileName()+".%04d";
		long chunkID;
		try {
			for (int i=0;i<fileMetaData.getNumOfChunks(); ++i){
				chunkID = UtilClass.hashMKey(String.format(baseName,i));
				chunck_IDs.add(chunkID);
				tasksSubmitter.putChunkID(chunkID);
			}
			synchronized(LUCK) {
				--numOfFiles;
				System.out.println("### remaining metadata: "+numOfFiles);
				if (numOfFiles==0)
					tasksSubmitter.putChunkID(POISON);
			}
		} catch (InterruptedException e) {
				e.printStackTrace();
		}
	}
	public void getFileMetaData(long fileHK) throws IOException {
		RemotePeer[] datasetPeer = peersCacher.getPeer(fileHK);
		GetFileMetaData fileMetaDataRqust = new GetFileMetaData();
		fileMetaDataRqust.setPeer(getNodeData());
		fileMetaDataRqust.setHashedKey(fileHK);
		datasetPeer[0].sendMessage(fileMetaDataRqust);
	}
	
	public void setNumOfClusterPeers(int n){
		peersCacher.setNumOfExpectedPeers(n);
	}
//#############################################################################################
	public void getDatasetMetaData() throws IOException {
		long datasetHK = UtilClass.hashMKey(datasetName);
		RemotePeer[] datasetPeer = peersCacher.getPeer(datasetHK);
		GetDatasetMetaData datasetMetaDataRqust = new GetDatasetMetaData();
		datasetMetaDataRqust.setPeer(getNodeData());
		datasetMetaDataRqust.setHashedKey(datasetHK);
		datasetPeer[0].sendMessage(datasetMetaDataRqust);
	}
	
	public void handleRandomPeer(RemotePeer rp) throws IOException{
		peersCacher.addPeer(rp);
		if (peersCacher.AreAllPeersReceived())
			testMapTasks(1);
			//getDatasetMetaData();
	}

	private JobDescriptor getIterationJob(int jobNr){
		JobDescriptor job_n = new JobDescriptor();
		job_n.setJobName(job.getJobName()+jobNr);
		job_n.setJobID(UtilClass.hashMKey(job_n.getJobName()));
		job_n.setNumOfReducers(job.getNumOfReducers());
		job_n.setJobMapClassName(job.getJobMapClassName());
		job_n.setJobReduceClassName(job.getJobReduceClassName());
		job_n.setJobInputData(job.getJobInputData());
		job_n.setJobOutputName(job.getJobOutputName()+jobNr);
		return job_n;
	}
	
	public void testMapTasks(int jobIDX) throws IOException{
		JobDescriptor job = getIterationJob(jobIDX);
		tasksSubmitter.setJob(job);
		threadPool.addTask(tasksSubmitter);
		tasksSubmitter.submitMapTasks(12778640);
		tasksSubmitter.submitMapTasks(10728507);
		tasksSubmitter.submitMapTasks(13379588);
		tasksSubmitter.submitMapTasks(9038546);
		
		tasksSubmitter.submitMapTasks(9577429);
		tasksSubmitter.submitMapTasks(11032188);
		tasksSubmitter.submitMapTasks(12201862);
		tasksSubmitter.submitMapTasks(8621642);
		tasksSubmitter.submitMapTasks(11881584);
		tasksSubmitter.submitMapTasks(14143102);
		
		tasksSubmitter.submitMapTasks(12363202);
		tasksSubmitter.submitMapTasks(12297217);
		tasksSubmitter.submitMapTasks(11183951);
		tasksSubmitter.submitMapTasks(13440125);

		tasksSubmitter.submitMapTasks(43306690);
		tasksSubmitter.submitMapTasks(43743279);
		tasksSubmitter.submitMapTasks(44244334);
		tasksSubmitter.submitMapTasks(45297801);		
		tasksSubmitter.submitMapTasks(46342222);
		tasksSubmitter.submitMapTasks(46675319);
		tasksSubmitter.submitMapTasks(47188313);
		tasksSubmitter.submitMapTasks(47737122);
		tasksSubmitter.submitMapTasks(47921101);
		tasksSubmitter.submitMapTasks(49134762);

		tasksSubmitter.submitMapTasks(631091173);
		tasksSubmitter.submitMapTasks(631158963);
		tasksSubmitter.submitMapTasks(633803761);
		tasksSubmitter.submitMapTasks(633881133);
		tasksSubmitter.submitMapTasks(634665146);
		tasksSubmitter.submitMapTasks(635239615);
/*		tasksSubmitter.submitMapTasks(636518194);
		tasksSubmitter.submitMapTasks(636704998);
		tasksSubmitter.submitMapTasks(637031658);
*/
		startJobBatch();
	}

	public void submitJob(JobDescriptor job, int  numOfExecutions) throws InterruptedException {
		datasetName =job.getJobInputData();
		this.numOfExecutions = numOfExecutions;
		this.job = job;
		tasksSubmitter = new JobTasksSubmitter(this); 
		try {
			initiateConnectionManager(Setting.DISCOVER_HOST,Setting.DISCOVER_PORT, new RetrivingAllPeersOnConnectingHandler(this));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//#############################################################################################
	/*
	 * Main Method
	 */
	public static void main(String args[]) throws InstantiationException, IllegalAccessException, IOException, ClassNotFoundException {	
		if (args.length < 1) {
			System.err.println("Tasks submitter:  Usage:");
			System.err.println("         java mr.dht.peer2peernetwork.nodes.TasksSubmitter portnum, jobFile, #OfIterations");
		    return;
		}
		try{
			int 	port 		= Integer.parseInt(args[0]);			
			JobSubmitter ds 	= new JobSubmitter("tasksSubmitter",port);
			ds.startup(port);
			JobDescriptor job = JobDescriptor.loadfromFile(args[1]);
			ds.submitJob(job, Integer.parseInt(args[2]));

		}catch(NumberFormatException e){
			System.err.println("Peer Node: the values of portnum must be integer");
		} catch (SocketException e) {
			System.err.println(e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}	
