package mr.dht.peer2peernetwork.wireformates;

import java.net.Socket;

import mr.dht.peer2peernetwork.handlers.MessageHandler;

public class Successor extends PeerInfo{
	public Successor() {
		super(SUCCESSOR, SUCCESSOR);
	}
}
