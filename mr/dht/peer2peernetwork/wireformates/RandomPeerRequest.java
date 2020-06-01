package mr.dht.peer2peernetwork.wireformates;

//this class represents a RegisterRequest message. It is used to pack and unpack the RegisterRequest message by using the methods in superclass
//the method handle() calls the method handle() in receiver to handle the message
public class RandomPeerRequest extends PeerInfo{
	
	public RandomPeerRequest() {
		super(RANDOM_PEER_REQUEST, RANDOM_PEER_REQUEST);
	}

	public String getMessageType() {
		return "RANDOM_PEER_REQUEST";
	}
}
