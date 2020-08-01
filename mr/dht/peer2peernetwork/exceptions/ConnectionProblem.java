package mr.dht.peer2peernetwork.exceptions;

public class ConnectionProblem extends Exception {
	public ConnectionProblem() { 
		super("ConnectionProblem"); 
	}
	public ConnectionProblem(String message) { 
		super(message); 
	}
}
