package mr.dht.peer2peernetwork.wireformates;

import mr.dht.peer2peernetwork.nodes.PeerData;
import mr.dht.peer2peernetwork.util.ByteStream;

public class StartMapTask extends Message{
	private PeerInfo taskOwner;
	private long 	jobID;
	private long 	taskID;
	private int 	numOfReducers;
	private String 	taskClassName;
	private String 	outputName;
		
	public StartMapTask(int messageID, int handlerID) {
		super(messageID,handlerID);
	}
	
	public StartMapTask() {
		super(START_MAPTASK, START_MAPTASK);
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
		taskID			= unpackLongField(byteStream);
		numOfReducers		= unpackIntField(byteStream);
		taskClassName		= unpackStringField(byteStream);
		outputName		= unpackStringField(byteStream);
	}

	@Override
	protected byte[] packMessageBody() {
		byte[] objectBytes 	= taskOwner.packMessage();
		byte[] bytes		= ByteStream.join (ByteStream.intToByteArray(objectBytes.length),objectBytes);
		bytes			= ByteStream.join (bytes, ByteStream.longToByteArray(jobID));
		bytes			= ByteStream.join (bytes, ByteStream.longToByteArray(taskID));
		bytes			= ByteStream.join (bytes, ByteStream.intToByteArray(numOfReducers));
		bytes			= ByteStream.join (bytes, ByteStream.packString(taskClassName));
		return ByteStream.join (bytes, ByteStream.packString(outputName));
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
	public long getTaskID() {
		return taskID;
	}
	public void setTaskID(long taskID) {
		this.taskID 		= taskID;
	}
	public int getNumOfReducers() {
		return numOfReducers;
	}
	public void setNumOfReducers(int numOfReducers) {
		this.numOfReducers = numOfReducers;
	}
	
	public String getTaskClassName() {
		return taskClassName;
	}
	public void setTaskClassName(String taskClassPath) {
		this.taskClassName = taskClassPath;
	}

	
	public static PeerInfo getPD(long id, String n, String h, int port){
		PeerData 	pd1 = new  PeerData();
		pd1.setNickName(n);
		pd1.setHost(h);
		pd1.setPeerID(id);
		pd1.setPortNum(port);
		PeerInfo pi = new PeerInfo();
		pi.setPeer(pd1);
		return pi;
	}

	
	public static void main(String args[]) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		StartMapTask msg = new StartMapTask();
		PeerInfo taskOwner = getPD(1000, "peer", "peerHost", 5000);
		msg.setTaskOwner(taskOwner);
		msg.setJobID(32119041972l);
		msg.setTaskID(13);
		msg.setTaskClassName("job1Map");
		byte [] bt = msg.packMessage();
		
		MessageFactory f = MessageFactory.getInstance();
		
		StartMapTask ftr = (StartMapTask) f.createMessage(bt);
		System.out.println(ftr.getTaskID()+ "  "+ftr.getJobID());
		
	}
	public String getOutputName() {
		return outputName;
	}
	public void setOutputName(String outputName) {
		this.outputName = outputName;
	}
}
