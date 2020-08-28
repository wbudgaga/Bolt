package mr.dht.peer2peernetwork.wireformates;

import mr.dht.peer2peernetwork.nodes.PeerData;
import mr.dht.peer2peernetwork.util.ByteStream;
public class FindRunningReducer extends Lookup{
	private long 		jobID;
	private int 		reduceID;

	protected FindRunningReducer(int msgID, int handlerID) {
		super(msgID, handlerID);
	}
	
	public FindRunningReducer() {
		super(FIND_RUNNING_REDUCER, FIND_RUNNING_REDUCER);
		setSrcPeerHandlerID(REDUCERPEER);
	}
	
	@Override
	public void initiate(byte[] byteStream) {
		unpackMessage(byteStream);
		setJobID(unpackLongField(byteStream));
		setReduceID(unpackIntField(byteStream));
	}
	
	@Override
	protected byte[] packMessageBody() {
		byte[] bytes		= super.packMessageBody();
		bytes= ByteStream.join (bytes,ByteStream.longToByteArray(getJobID()));
		return ByteStream.join (bytes,ByteStream.intToByteArray(getReduceID()));
	}
	
	@Override
	public String getMessageType() {
		return "FindRunningReducer";
	}

	public int getReduceID() {
		return reduceID;
	}

	public void setReduceID(int reduceID) {
		this.reduceID 		= reduceID;
	}

	public long getJobID() {
		return jobID;
	}

	public void setJobID(long jobID) {
		this.jobID 		= jobID;
	}
	
	public static void main(String[] a){
		FindRunningReducer f = new FindRunningReducer();
		f.setJobID(11);
		f.setQueryKey(1234618);
		PeerInfo sourcePeer = new PeerInfo();
		PeerData pd = new PeerData();
		pd.setHost("red-rock");
		pd.setNickName("walid");
		pd.setPeerID(102);
		pd.setPortNum(1201);
		sourcePeer.setPeer(pd);
		f.setSourcePeer(sourcePeer);
		byte[] b = f.packMessage();
		FindRunningReducer f1 = new FindRunningReducer();
		f1.initiate(b);
		System.out.println(f1.getHandlerID()+" "+f1.getHops()+" "+f1.getJobID()+" "+f1.getMessageID()+" "+f1.getMessageID()+" "+f1.getMessageType()+" "+f1.getMsgUUID()+" "+f1.getQueryKey()+" "+f1.getSourcePeer().getPeer().getHost());
		
	}
}
