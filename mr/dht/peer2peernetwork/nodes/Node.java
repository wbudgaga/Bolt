package mr.dht.peer2peernetwork.nodes;

import java.io.IOException;

import mr.communication.io.SelectorThread;
import mr.dht.peer2peernetwork.threadpool.ThreadPoolManager;

public abstract class Node {
	protected PeerData		nodeData;
	protected ThreadPoolManager	threadPool;

	protected Node(PeerData peerData) throws IOException{
		setNodeData(peerData);
		startThreadPool();
	}

	protected Node(long id, String  name) throws IOException{
		this.nodeData 						= new PeerData();
		this.nodeData.setNickName(name);
		this.nodeData.setPeerID(id);
		startThreadPool();
	}

	protected void startThreadPool(){
		threadPool 						= new ThreadPoolManager(Setting.THREADPOOL_SIZE);
		threadPool.start();
	}
	
	public ThreadPoolManager getThreadPoolManager(){
		return threadPool;
	}

	public PeerData getNodeData() {
		return nodeData;
	}
	
	public void setNodeData(PeerData nodeData) {
		this.nodeData = nodeData;
	}

	public long getID() {
		return nodeData.getPeerID();
	}
	
	public void exit(){
		System.exit(-1);
	}
}
