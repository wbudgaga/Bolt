package mr.dht.peer2peernetwork.wireformates;

import java.net.Socket;

import mr.dht.peer2peernetwork.handlers.MessageHandler;

public class GetPredecessorResponse extends PeerInfo{
	public GetPredecessorResponse() {
		super(GET_PREDECESSOR_RESPONSE, GET_PREDECESSOR_RESPONSE);
	}
}
