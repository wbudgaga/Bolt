package mr.dht.peer2peernetwork.wireformates;

//this class represents a RegisterRequest message. It is used to pack and unpack the RegisterRequest message by using the methods in superclass
//the method handle() calls the method handle() in receiver to handle the message
public class RegisterRequest extends Request{
	
	public RegisterRequest() {
		super(REGISTER_REQUEST, REGISTER_REQUEST);
	}
	
	public RegisterRequest(int msgID, int handlerID) {
		super(msgID, handlerID);
	}
	
	public String getMessageType() {
		return "REGISTER_REQUEST";
	}
}
