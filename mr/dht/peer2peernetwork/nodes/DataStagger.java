package mr.dht.peer2peernetwork.nodes;

import java.io.IOException;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import mr.dht.peer2peernetwork.handlers.connection.RetrivingAllPeersOnConnectingHandler;
import mr.dht.peer2peernetwork.threadpool.ThreadPoolManager;
import mr.dht.peer2peernetwork.util.UtilClass;
import mr.dht.peer2peernetwork.wireformates.FileMetaData;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.dht.peer2peernetwork.wireformates.StoreFileRequest;

public class DataStagger  extends Client{
	public final int HREAD_POOL_SIZE				= 5;//Runtime.getRuntime().availableProcessors();
	public final int STAGGER_POOL_SIZE				= 2 * THREAD_POOL_SIZE;
	private ThreadPoolManager taskThreadPool;  
	public final int chunksMSGQueueSize 				= 40000;
	public final int metaMSGQueueSize 				= 2 * STAGGER_POOL_SIZE;
	private BlockingQueue<Message>  chunksMSGs			= new ArrayBlockingQueue<Message>(chunksMSGQueueSize);
	private DatasetStagger dss;

	private PeerCacher		peersCacher;
	
	public DataStagger(String name, int port) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, NoSuchAlgorithmException {
		super(name, port);
		taskThreadPool 		= new ThreadPoolManager(THREAD_POOL_SIZE);
		peersCacher 		= new PeerCacher();
		initiatPools();
	}
	
	private void initiatPools(){
		for (int i=0; i < chunksMSGQueueSize; ++i){
			StoreFileRequest sfr = new StoreFileRequest();
			sfr.setReplicarionFactore(1/*Setting.REPLICATION_FACTOR*/);
			sfr.setBufferSize(FileStagger.SPLIT_PART_SIZE);
			sfr.setFileBytes(new byte[FileStagger.SPLIT_PART_SIZE]);
			returnObjToQueue(sfr);
		}
	}

	protected  void returnObjToQueue(Message sfr){
		chunksMSGs.offer(sfr);
	}
	protected synchronized StoreFileRequest getChunkMSG() throws InterruptedException{
		return (StoreFileRequest) chunksMSGs.take();
	}
	
	public  synchronized void sendDataToPeer(long hashedKey, Message chunkMSG) throws IOException{
		RemotePeer[] chunkPeers = peersCacher.getPeer(hashedKey);
		chunkPeers[0].sendMessage(chunkMSG);
		returnObjToQueue(chunkMSG);
	}
	public  synchronized void sendMetaDataToPeer(long hashedKey, FileMetaData metaMSG) throws IOException{
		RemotePeer[] chunkPeers = peersCacher.getPeer(hashedKey);
		chunkPeers[0].sendMessage(metaMSG);
	}

	public void setNumOfClusterPeers(int n){
		peersCacher.setNumOfExpectedPeers(n);
	}
//#############################################################################################
	public void submitDataset() {
		//remotePeer = rp;
		taskThreadPool.start();
		taskThreadPool.addTask(dss);
	}
	public void handleRandomPeer(RemotePeer rp){
		peersCacher.addPeer(rp);
		if (peersCacher.AreAllPeersReceived())
			submitDataset();
	}

	public void submitDataset(String datasetName, String  dataDir) throws InterruptedException {
		dss = new DatasetStagger(taskThreadPool, this, STAGGER_POOL_SIZE);
		dss.setDataset(datasetName, dataDir);
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
			System.err.println("Client Node:  Usage:");
			System.err.println("         java mr.dht.peer2peernetwork.nodes.DataStagger portnum datasetName, and dataLoc");
		    return;
		}
		try{
			int 	port 	= Integer.parseInt(args[0]);			
			DataStagger ds 	= new DataStagger("DataStagger",port);
			ds.startup(port);
			ds.submitDataset(args[1], args[2]);

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
