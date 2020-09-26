package mr.dht.peer2peernetwork.wireformates;

//this class represents a RegisterRequest message. It is used to pack and unpack the RegisterRequest message by using the methods in superclass
//the method handle() calls the method handle() in receiver to handle the message
public class StartJobBatch extends Request{	
	public StartJobBatch() {
		super(START_JOB_BATCH, START_JOB_BATCH);
	}

	public String getMessageType() {
		return "START_JOB_BATCH";
	}
}
