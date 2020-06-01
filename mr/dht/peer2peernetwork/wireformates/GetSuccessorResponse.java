package mr.dht.peer2peernetwork.wireformates;

import java.net.Socket;

import mr.dht.peer2peernetwork.handlers.MessageHandler;

public class GetSuccessorResponse extends PeerInfo{
	public GetSuccessorResponse() {
		super(GET_SUCCESSOR_RESPONSE, GET_SUCCESSOR_RESPONSE);
	}
}
