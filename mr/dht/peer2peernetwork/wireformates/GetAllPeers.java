package mr.dht.peer2peernetwork.wireformates;

//this class represents a RegisterRequest message. It is used to pack and unpack the RegisterRequest message by using the methods in superclass
//the method handle() calls the method handle() in receiver to handle the message
public class GetAllPeers extends RegisterRequest{
	public GetAllPeers() {
		super(GET_ALLPEERS, GET_ALLPEERS);
	}

	public String getMessageType() {
		return "GET_ALLPEERS";
	}
}
