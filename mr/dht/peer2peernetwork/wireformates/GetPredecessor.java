package mr.dht.peer2peernetwork.wireformates;

import mr.dht.peer2peernetwork.nodes.PeerData;

public class GetPredecessor extends PeerInfo{
	public GetPredecessor() {
		super(GET_PREDECESSOR, GET_PREDECESSOR);
	}
}
