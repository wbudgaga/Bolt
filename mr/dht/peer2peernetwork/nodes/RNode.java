package mr.dht.peer2peernetwork.nodes;

import java.io.IOException;

import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.handlers.MessageHandler;
import mr.dht.peer2peernetwork.threadpool.ThreadPoolManager;
import mr.dht.peer2peernetwork.util.UtilClass;
import mr.dht.peer2peernetwork.wireformates.FindRunningReducer;
import mr.dht.peer2peernetwork.wireformates.Lookup;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.dht.peer2peernetwork.wireformates.MessageFactory;
import mr.dht.peer2peernetwork.wireformates.StoreFileRequest;

public abstract class RNode extends Node{
	private PacketChannel pChannel;
	
	protected RNode(PeerData peerData, PacketChannel pChannel) throws IOException{
		super(peerData);
		setpChannel(pChannel);
	}
	
	public RNode(long id, String  name, String host, int port, PacketChannel pChannel) throws IOException{
		super(id, name);
		nodeData.setHost(host);
		nodeData.setPortNum(port);
		setpChannel(pChannel);
	}
	protected void startThreadPool(){
		threadPool 	= new ThreadPoolManager(2);
		threadPool.start();
	}
	
	public boolean isAlive(){return true;}

	public PacketChannel getpChannel() {
		return pChannel;
	}

	public void setpChannel(PacketChannel pChannel) {
		this.pChannel = pChannel;
	}

	public void sendMessage(Message msg) throws IOException{
		MessageHandler.sendMessage(pChannel, msg);
	}
	
	public static Lookup getLookupMSG(long key, int respHandlerID, PeerData sourceNode) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException{
		//Lookup lookupMSG = (Lookup) MessageFactory.getInstance().getMessageInstance(Message.LOOKUP);
		Lookup lookupMSG = new  Lookup();
		lookupMSG.setSourcePeer(RemotePeer.getPeerInfo(sourceNode));
		lookupMSG.setSrcPeerHandlerID(respHandlerID);
		lookupMSG.setQueryKey(key);
		lookupMSG.setHops(1);
		lookupMSG.setMsgUUID(UtilClass.getUUID());
		return lookupMSG;
	}

	public static FindRunningReducer getFindRunningReducerMSG(long key, long jobID, int taskID, PeerData sourceNode) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException{
		FindRunningReducer lookupMSG = new FindRunningReducer();
		lookupMSG.setSourcePeer(RemotePeer.getPeerInfo(sourceNode));
		lookupMSG.setQueryKey(key);
		lookupMSG.setHops(1);
		lookupMSG.setMsgUUID(UtilClass.getUUID());
		lookupMSG.setJobID(jobID);
		lookupMSG.setReduceID(taskID);
		return lookupMSG;
	}

/*	protected void sendMessage(Message msg, String host, int port) throws IOException{
		connectionManager.sendMessage(msg, host, port);
	}

	protected void sendMessage(Message msg) throws IOException{
		sendMessage(msg,nodeData.getHost(), nodeData.getPortNum());
	}
	
	protected Message sendReceiveMessage(Message msg){
		return connectionManager.sendReceiveMessage(msg,nodeData.getHost(), nodeData.getPortNum());
	}

	public boolean isAlive(){
		return ConnectionManager.isAlive(nodeData.getHost(), nodeData.getPortNum());
	}
*/}