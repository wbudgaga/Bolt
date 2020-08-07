package mr.dht.peer2peernetwork.nodes;

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
import mr.dht.peer2peernetwork.handlers.StartJobBatchHandler;
import mr.dht.peer2peernetwork.handlers.connection.RandomPeerRequestConnectingHandler;
import mr.dht.peer2peernetwork.wireformates.DatasetMetaData;
import mr.dht.peer2peernetwork.wireformates.FileMetaData;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.dht.peer2peernetwork.wireformates.PeerInfo;
import mr.dht.peer2peernetwork.wireformates.StartJobBatch;
import mr.dht.peer2peernetwork.wireformates.StartMapTask;
import mr.dht.peer2peernetwork.wireformates.StartReduceTask;
import mr.dht.peer2peernetwork.wireformates.TextNumTaskData;
import mr.resourcemanagement.datapartitioning.ModPartitioner;
import mr.resourcemanagement.execution.mrtasks.management.DataRouter;
import mr.resourcemanagement.execution.mrtasks.management.JobTasksManager;

public class Client  extends LNode implements CMDLineInterface{
	public RemotePeer remotePeer;
	private final Object LOCK 							= new Object();
	private ConcurrentHashMap <String,Long> pendingJobs 				= new ConcurrentHashMap<String,Long>();//<jobID+"_"+TaskID,rKey>
	private ConcurrentHashMap <Long,BlockingQueue<StartMapTask>> pendingJobTasks 	= new ConcurrentHashMap<Long,BlockingQueue<StartMapTask>>();//<jobID,<tasks>>
	protected ConcurrentHashMap <Long,RemotePeer> cachedPeers 			= new ConcurrentHashMap<Long,RemotePeer>();
	private ConcurrentHashMap <Long,TextNumTaskData> pendingTasksData 		= new ConcurrentHashMap<Long,TextNumTaskData>();
	public boolean 	allJobSubmitted = false;

	
	public Client(String name, int port) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
		super(name, port);
	}
			
	public void handleRandomPeer(RemotePeer rp) throws InvalidFingerTableEntry, IOException{
		remotePeer = rp;
		System.out.println("random peer ("+rp.getID()+") has been received###########");
	}

	public void print(){
		PeerData.printHeader();
		remotePeer.getNodeData().print();
		PeerData.printRowSeparator();
	}
	
	public boolean startup( int port) throws InstantiationException, IllegalAccessException, IOException, ClassNotFoundException{
		startListining(port);
		return true;
	}
	
	public void lookup(long key, int respHandlerID){
		try {
			remotePeer.lookup(key, respHandlerID, nodeData);
		} catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void findRandomPeer() {
		try {
			initiateConnectionManager(Setting.DISCOVER_HOST,Setting.DISCOVER_PORT, new RandomPeerRequestConnectingHandler(this));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void setNumOfClusterPeers(int n){}
	public void setDatasetMetaData(DatasetMetaData datasetMetaData) throws IOException{}
	public void setFileMetaData(FileMetaData fileMetaData) throws IOException{}
//#############################################################################################
	

	//=============================================================
		private String getJobTaskID(long jobID, int taskID, String taskType){
			return jobID+"_"+taskType+taskID;
		}
	//=============================================================		
	@Override
	public void submitJob(JobDescriptor job) {
	}
		
	public void startJobBatch() throws IOException{
		StartJobBatch msg = new StartJobBatch();
		msg.setPeer(getNodeData());
		for (RemotePeer p: cachedPeers.values()){
			p.sendMessage(msg);
		}
	}
	public synchronized void handleQueryResult(long queryKey, RemotePeer rp) throws InvalidFingerTableEntry, IOException{
	}
//##############################################################################################	

	@Override
	public void submitTestBuffer(HashMap<String, Long[]> outputBuf, long jobID, int numOfReducers, int reducerIDX) {
/*		try {// replace this method : lookup->key(buffer)-exe withn this method: create msg and its handler e.g: msg: ResponsblePeer4Buff ->peerID-> handler:ResponsblePeer4Buff
			long reducersKey = DataRouter.getRountingKey(new ModPartitioner<>(), numOfReducers, jobID, reducerIDX);
			PeerInfo taskOwner = TextNumTaskData.getPD(1000, "peer", "peerHost", 5000);
			TextNumTaskData msg = new TextNumTaskData();
			msg.setDataBuf(outputBuf);
			msg.setTaskOwner(taskOwner);
			msg.setJobID(jobID);
			msg.setTaskID(reducerIDX);
			pendingTasksData.put(reducersKey, msg);
			lookup(reducersKey, Message.SUBMITDATA_QResult);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
*/	}

	/*
	 * Main Method
	 */
	public static void main(String args[]) throws InstantiationException, IllegalAccessException, IOException, ClassNotFoundException {
		Client peer;
		if (args.length < 1) {
			System.err.println("Client Node:  Usage:");
			System.err.println("         java mr.dht.peer2peernetwork.nodes.Client portnum");
		    return;
		}
		try{
			int 	port 		= Integer.parseInt(args[0]);
			peer = new Client("client",port);
			peer.startup(port);
			ClientCMDThread cl = new ClientCMDThread(peer);
			cl.start();

		}catch(NumberFormatException e){
			System.err.println("Peer Node: the values of portnum must be integer");
		} catch (SocketException e) {
			System.err.println(e.getMessage());
		}
	}

	
	@Override
	public void socketConnected(Acceptor acceptor, SocketChannel sc) {
		System.out.println("["+ acceptor + "] connection received: " + sc);
		try {
	      sc.socket().setReceiveBufferSize(2*1024);
	      sc.socket().setSendBufferSize(2*1024);
	      // The contructor enables reading automatically.
	      PacketChannel pc = new PacketChannel(sc, selector, new MultiMSGDecoder(), new ClientMessageHandler(this));
	      pc.resumeReading();
		} catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
		      e.printStackTrace();
	    }
	}

	@Override
	public void socketError(Acceptor acceptor, Exception ex) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void socketException(PacketChannel pc, Exception ex) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void socketDisconnected(PacketChannel pc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void submitDataSet(String name, String loc) {
/*		DataStagger ds = new DataStagger(name, getNodeData().getPortNum());
		ds.submitDataset(loc, name);
*/		
	}
}
	
