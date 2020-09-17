package mr.dht.peer2peernetwork.wireformates;

import mr.dht.peer2peernetwork.handlers.HandlerTypes;
import mr.dht.peer2peernetwork.nodes.PeerData;
import mr.dht.peer2peernetwork.util.ByteStream;

public class TaskData extends StartMapTask{
	protected TaskData(int msgID, int handlerID) {
		super(msgID, handlerID);
	}
	
	public TaskData() {
		super(TASKDATA, TASKDATA);
	}
	
	@Override
	public void initiate(byte[] byteStream) {
		super.initiate(byteStream);
	}
	
	@Override
	protected byte[] packMessageBody() {
		setOutputName("");
		setTaskClassName("");
		return super.packMessageBody();
	}
	
	@Override
	public String getMessageType() {
		return "KeyValueTaskData";
	}
	
	public static PeerInfo getPD(long id, String n, String h, int port){
		PeerData pd1 			= new  PeerData();
		pd1.setNickName(n);
		pd1.setHost(h);
		pd1.setPeerID(id);
		pd1.setPortNum(port);
		PeerInfo pi 			= new PeerInfo();
		pi.setPeer(pd1);
		return pi;
	}

	
	public static void main(String args[]) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		TaskData msg 			= new TaskData();
		PeerInfo taskOwner 		= getPD(1000, "peer", "peerHost", 5000);
		msg.setTaskOwner(taskOwner);
		msg.setJobID(32119041972l);
		msg.setTaskID(13);
/*		msg.setDataInputPath("/s/chopin/b/grad/budgaga/workspaceALL/workDIr/bin");
		msg.setTaskClassName("job1Map");
*/		byte [] bt = msg.packMessage();
		
		MessageFactory f = MessageFactory.getInstance();
		
		TaskData ftr = (TaskData) f.createMessage(bt);
		System.out.println(ftr.getJobID()+"   "+ftr.getTaskID()+"==="+ftr.getOutputName()+"===");
		
	}

}
