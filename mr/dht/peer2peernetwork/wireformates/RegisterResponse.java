package mr.dht.peer2peernetwork.wireformates;


public class RegisterResponse 	extends Response{
	
	public RegisterResponse() {
		super(REGISTER_RESPONSE, REGISTER_RESPONSE);
	}

	public String getMessageType() {
		return "REGISTER_RESPONSE";
	}

}
