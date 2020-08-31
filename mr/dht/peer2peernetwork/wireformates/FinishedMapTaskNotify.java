package mr.dht.peer2peernetwork.wireformates;

import mr.dht.peer2peernetwork.nodes.PeerData;
import mr.dht.peer2peernetwork.util.ByteStream;

public class FinishedMapTaskNotify extends Message{
	private PeerInfo taskOwner;
	private long 	jobID;
	private int 	numOfFinishedMaps;
	private int 	numOfSentBuffers;
		
	public FinishedMapTaskNotify(int messageID, int handlerID) {
		super(messageID,handlerID);
	}
	public FinishedMapTaskNotify() {
		super(FINISHEDMAPTASKNOTIFY, FINISHEDMAPTASKNOTIFY);
	}
	private PeerInfo unpackPeerInfo(byte[] byteStream){
		byte[] bytes 		= readObjectBytes(byteStream);
		PeerInfo peer 		= new PeerInfo();
		peer.initiate(bytes);
		return peer;
	}
	
	@Override
	public void initiate(byte[] byteStream) {
		taskOwner 		= unpackPeerInfo(byteStream);
		jobID			= unpackLongField(byteStream);
		numOfFinishedMaps	= unpackIntField(byteStream);
		numOfSentBuffers	= unpackIntField(byteStream);
	}

	@Override
	protected byte[] packMessageBody() {
		byte[] objectBytes 	= taskOwner.packMessage();
		byte[] bytes		= ByteStream.join (ByteStream.intToByteArray(objectBytes.length),objectBytes);	
		bytes			= ByteStream.join (bytes, ByteStream.longToByteArray(jobID));
		bytes			= ByteStream.join (bytes, ByteStream.intToByteArray(numOfFinishedMaps));
		return ByteStream.join (bytes, ByteStream.intToByteArray(numOfSentBuffers));
	}
	
	@Override
	public String getMessageType() {
		return "StartMapTask";
	}
	
	public void setTaskOwner(PeerInfo taskOwner) {
		this.taskOwner 		= taskOwner;
	}

	public PeerInfo getPeer() {
		return taskOwner;
	}
	
	public long getJobID() {
		return jobID;
	}
	public void setJobID(long jobID) {
		this.jobID 		= jobID;
	}
	public int getNumOfFinishedMaps() {
		return numOfFinishedMaps;
	}
	public void setNumOfFinishedMaps(int n) {
		this.numOfFinishedMaps 	= n;
	}
	public int getNumOfSentBuffers() {
		return numOfSentBuffers;
	}
	public void setNumOfSentBuffers(int n) {
		this.numOfSentBuffers 	= n;
	}

		
	public static PeerInfo getPD(long id, String n, String h, int port){
		PeerData 	pd1 	= new  PeerData();
		pd1.setNickName(n);
		pd1.setHost(h);
		pd1.setPeerID(id);
		pd1.setPortNum(port);
		PeerInfo pi 		= new PeerInfo();
		pi.setPeer(pd1);
		return pi;
	}

	
	public static void main(String args[]) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		FinishedMapTaskNotify msg = new FinishedMapTaskNotify();
		PeerInfo taskOwner 	= getPD(1000, "peer", "peerHost", 5000);
		msg.setTaskOwner(taskOwner);
		msg.setJobID(32119041972l);
		msg.setNumOfFinishedMaps(13);
		msg.setNumOfSentBuffers(421);
		byte [] bt 		= msg.packMessage();
		
		MessageFactory f = MessageFactory.getInstance();
		
		FinishedMapTaskNotify ftr = (FinishedMapTaskNotify) f.createMessage(bt);
		System.out.println(ftr.getMsgUUID()+"   " + ftr.getNumOfFinishedMaps()+"   "+ftr.getNumOfSentBuffers());
		
	}
}
